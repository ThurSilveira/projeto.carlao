import os

from fastapi import APIRouter, Depends, HTTPException, Request, Response, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import SessaoAutenticacao
from app.schemas import AlterarSenhaIn, LoginIn, SessaoOut, UsuarioOut
from app.security import get_current_session, validate_login_source
from app.services import auditoria_service, auth_service


router = APIRouter()


def _session_response(session: SessaoAutenticacao) -> SessaoOut:
    access = session.usuario.acesso
    return SessaoOut(
        usuario=UsuarioOut(
            id=session.usuario.id,
            nome=session.usuario.nome,
            email=session.usuario.email,
            perfil=access.perfil if access else "SEM_PERFIL",
            protegido=bool(access and access.protegido),
            ministro_id=(session.usuario.vinculo_ministro.ministro_id if session.usuario.vinculo_ministro else None),
            deve_alterar_senha=session.usuario.deve_alterar_senha,
        ),
        csrf_token=session.csrf_token,
        expira_em=session.expira_em,
    )


def _set_session_cookie(response: Response, token: str) -> None:
    production = os.getenv("ENVIRONMENT", "production").lower() == "production"
    response.set_cookie(
        key=auth_service.session_cookie_name(),
        value=token,
        max_age=int(auth_service.session_ttl().total_seconds()),
        secure=production,
        httponly=True,
        samesite="lax",
        path="/",
    )
    response.headers["Cache-Control"] = "no-store"


def _delete_session_cookie(response: Response) -> None:
    production = os.getenv("ENVIRONMENT", "production").lower() == "production"
    response.delete_cookie(
        key=auth_service.session_cookie_name(),
        secure=production,
        httponly=True,
        samesite="lax",
        path="/",
    )
    response.headers["Cache-Control"] = "no-store"


@router.post("/login", response_model=SessaoOut)
def login(data: LoginIn, request: Request, response: Response, db: Session = Depends(get_db)):
    validate_login_source(request)
    client_address = request.client.host if request.client else "unknown"
    try:
        user = auth_service.authenticate(db, data.email, data.senha, client_address)
    except auth_service.LoginBloqueadoError as exc:
        raise HTTPException(
            status.HTTP_429_TOO_MANY_REQUESTS,
            detail=str(exc),
            headers={"Retry-After": str(exc.retry_after)},
        ) from exc
    if not user:
        raise HTTPException(status.HTTP_401_UNAUTHORIZED, detail="E-mail ou senha inválidos.")

    auditoria_service.registrar(db, "Autenticação", "LOGIN", None, "SESSÃO INICIADA", str(user.id))
    session, token = auth_service.create_session(db, user)
    _set_session_cookie(response, token)
    return _session_response(session)


@router.get("/me", response_model=SessaoOut)
def me(session: SessaoAutenticacao = Depends(get_current_session)):
    return _session_response(session)


@router.post("/logout", status_code=status.HTTP_204_NO_CONTENT)
def logout(
    request: Request,
    response: Response,
    session: SessaoAutenticacao = Depends(get_current_session),
    db: Session = Depends(get_db),
):
    auditoria_service.registrar(
        db, "Autenticação", "LOGOUT", "SESSÃO ATIVA", "SESSÃO ENCERRADA", str(session.usuario.id)
    )
    auth_service.revoke_session(db, request.cookies.get(auth_service.session_cookie_name()))
    _delete_session_cookie(response)


@router.put("/password", response_model=SessaoOut)
def alterar_senha(
    data: AlterarSenhaIn,
    response: Response,
    session: SessaoAutenticacao = Depends(get_current_session),
    db: Session = Depends(get_db),
):
    user = session.usuario
    try:
        new_session, token = auth_service.change_password(db, user, data.senha_atual, data.nova_senha)
    except ValueError as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, detail=str(exc)) from exc
    auditoria_service.registrar(
        db, "Autenticação", "SENHA_ALTERADA", None, "DEMAIS SESSÕES ENCERRADAS", str(user.id)
    )
    db.commit()
    _set_session_cookie(response, token)
    return _session_response(new_session)
