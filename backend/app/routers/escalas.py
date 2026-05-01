from typing import List, Optional
from fastapi import APIRouter, Body, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.routers import CamelRouter
from app.schemas import EscalaIn, EscalaOut, GerarEscalaIn, PreviewEscalaOut, SubstituirEscalaIn
from app.services import escala_service

router = CamelRouter()


@router.get("", response_model=List[EscalaOut])
def listar(db: Session = Depends(get_db)):
    return escala_service.listar(db)


# Must come before /{escala_id} so "preview" is not parsed as an int
@router.get("/preview/{evento_id}", response_model=PreviewEscalaOut)
def preview(evento_id: int, db: Session = Depends(get_db)):
    try:
        return escala_service.preview(db, evento_id)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.get("/{escala_id}/substituir/preview/{ministro_id}", response_model=PreviewEscalaOut)
def preview_substituicao(escala_id: int, ministro_id: int, db: Session = Depends(get_db)):
    try:
        return escala_service.preview_substituicao(db, escala_id, ministro_id)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.get("/{escala_id}", response_model=EscalaOut)
def obter(escala_id: int, db: Session = Depends(get_db)):
    e = escala_service.obter(db, escala_id)
    if not e:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return e


@router.put("/{escala_id}/substituir", response_model=EscalaOut)
def substituir(escala_id: int, data: SubstituirEscalaIn, db: Session = Depends(get_db)):
    try:
        return escala_service.substituir(db, escala_id, data.ministro_id, data.substituto_id)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.post("", response_model=EscalaOut, status_code=status.HTTP_201_CREATED)
def criar(data: EscalaIn, db: Session = Depends(get_db)):
    try:
        return escala_service.criar(db, data)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.post("/gerar/{evento_id}", response_model=EscalaOut, status_code=status.HTTP_201_CREATED)
def gerar(evento_id: int, body: Optional[GerarEscalaIn] = Body(None), db: Session = Depends(get_db)):
    try:
        ids = body.ministro_ids_manuais if body else None
        return escala_service.gerar(db, evento_id, ids)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.put("/{escala_id}/aprovar", response_model=EscalaOut)
def aprovar(escala_id: int, db: Session = Depends(get_db)):
    e = escala_service.aprovar(db, escala_id)
    if not e:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return e


@router.put("/{escala_id}/cancelar", response_model=EscalaOut)
def cancelar(escala_id: int, db: Session = Depends(get_db)):
    e = escala_service.cancelar(db, escala_id)
    if not e:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return e


@router.delete("/{escala_id}", status_code=status.HTTP_204_NO_CONTENT)
def deletar(escala_id: int, db: Session = Depends(get_db)):
    escala_service.deletar(db, escala_id)
