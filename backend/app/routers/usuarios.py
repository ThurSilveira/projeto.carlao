from typing import List

from fastapi import Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Usuario
from app.routers import CamelRouter
from app.schemas import PerfilOut, RedefinirSenhaIn, UsuarioAdminIn, UsuarioAdminOut, UsuarioAdminUpdate
from app.security import require_permission
from app.services import usuario_service


admin_access = require_permission("usuarios:gerir")
router = CamelRouter(dependencies=[Depends(admin_access)])


@router.get("/perfis", response_model=List[PerfilOut])
def listar_perfis():
    return usuario_service.listar_perfis()


@router.get("", response_model=List[UsuarioAdminOut])
def listar(db: Session = Depends(get_db)):
    return usuario_service.listar(db)


@router.get("/{usuario_id}", response_model=UsuarioAdminOut)
def obter(usuario_id: int, db: Session = Depends(get_db)):
    try:
        return usuario_service.obter(db, usuario_id)
    except ValueError as exc:
        raise HTTPException(status.HTTP_404_NOT_FOUND, detail=str(exc)) from exc


@router.post("", response_model=UsuarioAdminOut, status_code=status.HTTP_201_CREATED)
def criar(data: UsuarioAdminIn, db: Session = Depends(get_db), actor: Usuario = Depends(admin_access)):
    try:
        return usuario_service.criar(db, data, actor)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc


@router.put("/{usuario_id}", response_model=UsuarioAdminOut)
def atualizar(
    usuario_id: int,
    data: UsuarioAdminUpdate,
    db: Session = Depends(get_db),
    actor: Usuario = Depends(admin_access),
):
    try:
        return usuario_service.atualizar(db, usuario_id, data, actor)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc


@router.put("/{usuario_id}/senha", status_code=status.HTTP_204_NO_CONTENT)
def redefinir_senha(
    usuario_id: int,
    data: RedefinirSenhaIn,
    db: Session = Depends(get_db),
    actor: Usuario = Depends(admin_access),
):
    try:
        usuario_service.redefinir_senha(db, usuario_id, data.nova_senha, actor)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc


@router.delete("/{usuario_id}", status_code=status.HTTP_204_NO_CONTENT)
def deletar(
    usuario_id: int,
    db: Session = Depends(get_db),
    actor: Usuario = Depends(admin_access),
):
    try:
        usuario_service.deletar(db, usuario_id, actor)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc
