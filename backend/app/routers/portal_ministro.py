from dataclasses import dataclass
from datetime import date
from typing import List

from fastapi import Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Ministro, Usuario
from app.routers import CamelRouter
from app.schemas import (
    CalendarioMinistroEventoOut,
    FeedbackMinistroIn,
    FeedbackMinistroOut,
    IndisponibilidadeIn,
    IndisponibilidadeOut,
    MinistroPortalOut,
)
from app.security import require_user
from app.services import portal_ministro_service


router = CamelRouter()


@dataclass(frozen=True)
class PortalContext:
    user: Usuario
    minister: Ministro


def portal_context(user: Usuario = Depends(require_user)) -> PortalContext:
    try:
        minister = portal_ministro_service.minister_for_user(user)
    except PermissionError as exc:
        raise HTTPException(status.HTTP_403_FORBIDDEN, detail=str(exc)) from exc
    return PortalContext(user=user, minister=minister)


@router.get("/me", response_model=MinistroPortalOut)
def me(context: PortalContext = Depends(portal_context)):
    return portal_ministro_service.profile(context.user)


@router.get("/calendario", response_model=List[CalendarioMinistroEventoOut])
def calendario(
    data_inicio: date = Query(..., alias="dataInicio"),
    data_fim: date = Query(..., alias="dataFim"),
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    try:
        return portal_ministro_service.calendar(db, context.minister, data_inicio, data_fim)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc


@router.get("/indisponibilidades", response_model=List[IndisponibilidadeOut])
def listar_indisponibilidades(
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    return portal_ministro_service.list_unavailability(db, context.minister)


@router.post(
    "/indisponibilidades",
    response_model=IndisponibilidadeOut,
    status_code=status.HTTP_201_CREATED,
)
def criar_indisponibilidade(
    data: IndisponibilidadeIn,
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    try:
        return portal_ministro_service.create_unavailability(
            db, context.minister, context.user, data
        )
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc


@router.put("/indisponibilidades/{indisponibilidade_id}", response_model=IndisponibilidadeOut)
def atualizar_indisponibilidade(
    indisponibilidade_id: int,
    data: IndisponibilidadeIn,
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    try:
        return portal_ministro_service.update_unavailability(
            db, context.minister, context.user, indisponibilidade_id, data
        )
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc


@router.delete("/indisponibilidades/{indisponibilidade_id}", status_code=status.HTTP_204_NO_CONTENT)
def excluir_indisponibilidade(
    indisponibilidade_id: int,
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    try:
        portal_ministro_service.delete_unavailability(
            db, context.minister, context.user, indisponibilidade_id
        )
    except ValueError as exc:
        raise HTTPException(status.HTTP_404_NOT_FOUND, detail=str(exc)) from exc


@router.get("/feedbacks", response_model=List[FeedbackMinistroOut])
def listar_feedbacks(
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    return portal_ministro_service.list_feedback(db, context.minister)


@router.post("/feedbacks", response_model=FeedbackMinistroOut, status_code=status.HTTP_201_CREATED)
def criar_feedback(
    data: FeedbackMinistroIn,
    db: Session = Depends(get_db),
    context: PortalContext = Depends(portal_context),
):
    try:
        return portal_ministro_service.create_feedback(
            db, context.minister, context.user, data
        )
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc
