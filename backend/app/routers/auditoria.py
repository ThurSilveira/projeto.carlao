from typing import List
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.database import get_db
from app.routers import CamelRouter
from app.schemas import LogAuditoriaOut
from app.services import auditoria_service

router = CamelRouter()


@router.get("", response_model=List[LogAuditoriaOut])
def listar(db: Session = Depends(get_db)):
    return auditoria_service.listar(db)
