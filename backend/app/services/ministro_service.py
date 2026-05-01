from sqlalchemy.orm import Session
from app.models import Ministro, EscalaMinistro, Escala, Evento
from app.schemas import MinistroIn, MinistroOut, IndisponibilidadeOut
from app.services import auditoria_service

_FUNCOES_VALIDAS = {"EUCARISTIA", "LEITURA", "ACOLHIMENTO", "MUSICA", "CATEQUESE", "ADORACAO", "OUTRO"}


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
    m = Ministro()
    _preencher(m, data)
    db.add(m)
    db.flush()
    auditoria_service.registrar(db, "Ministro", "CRIADO", None, m.nome)
    db.commit()
    db.refresh(m)
    return _to_out(db, m)


def atualizar(db: Session, ministro_id: int, data: MinistroIn) -> MinistroOut | None:
    m = db.get(Ministro, ministro_id)
    if not m:
        return None
    prev = m.nome
    _preencher(m, data)
    auditoria_service.registrar(db, "Ministro", "ATUALIZADO", prev, m.nome)
    db.commit()
    db.refresh(m)
    return _to_out(db, m)


def deletar(db: Session, ministro_id: int) -> None:
    m = db.get(Ministro, ministro_id)
    if m:
        auditoria_service.registrar(db, "Ministro", "DELETADO", m.nome, None)
        db.delete(m)
        db.commit()
