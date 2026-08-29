from sqlalchemy.orm import Session
from app.models import Ministro, EscalaMinistro, Escala, Evento, Usuario
from app.schemas import MinistroIn, MinistroOut, IndisponibilidadeOut
from app.services import auditoria_service, auth_service

_FUNCOES_VALIDAS = {"EUCARISTIA", "LEITURA", "ACOLHIMENTO", "MUSICA", "CATEQUESE", "ADORACAO", "OUTRO"}


def _sync_calendar_scales(db: Session, minister_id: int) -> None:
    from app.services import google_calendar_service

    scale_ids = (
        db.query(Escala.id)
        .join(EscalaMinistro)
        .filter(
            EscalaMinistro.ministro_id == minister_id,
            EscalaMinistro.substituido.is_(False),
        )
        .distinct()
        .all()
    )
    for scale_id, in scale_ids:
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


def _to_out(db: Session, m: Ministro) -> MinistroOut:
    escalas_agendadas = [
        row[0]
        for row in (
            db.query(Evento.data)
            .join(Escala, Escala.evento_id == Evento.id)
            .join(EscalaMinistro, EscalaMinistro.escala_id == Escala.id)
            .filter(EscalaMinistro.ministro_id == m.id)
            .all()
        )
    ]
    return MinistroOut(
        id=m.id,
        nome=m.nome,
        email=m.email,
        telefone=m.telefone,
        data_nascimento=m.data_nascimento,
        observacoes=m.observacoes,
        ativo=m.ativo,
        visitas_ao_infermo=m.visitas_ao_infermo,
        status_curso=m.status_curso,
        escalas_mes=m.escalas_mes,
        funcao=m.funcao,
        funcao_especificada=m.funcao_especificada,
        indisponibilidades=[IndisponibilidadeOut.model_validate(i) for i in m.indisponibilidades],
        escalas_agendadas=escalas_agendadas,
    )


def _preencher(ministro: Ministro, data: MinistroIn) -> None:
    ministro.nome = data.nome
    ministro.email = data.email
    ministro.telefone = data.telefone
    ministro.data_nascimento = data.data_nascimento
    ministro.observacoes = data.observacoes
    ministro.ativo = data.ativo
    ministro.visitas_ao_infermo = data.visitas_ao_infermo
    ministro.status_curso = data.status_curso
    ministro.funcao_especificada = data.funcao_especificada
    funcao = data.funcao or "LEITURA"
    ministro.funcao = funcao if funcao in _FUNCOES_VALIDAS else "LEITURA"


def listar(db: Session) -> list[MinistroOut]:
    return [_to_out(db, m) for m in db.query(Ministro).all()]


def obter(db: Session, ministro_id: int) -> MinistroOut | None:
    m = db.get(Ministro, ministro_id)
    return _to_out(db, m) if m else None


def criar(db: Session, data: MinistroIn) -> MinistroOut:
    normalized_email = auth_service.validate_email(data.email)
    if db.query(Ministro).filter(Ministro.email == normalized_email).first():
        raise ValueError("Já existe um ministro com este e-mail.")
    if db.query(Usuario).filter(Usuario.email == normalized_email).first():
        raise ValueError("Já existe um usuário com este e-mail.")
    data.email = normalized_email
    m = Ministro()
    _preencher(m, data)
    db.add(m)
    db.flush()
    auth_service.create_minister_access(db, m)
    auditoria_service.registrar(db, "Ministro", "CRIADO", None, m.nome)
    db.commit()
    db.refresh(m)
    return _to_out(db, m)


def atualizar(db: Session, ministro_id: int, data: MinistroIn) -> MinistroOut | None:
    m = db.get(Ministro, ministro_id)
    if not m:
        return None
    normalized_email = auth_service.validate_email(data.email)
    duplicate = db.query(Ministro).filter(
        Ministro.email == normalized_email,
        Ministro.id != m.id,
    ).first()
    if duplicate:
        raise ValueError("Já existe um ministro com este e-mail.")
    if m.vinculo_usuario:
        linked_user = m.vinculo_usuario.usuario
        conflicting_user = db.query(Usuario).filter(
            Usuario.email == normalized_email,
            Usuario.id != linked_user.id,
        ).first()
        if conflicting_user:
            raise ValueError("O novo e-mail já pertence a outro usuário do sistema.")
        linked_user.email = normalized_email
        linked_user.nome = data.nome.strip()
        linked_user.ativo = data.ativo
    data.email = normalized_email
    prev = m.nome
    _preencher(m, data)
    auditoria_service.registrar(db, "Ministro", "ATUALIZADO", prev, m.nome)
    db.commit()
    _sync_calendar_scales(db, m.id)
    db.refresh(m)
    return _to_out(db, m)


def deletar(db: Session, ministro_id: int) -> None:
    m = db.get(Ministro, ministro_id)
    if m:
        if m.vinculo_usuario:
            db.delete(m.vinculo_usuario.usuario)
            db.flush()
        auditoria_service.registrar(db, "Ministro", "DELETADO", m.nome, None)
        db.delete(m)
        db.commit()
