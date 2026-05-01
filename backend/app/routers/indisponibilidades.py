from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.routers import CamelRouter
from app.schemas import IndisponibilidadeIn, IndisponibilidadeOut
from app.services import indisponibilidade_service

router = CamelRouter()


@router.get("", response_model=List[IndisponibilidadeOut])
def listar(ministro_id: int, db: Session = Depends(get_db)):
    return indisponibilidade_service.listar_por_ministro(db, ministro_id)


@router.post("", response_model=IndisponibilidadeOut, status_code=status.HTTP_201_CREATED)
def criar(ministro_id: int, data: IndisponibilidadeIn, db: Session = Depends(get_db)):
    try:
        return indisponibilidade_service.criar(db, ministro_id, data)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.put("/{indisponibilidade_id}", response_model=IndisponibilidadeOut)
def atualizar(ministro_id: int, indisponibilidade_id: int, data: IndisponibilidadeIn, db: Session = Depends(get_db)):
    try:
        return indisponibilidade_service.atualizar(db, indisponibilidade_id, data)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc))


@router.delete("/{indisponibilidade_id}", status_code=status.HTTP_204_NO_CONTENT)
def deletar(ministro_id: int, indisponibilidade_id: int, db: Session = Depends(get_db)):
    indisponibilidade_service.deletar(db, indisponibilidade_id)
