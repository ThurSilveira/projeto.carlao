from sqlalchemy.orm import Session
from app.models import Indisponibilidade, Ministro
from app.schemas import IndisponibilidadeIn, IndisponibilidadeOut


def _to_out(i: Indisponibilidade) -> IndisponibilidadeOut:
    return IndisponibilidadeOut.model_validate(i)


def listar_por_ministro(db: Session, ministro_id: int) -> list[IndisponibilidadeOut]:
    rows = db.query(Indisponibilidade).filter(Indisponibilidade.ministro_id == ministro_id).all()
    return [_to_out(i) for i in rows]


def criar(db: Session, ministro_id: int, data: IndisponibilidadeIn) -> IndisponibilidadeOut:
    if not db.get(Ministro, ministro_id):
        raise ValueError(f"Ministro não encontrado: {ministro_id}")
    entity = Indisponibilidade(
        ministro_id=ministro_id,
        data=data.data,
        horario_inicio=data.horario_inicio,
        horario_fim=data.horario_fim,
        motivo=data.motivo,
    )
    db.add(entity)
    db.commit()
    db.refresh(entity)
    return _to_out(entity)


def atualizar(db: Session, indisponibilidade_id: int, data: IndisponibilidadeIn) -> IndisponibilidadeOut:
    entity = db.get(Indisponibilidade, indisponibilidade_id)
    if not entity:
        raise ValueError(f"Indisponibilidade não encontrada: {indisponibilidade_id}")
    entity.data = data.data
    entity.horario_inicio = data.horario_inicio
    entity.horario_fim = data.horario_fim
    entity.motivo = data.motivo
    db.commit()
    db.refresh(entity)
    return _to_out(entity)


def deletar(db: Session, indisponibilidade_id: int) -> None:
    entity = db.get(Indisponibilidade, indisponibilidade_id)
    if entity:
        db.delete(entity)
        db.commit()
