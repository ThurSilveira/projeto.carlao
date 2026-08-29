import hashlib
import logging
import os
import secrets
from datetime import UTC, datetime, timedelta

from argon2 import PasswordHasher
from argon2.exceptions import InvalidHashError, VerificationError
from sqlalchemy.orm import Session

from app.auth_profiles import ADMINISTRADOR, MINISTRO
from app.models import (
    AcessoUsuario,
    Ministro,
    SessaoAutenticacao,
    TentativaLogin,
    Usuario,
    VinculoUsuarioMinistro,
)


logger = logging.getLogger(__name__)
SESSION_COOKIE_PRODUCTION = "__Host-escala_session"
SESSION_COOKIE_DEVELOPMENT = "escala_session"
DEFAULT_MINISTER_PASSWORD = "Senha123"
_PASSWORD_HASHER = PasswordHasher(
    time_cost=3,
    memory_cost=65536,
    parallelism=4,
    hash_len=32,
    salt_len=16,
)
_DUMMY_PASSWORD_HASH = _PASSWORD_HASHER.hash(secrets.token_urlsafe(32))


class LoginBloqueadoError(Exception):
    def __init__(self, retry_after: int):
        super().__init__("Muitas tentativas de acesso. Aguarde antes de tentar novamente.")
        self.retry_after = retry_after


def utcnow() -> datetime:
    return datetime.now(UTC).replace(tzinfo=None)


def normalize_email(email: str) -> str:
    return email.strip().lower()


def validate_email(email: str) -> str:
    normalized = normalize_email(email)
    local, separator, domain = normalized.rpartition("@")
    if not separator or not local or "." not in domain or domain.startswith(".") or domain.endswith("."):
        raise ValueError("Informe um endereço de e-mail válido.")
    return normalized


def validate_new_password(password: str) -> None:
    if len(password) < 12:
        raise ValueError("A senha deve conter pelo menos 12 caracteres.")
    if len(password) > 128:
        raise ValueError("A senha deve conter no máximo 128 caracteres.")


def hash_password(password: str) -> str:
    validate_new_password(password)
    return _PASSWORD_HASHER.hash(password)


def create_minister_access(db: Session, minister: Ministro) -> Usuario:
    """Cria a conta inicial vinculada ao ministro dentro da transação atual."""
    normalized_email = validate_email(minister.email)
    if db.query(Usuario.id).filter(Usuario.email == normalized_email).first():
        raise ValueError("Já existe um usuário com este e-mail.")

    user = Usuario(
        nome=minister.nome.strip()[:120],
        email=normalized_email,
        senha_hash=_PASSWORD_HASHER.hash(DEFAULT_MINISTER_PASSWORD),
        deve_alterar_senha=True,
        ativo=minister.ativo,
    )
    db.add(user)
    db.flush()
    db.add(AcessoUsuario(usuario_id=user.id, perfil=MINISTRO, protegido=False))
    db.add(VinculoUsuarioMinistro(usuario_id=user.id, ministro_id=minister.id))
    return user


def verify_password(password: str, password_hash: str) -> bool:
    try:
        return _PASSWORD_HASHER.verify(password_hash, password)
    except (VerificationError, InvalidHashError):
        return False


def needs_password_rehash(password_hash: str) -> bool:
    try:
        return _PASSWORD_HASHER.check_needs_rehash(password_hash)
    except InvalidHashError:
        return True


def session_cookie_name() -> str:
    if os.getenv("ENVIRONMENT", "production").lower() == "production":
        return SESSION_COOKIE_PRODUCTION
    return SESSION_COOKIE_DEVELOPMENT


def session_ttl() -> timedelta:
    raw_hours = os.getenv("AUTH_SESSION_HOURS", "8")
    try:
        hours = int(raw_hours)
    except ValueError as exc:
        raise RuntimeError("AUTH_SESSION_HOURS deve ser um número inteiro.") from exc
    if not 1 <= hours <= 168:
        raise RuntimeError("AUTH_SESSION_HOURS deve estar entre 1 e 168 horas.")
    return timedelta(hours=hours)


def _sha256(value: str) -> str:
    return hashlib.sha256(value.encode("utf-8")).hexdigest()


def bootstrap_admin(db: Session) -> bool:
    """Cria uma única conta inicial explícita, sem inventar credenciais."""
    if db.query(Usuario.id).first():
        return False

    email = os.getenv("AUTH_ADMIN_EMAIL", "").strip()
    password = os.getenv("AUTH_ADMIN_PASSWORD", "")
    name = os.getenv("AUTH_ADMIN_NAME", "Administrador").strip() or "Administrador"
    if not email and not password:
        logger.warning(
            "Autenticação ainda não inicializada: defina AUTH_ADMIN_EMAIL e AUTH_ADMIN_PASSWORD."
        )
        return False
    if not email or not password:
        raise RuntimeError("Defina AUTH_ADMIN_EMAIL e AUTH_ADMIN_PASSWORD em conjunto.")

    admin = Usuario(
        nome=name[:120],
        email=validate_email(email),
        senha_hash=hash_password(password),
        ativo=True,
    )
    db.add(admin)
    db.flush()
    db.add(AcessoUsuario(usuario_id=admin.id, perfil=ADMINISTRADOR, protegido=True))
    db.commit()
    logger.info("Administrador inicial protegido criado.")
    return True


