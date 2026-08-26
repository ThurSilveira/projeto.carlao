from sqlalchemy.orm import Session
from app.models import Escala, Evento
from app.schemas import EventoIn, EventoOut
from app.services import auditoria_service

_TIPOS_VALIDOS = {"MISSA_PAROQUIAL", "MISSA_ESPECIAL", "RETIRO", "BATIZADO", "CASAMENTO", "ADORACAO", "OUTRO"}


def _sync_calendar_scales(db: Session, event: Evento) -> None:
    from app.services import google_calendar_service

    for scale_id, in db.query(Escala.id).filter(Escala.evento_id == event.id).all():
        try:
            google_calendar_service.sync_scale(db, scale_id)
        except Exception as exc:
            db.rollback()
            auditoria_service.registrar(
                db,
                "Google Calendar",
                "FALHA",
                None,
                f"Escala {scale_id} — {str(exc)[:500]}",
            )
            db.commit()


def _to_out(e: Evento) -> EventoOut:
    return EventoOut.model_validate(e)


def _preencher(evento: Evento, data: EventoIn) -> None:
    evento.nome = data.nome
    evento.data = data.data
    evento.horario = data.horario
    evento.local = data.local
    evento.max_ministros = data.max_ministros if data.max_ministros is not None else 6
    evento.cancelado = data.cancelado
    evento.tipo_especificado = data.tipo_especificado
    tipo = data.tipo_evento or "MISSA_PAROQUIAL"
    evento.tipo_evento = tipo if tipo in _TIPOS_VALIDOS else "OUTRO"


def listar(db: Session) -> list[EventoOut]:
    return [_to_out(e) for e in db.query(Evento).filter(Evento.cancelado == False).all()]


def obter(db: Session, evento_id: int) -> EventoOut | None:
    e = db.get(Evento, evento_id)
    return _to_out(e) if e else None


def _evento_detalhes(evento: Evento) -> str:
    horario = evento.horario or "horário não informado"
    local = evento.local or "local não informado"
    return f"{evento.nome} — {evento.data} {horario} @ {local}"


def criar(db: Session, data: EventoIn) -> EventoOut:
    evento = Evento()
    _preencher(evento, data)
    db.add(evento)
    db.flush()
    auditoria_service.registrar(db, "Evento", "CRIADO", None, f"CRIADO — {_evento_detalhes(evento)}")
    db.commit()
    db.refresh(evento)
    return _to_out(evento)


def atualizar(db: Session, evento_id: int, data: EventoIn) -> EventoOut | None:
    evento = db.get(Evento, evento_id)
    if not evento:
        return None
    prev = evento.nome
    _preencher(evento, data)
    auditoria_service.registrar(db, "Evento", "ATUALIZADO", prev, f"ATUALIZADO — {_evento_detalhes(evento)}")
    db.commit()
    _sync_calendar_scales(db, evento)
    db.refresh(evento)
    return _to_out(evento)


def cancelar(db: Session, evento_id: int) -> EventoOut | None:
    evento = db.get(Evento, evento_id)
    if not evento:
        return None
    evento.cancelado = True
    auditoria_service.registrar(db, "Evento", "CANCELADO", "ATIVO", f"CANCELADO — {_evento_detalhes(evento)}")
    db.commit()
    _sync_calendar_scales(db, evento)
    db.refresh(evento)
    return _to_out(evento)


def deletar(db: Session, evento_id: int) -> None:
    evento = db.get(Evento, evento_id)
    if evento:
        from app.services import google_calendar_service

        for scale_id, in db.query(Escala.id).filter(Escala.evento_id == evento.id).all():
            result = google_calendar_service.cancel_scale_events(db, scale_id)
            if result.falhas or result.pendentes:
                raise ValueError(
                    "Não foi possível remover todas as notificações do Google Calendar; o evento não foi excluído."
                )
        auditoria_service.registrar(db, "Evento", "DELETADO", evento.nome, f"DELETADO — {_evento_detalhes(evento)}")
        db.delete(evento)
        db.commit()
