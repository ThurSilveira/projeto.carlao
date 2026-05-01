from sqlalchemy.orm import Session
from app.models import Feedback, Ministro, Evento
from app.schemas import FeedbackIn, FeedbackOut
from app.services import auditoria_service


def _to_out(f: Feedback) -> FeedbackOut:
    return FeedbackOut.model_validate(f)


def listar(db: Session) -> list[FeedbackOut]:
    return [_to_out(f) for f in db.query(Feedback).all()]


def criar(db: Session, data: FeedbackIn) -> FeedbackOut:
    if not db.get(Ministro, data.ministro_id):
        raise ValueError("Ministro não encontrado")
    if not db.get(Evento, data.evento_id):
        raise ValueError("Evento não encontrado")
    fb = Feedback(
        ministro_id=data.ministro_id,
        evento_id=data.evento_id,
        nota=data.nota,
        comentario=data.comentario,
        status="PENDENTE",
    )
    db.add(fb)
    db.flush()
    auditoria_service.registrar(db, "Feedback", "CRIADO", None, "PENDENTE")
    db.commit()
    db.refresh(fb)
    return _to_out(fb)


def responder(db: Session, feedback_id: int, resposta: str) -> FeedbackOut:
    fb = db.get(Feedback, feedback_id)
    if not fb:
        raise ValueError("Feedback não encontrado")
    prev = fb.status
    fb.resposta = resposta
    fb.status = "RESPONDIDO"
    auditoria_service.registrar(db, "Feedback", "ATUALIZADO", prev, "RESPONDIDO")
    db.commit()
    db.refresh(fb)
    return _to_out(fb)