def bootstrap_test_minister(db: Session) -> bool:
    """Cria o acesso de teste somente quando as duas credenciais forem fornecidas."""
    email = os.getenv("AUTH_MINISTRO_EMAIL", "").strip()
    password = os.getenv("AUTH_MINISTRO_PASSWORD", "")
    name = os.getenv("AUTH_MINISTRO_NAME", "Ministro de Teste").strip() or "Ministro de Teste"
    if not email and not password:
        return False
    if not email or not password:
        raise RuntimeError("Defina AUTH_MINISTRO_EMAIL e AUTH_MINISTRO_PASSWORD em conjunto.")

    normalized_email = validate_email(email)
    if db.query(Usuario.id).filter(Usuario.email == normalized_email).first():
        return False

    minister = db.query(Ministro).filter(Ministro.email == normalized_email).first()
    if minister and not minister.ativo:
        raise RuntimeError("O ministro configurado para teste está inativo.")
    if not minister:
        minister = Ministro(nome=name[:120], email=normalized_email, ativo=True, funcao="LEITURA")
        db.add(minister)
        db.flush()

    user = Usuario(
        nome=minister.nome[:120],
        email=normalized_email,
        senha_hash=hash_password(password),
        ativo=True,
    )
    db.add(user)
    db.flush()
    db.add(AcessoUsuario(usuario_id=user.id, perfil=MINISTRO, protegido=False))
    db.add(VinculoUsuarioMinistro(usuario_id=user.id, ministro_id=minister.id))
    db.commit()
    return True


def _rate_key(kind: str, value: str) -> str:
    return _sha256(f"{kind}:{value}")


def _attempt_count(db: Session, key: str, since: datetime) -> int:
    return (
        db.query(TentativaLogin)
        .filter(TentativaLogin.chave_hash == key, TentativaLogin.criada_em >= since)
        .count()
    )


def check_rate_limit(db: Session, email: str, client_address: str) -> None:
    now = utcnow()
    window = timedelta(minutes=15)
    since = now - window
    db.query(TentativaLogin).filter(TentativaLogin.criada_em < since).delete(synchronize_session=False)
    db.commit()

    identity_key = _rate_key("identity", normalize_email(email))
    client_key = _rate_key("client", client_address)
    if _attempt_count(db, identity_key, since) < 5 and _attempt_count(db, client_key, since) < 25:
        return

    oldest = (
        db.query(TentativaLogin)
        .filter(
            TentativaLogin.chave_hash.in_([identity_key, client_key]),
            TentativaLogin.criada_em >= since,
        )
        .order_by(TentativaLogin.criada_em.asc())
        .first()
    )
    retry_after = 900
    if oldest:
        retry_after = max(1, int((oldest.criada_em + window - now).total_seconds()))
    raise LoginBloqueadoError(retry_after)


def record_failed_login(db: Session, email: str, client_address: str) -> None:
    db.add_all(
        [
            TentativaLogin(chave_hash=_rate_key("identity", normalize_email(email))),
            TentativaLogin(chave_hash=_rate_key("client", client_address)),
        ]
    )
    db.commit()


def clear_login_attempts(db: Session, email: str, client_address: str) -> None:
    keys = [_rate_key("identity", normalize_email(email)), _rate_key("client", client_address)]
    db.query(TentativaLogin).filter(TentativaLogin.chave_hash.in_(keys)).delete(synchronize_session=False)
    db.commit()


def authenticate(db: Session, email: str, password: str, client_address: str) -> Usuario | None:
    normalized_email = normalize_email(email)
    check_rate_limit(db, normalized_email, client_address)
    user = db.query(Usuario).filter(Usuario.email == normalized_email).first()
    valid = verify_password(password, user.senha_hash if user else _DUMMY_PASSWORD_HASH)
    if not user or not user.ativo or not valid:
        record_failed_login(db, normalized_email, client_address)
        return None

    clear_login_attempts(db, normalized_email, client_address)
    if needs_password_rehash(user.senha_hash):
        user.senha_hash = _PASSWORD_HASHER.hash(password)
        db.commit()
    return user


def create_session(db: Session, user: Usuario) -> tuple[SessaoAutenticacao, str]:
    raw_token = secrets.token_urlsafe(48)
    session = SessaoAutenticacao(
        usuario_id=user.id,
        token_hash=_sha256(raw_token),
        csrf_token=secrets.token_urlsafe(32),
        expira_em=utcnow() + session_ttl(),
    )
    db.add(session)
    db.commit()
    db.refresh(session)
    return session, raw_token


def get_session(db: Session, raw_token: str | None) -> SessaoAutenticacao | None:
    if not raw_token:
        return None
    session = db.query(SessaoAutenticacao).filter(
        SessaoAutenticacao.token_hash == _sha256(raw_token)
    ).first()
    if not session:
        return None
    if session.expira_em <= utcnow() or not session.usuario or not session.usuario.ativo:
        db.delete(session)
        db.commit()
        return None
    return session


def revoke_session(db: Session, raw_token: str | None) -> None:
    if not raw_token:
        return
    db.query(SessaoAutenticacao).filter(
        SessaoAutenticacao.token_hash == _sha256(raw_token)
    ).delete(synchronize_session=False)
    db.commit()


def change_password(
    db: Session,
    user: Usuario,
    current_password: str,
    new_password: str,
) -> tuple[SessaoAutenticacao, str]:
    if not verify_password(current_password, user.senha_hash):
        raise ValueError("A senha atual está incorreta.")
    validate_new_password(new_password)
    if verify_password(new_password, user.senha_hash):
        raise ValueError("A nova senha deve ser diferente da senha atual.")

    user.senha_hash = hash_password(new_password)
    user.deve_alterar_senha = False
    db.query(SessaoAutenticacao).filter(
        SessaoAutenticacao.usuario_id == user.id
    ).delete(synchronize_session="fetch")
    db.commit()
    return create_session(db, user)
