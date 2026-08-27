import os
from datetime import date, datetime, time, timedelta
from zoneinfo import ZoneInfo, ZoneInfoNotFoundError

from sqlalchemy.orm import Session

from app.auth_profiles import MINISTRO
from app.models import Escala, EscalaMinistro, Evento, Feedback, Indisponibilidade, Ministro, Usuario
from app.schemas import (
    CalendarioMinistroEventoOut,
    FeedbackMinistroIn,
    FeedbackMinistroOut,
    IndisponibilidadeIn,
    IndisponibilidadeOut,
    MinistroPortalOut,
)
from app.services import auditoria_service


VISIBLE_SCALE_STATUSES = {"APROVADA", "CONFIRMADA"}


def _event_timezone() -> ZoneInfo:
    try:
        return ZoneInfo(os.getenv("EVENT_TIMEZONE", "America/Sao_Paulo"))
    except ZoneInfoNotFoundError as exc:
        raise ValueError("O fuso horário configurado para os eventos é inválido.") from exc


def _event_duration() -> int:
    try:
        duration = int(os.getenv("EVENT_DURATION_MINUTES", "120"))
    except ValueError as exc:
        raise ValueError("A duração configurada para os eventos é inválida.") from exc
    if not 1 <= duration <= 1440:
        raise ValueError("A duração dos eventos deve estar entre 1 e 1440 minutos.")
    return duration


def _event_finished(event: Evento) -> bool:
    try:
        event_time = time.fromisoformat(event.horario)
    except (TypeError, ValueError) as exc:
        raise ValueError("O evento possui horário inválido.") from exc
    timezone = _event_timezone()
    event_end = datetime.combine(event.data, event_time, timezone) + timedelta(minutes=_event_duration())
    return datetime.now(timezone) >= event_end


def _today() -> date:
    return datetime.now(_event_timezone()).date()


def minister_for_user(user: Usuario) -> Ministro:
    if not user.acesso or user.acesso.perfil != MINISTRO:
        raise PermissionError("Esta área é exclusiva para ministros.")
    link = user.vinculo_ministro
    if not link or not link.ministro:
        raise PermissionError("Usuário sem cadastro de ministro vinculado.")
    if not link.ministro.ativo:
        raise PermissionError("O cadastro do ministro está inativo.")
    return link.ministro


def profile(user: Usuario) -> MinistroPortalOut:
    minister = minister_for_user(user)
    return MinistroPortalOut(
        id=minister.id,
        nome=minister.nome,
        email=minister.email,
        funcao=minister.funcao,
    )


def calendar(
    db: Session,
    minister: Ministro,
    start_date: date,
    end_date: date,
) -> list[CalendarioMinistroEventoOut]:
    if end_date < start_date:
        raise ValueError("A data final deve ser igual ou posterior à data inicial.")
    if (end_date - start_date).days > 366:
        raise ValueError("O período máximo do calendário é de 366 dias.")

    events = (
        db.query(Evento)
        .filter(Evento.data >= start_date, Evento.data <= end_date)
        .order_by(Evento.data.asc(), Evento.horario.asc(), Evento.id.asc())
        .all()
    )
    if not events:
        return []

    event_ids = [event.id for event in events]
    assignments = (
        db.query(EscalaMinistro)
        .join(Escala)
        .filter(
            EscalaMinistro.ministro_id == minister.id,
            EscalaMinistro.substituido.is_(False),
            Escala.evento_id.in_(event_ids),
            Escala.status.in_(VISIBLE_SCALE_STATUSES),
        )
        .order_by(Escala.id.desc())
        .all()
    )
    assignment_by_event: dict[int, EscalaMinistro] = {}
    for assignment in assignments:
        assignment_by_event.setdefault(assignment.escala.evento_id, assignment)

    feedback_event_ids = {
        row[0]
        for row in db.query(Feedback.evento_id).filter(
            Feedback.ministro_id == minister.id,
            Feedback.evento_id.in_(event_ids),
        ).all()
    }

    output: list[CalendarioMinistroEventoOut] = []
    for event in events:
        assignment = assignment_by_event.get(event.id)
        scale = assignment.escala if assignment else None
        output.append(
            CalendarioMinistroEventoOut(
                evento_id=event.id,
                nome=event.nome,
                data=event.data,
                horario=event.horario,
                tipo_evento=event.tipo_evento,
                local=event.local,
                cancelado=event.cancelado,
                escala_id=scale.id if scale else None,
                status_escala=scale.status if scale else None,
                escalado=assignment is not None,
                funcao_ministro=minister.funcao if assignment else None,
                confirmacao_ministro=bool(assignment and assignment.confirmacao_ministro),
                feedback_enviado=event.id in feedback_event_ids,
                feedback_disponivel=bool(
                    assignment
                    and not event.cancelado
                    and event.id not in feedback_event_ids
                    and _event_finished(event)
                ),
            )
        )
    return output


def _validate_unavailability(data: IndisponibilidadeIn) -> None:
    if data.data < _today():
        raise ValueError("Não é possível cadastrar indisponibilidade em uma data passada.")
    if data.horario_fim and not data.horario_inicio:
        raise ValueError("Informe o horário inicial quando houver horário final.")
    if data.horario_inicio and data.horario_fim and data.horario_fim < data.horario_inicio:
        raise ValueError("O horário final deve ser igual ou posterior ao horário inicial.")


