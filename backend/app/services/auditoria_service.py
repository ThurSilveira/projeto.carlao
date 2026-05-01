from sqlalchemy.orm import Session
from app.models import LogAuditoria
from app.schemas import LogAuditoriaOut


def registrar(db: Session, entidade: str, acao: str, status_anterior: str | None, status_novo: str | None) -> None:
    try:
        log = LogAuditoria(
            entidade=entidade,
            acao=acao,
            status_anterior=status_anterior,
            status_novo=status_novo,
        )
        db.add(log)
    except Exception:
        pass


def listar(db: Session) -> list[LogAuditoriaOut]:
    logs = db.query(LogAuditoria).order_by(LogAuditoria.data_hora.desc()).all()
    return [LogAuditoriaOut.model_validate(log) for log in logs]
