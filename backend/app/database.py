import os
import re
from urllib.parse import parse_qsl, urlencode, urlparse, urlunparse
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase


def _build_url() -> str:
    url = os.getenv("DATABASE_URL", "").strip()
    if url:
        if url.startswith("jdbc:postgresql://"):
            url = url.replace("jdbc:postgresql://", "postgresql://", 1)
        elif url.startswith("jdbc:postgres://"):
            url = url.replace("jdbc:postgres://", "postgresql://", 1)
        if url.startswith("postgres://"):
            url = url.replace("postgres://", "postgresql+psycopg2://", 1)
        elif url.startswith("postgresql://"):
            url = url.replace("postgresql://", "postgresql+psycopg2://", 1)

        parsed = urlparse(url)
        query_items = parse_qsl(parsed.query, keep_blank_values=True)
        schema = next((value for key, value in query_items if key == "schema"), None)
        if schema is not None:
            if not re.fullmatch(r"[A-Za-z_][A-Za-z0-9_]*", schema):
                raise RuntimeError("O parâmetro schema da DATABASE_URL é inválido.")
            query_items = [(key, value) for key, value in query_items if key != "schema"]
            if schema != "public" and not any(key == "options" for key, _ in query_items):
                query_items.append(("options", f"-csearch_path={schema}"))
            parsed = parsed._replace(query=urlencode(query_items))
            url = urlunparse(parsed)

        if parsed.username is None and parsed.password is None:
            db_user = os.getenv("DB_USERNAME", "")
            db_password = os.getenv("DB_PASSWORD", "")
            if db_user:
                netloc = db_user
                if db_password:
                    netloc = f"{db_user}:{db_password}"
                if parsed.hostname:
                    netloc = f"{netloc}@{parsed.hostname}"
                if parsed.port:
                    netloc = f"{netloc}:{parsed.port}"
                url = urlunparse((parsed.scheme, netloc, parsed.path, parsed.params, parsed.query, parsed.fragment))

        return url

    host = os.getenv("DB_HOST", "localhost")
    port = os.getenv("DB_PORT", "5432")
    name = os.getenv("DB_NAME", "escala_ministerial")
    user = os.getenv("DB_USERNAME", "")
    password = os.getenv("DB_PASSWORD", "")
    return f"postgresql+psycopg2://{user}:{password}@{host}:{port}/{name}"


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
