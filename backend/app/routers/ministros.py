from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.routers import CamelRouter
from app.schemas import MinistroIn, MinistroOut
from app.services import ministro_service

router = CamelRouter()


@router.get("", response_model=List[MinistroOut])
def listar(db: Session = Depends(get_db)):
    return ministro_service.listar(db)


@router.get("/{ministro_id}", response_model=MinistroOut)
def obter(ministro_id: int, db: Session = Depends(get_db)):
    m = ministro_service.obter(db, ministro_id)
    if not m:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return m


@router.post("", response_model=MinistroOut, status_code=status.HTTP_201_CREATED)
def criar(data: MinistroIn, db: Session = Depends(get_db)):
    return ministro_service.criar(db, data)


@router.put("/{ministro_id}", response_model=MinistroOut)
def atualizar(ministro_id: int, data: MinistroIn, db: Session = Depends(get_db)):
    m = ministro_service.atualizar(db, ministro_id, data)
    if not m:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return m


@router.delete("/{ministro_id}", status_code=status.HTTP_204_NO_CONTENT)
def deletar(ministro_id: int, db: Session = Depends(get_db)):
    ministro_service.deletar(db, ministro_id)
