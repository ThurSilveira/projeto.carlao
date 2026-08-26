import hmac
import os
from collections.abc import Generator
from typing import Callable

from fastapi import Depends, HTTPException, Request, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.auth_profiles import has_permission
from app.models import SessaoAutenticacao, Usuario
from app.services import auth_service


SAFE_METHODS = {"GET", "HEAD", "OPTIONS"}


def allowed_origins() -> set[str]:
    return {
        origin.strip().rstrip("/")
        for origin in os.getenv("CORS_ORIGINS", "http://localhost:3000").split(",")
        if origin.strip() and origin.strip() != "*"
    }


def validate_login_source(request: Request) -> None:
    origin = request.headers.get("origin", "").rstrip("/")
    requested_with = request.headers.get("x-requested-with", "")
    if origin and origin in allowed_origins():
        return
    if not origin and requested_with == "XMLHttpRequest":
        return
    raise HTTPException(status.HTTP_403_FORBIDDEN, detail="Origem da requisição não autorizada.")


def get_current_session(request: Request, db: Session = Depends(get_db)) -> SessaoAutenticacao:
    raw_token = request.cookies.get(auth_service.session_cookie_name())
    session = auth_service.get_session(db, raw_token)
    if not session:
        raise HTTPException(
            status.HTTP_401_UNAUTHORIZED,
            detail="Sessão ausente ou expirada.",
            headers={"WWW-Authenticate": "Session"},
        )

    if request.method not in SAFE_METHODS:
        submitted_token = request.headers.get("x-csrf-token", "")
        if not submitted_token or not hmac.compare_digest(submitted_token, session.csrf_token):
            raise HTTPException(status.HTTP_403_FORBIDDEN, detail="Token CSRF inválido ou ausente.")
    return session


def require_user(
    session: SessaoAutenticacao = Depends(get_current_session),
    db: Session = Depends(get_db),
) -> Generator[Usuario, None, None]:
    db.info["current_user_id"] = str(session.usuario.id)
    try:
        yield session.usuario
    finally:
        db.info.pop("current_user_id", None)


def require_permission(permission: str) -> Callable:
    def dependency(user: Usuario = Depends(require_user)) -> Usuario:
        profile = user.acesso.perfil if user.acesso else ""
        if not has_permission(profile, permission):
            raise HTTPException(
                status.HTTP_403_FORBIDDEN,
                detail="Seu perfil não possui permissão para esta operação.",
            )
        return user

    return dependency


def require_resource_access(resource: str) -> Callable:
    def dependency(request: Request, user: Usuario = Depends(require_user)) -> Usuario:
        action = "ler" if request.method in SAFE_METHODS else "gerir"
        profile = user.acesso.perfil if user.acesso else ""
        if not has_permission(profile, f"{resource}:{action}"):
            raise HTTPException(
                status.HTTP_403_FORBIDDEN,
                detail="Seu perfil não possui permissão para esta operação.",
            )
        return user

    return dependency