def list_unavailability(db: Session, minister: Ministro) -> list[IndisponibilidadeOut]:
    rows = (
        db.query(Indisponibilidade)
        .filter(Indisponibilidade.ministro_id == minister.id)
        .order_by(Indisponibilidade.data.asc(), Indisponibilidade.horario_inicio.asc())
        .all()
    )
    return [IndisponibilidadeOut.model_validate(row) for row in rows]


def create_unavailability(
    db: Session,
    minister: Ministro,
    user: Usuario,
    data: IndisponibilidadeIn,
) -> IndisponibilidadeOut:
    _validate_unavailability(data)
    entity = Indisponibilidade(
        ministro_id=minister.id,
        data=data.data,
        horario_inicio=data.horario_inicio,
        horario_fim=data.horario_fim,
        motivo=data.motivo.strip() if data.motivo else None,
    )
    db.add(entity)
    db.flush()
    auditoria_service.registrar(
        db, "Indisponibilidade", "CRIADO", None, f"Ministro {minister.id} — {data.data}", str(user.id)
    )
    db.commit()
    db.refresh(entity)
    return IndisponibilidadeOut.model_validate(entity)


def _owned_unavailability(db: Session, minister: Ministro, unavailability_id: int) -> Indisponibilidade:
    entity = db.query(Indisponibilidade).filter(
        Indisponibilidade.id == unavailability_id,
        Indisponibilidade.ministro_id == minister.id,
    ).first()
    if not entity:
        raise ValueError("Indisponibilidade não encontrada.")
    return entity


def update_unavailability(
    db: Session,
    minister: Ministro,
    user: Usuario,
    unavailability_id: int,
    data: IndisponibilidadeIn,
) -> IndisponibilidadeOut:
    _validate_unavailability(data)
    entity = _owned_unavailability(db, minister, unavailability_id)
    previous = f"{entity.data} {entity.horario_inicio or 'dia inteiro'}"
    entity.data = data.data
    entity.horario_inicio = data.horario_inicio
    entity.horario_fim = data.horario_fim
    entity.motivo = data.motivo.strip() if data.motivo else None
    auditoria_service.registrar(
        db,
        "Indisponibilidade",
        "ATUALIZADO",
        previous,
        f"{entity.data} {entity.horario_inicio or 'dia inteiro'}",
        str(user.id),
    )
    db.commit()
    db.refresh(entity)
    return IndisponibilidadeOut.model_validate(entity)


def delete_unavailability(
    db: Session,
    minister: Ministro,
    user: Usuario,
    unavailability_id: int,
) -> None:
    entity = _owned_unavailability(db, minister, unavailability_id)
    description = f"Ministro {minister.id} — {entity.data}"
    db.delete(entity)
    auditoria_service.registrar(
        db, "Indisponibilidade", "DELETADO", description, None, str(user.id)
    )
    db.commit()


def _feedback_out(feedback: Feedback) -> FeedbackMinistroOut:
    event = feedback.evento
    return FeedbackMinistroOut(
        id=feedback.id,
        ministro_id=feedback.ministro_id,
        evento_id=feedback.evento_id,
        nota=feedback.nota,
        comentario=feedback.comentario,
        data_envio=feedback.data_envio,
        status=feedback.status,
        resposta=feedback.resposta,
        evento_nome=event.nome,
        evento_data=event.data,
        evento_horario=event.horario,
        evento_local=event.local,
    )


def list_feedback(db: Session, minister: Ministro) -> list[FeedbackMinistroOut]:
    rows = db.query(Feedback).filter(
        Feedback.ministro_id == minister.id
    ).order_by(Feedback.data_envio.desc()).all()
    return [_feedback_out(row) for row in rows]


def create_feedback(
    db: Session,
    minister: Ministro,
    user: Usuario,
    data: FeedbackMinistroIn,
) -> FeedbackMinistroOut:
    event = db.get(Evento, data.evento_id)
    if not event:
        raise ValueError("Evento não encontrado.")
    if event.cancelado:
        raise ValueError("Não é possível enviar feedback para um evento cancelado.")
    if not _event_finished(event):
        raise ValueError("O feedback só pode ser enviado após a realização do evento.")

    participated = db.query(EscalaMinistro.id).join(Escala).filter(
        EscalaMinistro.ministro_id == minister.id,
        EscalaMinistro.substituido.is_(False),
        Escala.evento_id == event.id,
        Escala.status.in_(VISIBLE_SCALE_STATUSES),
    ).first()
    if not participated:
        raise ValueError("O feedback está disponível apenas para eventos da sua escala.")
    if db.query(Feedback.id).filter(
        Feedback.ministro_id == minister.id,
        Feedback.evento_id == event.id,
    ).first():
        raise ValueError("Você já enviou feedback para este evento.")

    feedback = Feedback(
        ministro_id=minister.id,
        evento_id=event.id,
        nota=data.nota,
        comentario=data.comentario.strip() if data.comentario else None,
        status="PENDENTE",
    )
    db.add(feedback)
    db.flush()
    auditoria_service.registrar(
        db, "Feedback", "CRIADO", None, f"Evento {event.id} — ministro {minister.id}", str(user.id)
    )
    db.commit()
    db.refresh(feedback)
    return _feedback_out(feedback)
