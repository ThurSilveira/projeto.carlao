from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.routers import CamelRouter
from app.schemas import EventoIn, EventoOut
from app.services import evento_service

router = CamelRouter()


@router.get("", response_model=List[EventoOut])
def listar(db: Session = Depends(get_db)):
    return evento_service.listar(db)


@router.get("/{evento_id}", response_model=EventoOut)
def obter(evento_id: int, db: Session = Depends(get_db)):
    e = evento_service.obter(db, evento_id)
    if not e:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return e


@router.post("", response_model=EventoOut, status_code=status.HTTP_201_CREATED)
def criar(data: EventoIn, db: Session = Depends(get_db)):
    return evento_service.criar(db, data)


@router.put("/{evento_id}", response_model=EventoOut)
def atualizar(evento_id: int, data: EventoIn, db: Session = Depends(get_db)):
    e = evento_service.atualizar(db, evento_id, data)
    if not e:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return e


@router.put("/{evento_id}/cancelar", response_model=EventoOut)
def cancelar(evento_id: int, db: Session = Depends(get_db)):
    e = evento_service.cancelar(db, evento_id)
    if not e:
        raise HTTPException(status.HTTP_404_NOT_FOUND)
    return e


@router.delete("/{evento_id}", status_code=status.HTTP_204_NO_CONTENT)
def deletar(evento_id: int, db: Session = Depends(get_db)):
    evento_service.deletar(db, evento_id)
