import os
import re

from sqlalchemy import create_engine
from sqlalchemy.engine import URL, make_url
from sqlalchemy.orm import sessionmaker, DeclarativeBase


def _normalize_url(raw_url: str) -> str:
    """Converte formatos aceitos pelo projeto para uma URL válida do psycopg2."""
    url = raw_url.strip()
    if url.startswith("jdbc:postgresql://"):
        url = url.removeprefix("jdbc:")
    elif url.startswith("jdbc:postgres://"):
        url = url.removeprefix("jdbc:")

    parsed = make_url(url)
    if parsed.drivername in {"postgres", "postgresql"}:
        parsed = parsed.set(drivername="postgresql+psycopg2")

    query = dict(parsed.query)
    schema = query.pop("schema", None)
    if schema is not None:
        if not re.fullmatch(r"[A-Za-z_][A-Za-z0-9_]*", schema):
            raise RuntimeError("O parâmetro schema da DATABASE_URL é inválido.")
        if schema != "public" and "options" not in query:
            query["options"] = f"-csearch_path={schema}"
        parsed = parsed.set(query=query)

    if parsed.username is None:
        username = os.getenv("DB_USERNAME", "").strip() or None
        password = os.getenv("DB_PASSWORD") if username else None
        parsed = parsed.set(username=username, password=password)

    return parsed.render_as_string(hide_password=False)


def _build_url() -> str:
    url = os.getenv("DATABASE_URL", "").strip()
    if url:
        return _normalize_url(url)

    host = os.getenv("DB_HOST", "localhost")
    raw_port = os.getenv("DB_PORT", "5432")
    name = os.getenv("DB_NAME", "escala_ministerial")
    user = os.getenv("DB_USERNAME", "").strip() or None
    password = os.getenv("DB_PASSWORD") if user else None
    try:
        port = int(raw_port)
    except ValueError as exc:
        raise RuntimeError("DB_PORT deve ser um número inteiro.") from exc

    return URL.create(
        "postgresql+psycopg2",
        username=user,
        password=password,
        host=host,
        port=port,
        database=name,
    ).render_as_string(hide_password=False)


_sslmode = os.getenv("DB_SSLMODE", "").strip().lower()
_ssl = os.getenv("DB_SSL", "false").lower() == "true"
if _sslmode:
    _connect_args = {"sslmode": _sslmode}
elif _ssl:
    _connect_args = {"sslmode": "require"}
else:
    _connect_args = {}

engine = create_engine(
    _build_url(),
    connect_args=_connect_args,
    pool_size=5,
    max_overflow=5,
    pool_pre_ping=True,
)

SessionLocal = sessionmaker(autocommit=False, autoflush=True, bind=engine)


class Base(DeclarativeBase):
    pass


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
