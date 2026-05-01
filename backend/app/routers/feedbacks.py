from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.routers import CamelRouter
from app.schemas import FeedbackIn, FeedbackOut, FeedbackResponder
from app.services import feedback_service

router = CamelRouter()


@router.get("", response_model=List[FeedbackOut])
def listar(db: Session = Depends(get_db)):
    return feedback_service.listar(db)


@router.post("", response_model=FeedbackOut, status_code=status.HTTP_201_CREATED)
def criar(data: FeedbackIn, db: Session = Depends(get_db)):
    try:
        return feedback_service.criar(db, data)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.put("/{feedback_id}/responder", response_model=FeedbackOut)
def responder(feedback_id: int, body: FeedbackResponder, db: Session = Depends(get_db)):
    if not body.resposta or not body.resposta.strip():
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail="Resposta não pode ser vazia")
    try:
        return feedback_service.responder(db, feedback_id, body.resposta)
    except ValueError as exc:
        raise HTTPException(status.HTTP_404_NOT_FOUND, detail=str(exc))
