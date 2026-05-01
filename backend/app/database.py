import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase


def _build_url() -> str:
    url = os.getenv("DATABASE_URL", "")
    # Render/Heroku use postgres:// or postgresql:// — SQLAlchemy 2.x needs psycopg2 dialect
    if url.startswith("postgres://"):
        url = url.replace("postgres://", "postgresql+psycopg2://", 1)
    elif url.startswith("postgresql://"):
        url = url.replace("postgresql://", "postgresql+psycopg2://", 1)
    if url:
        return url
    host = os.getenv("DB_HOST", "localhost")
    port = os.getenv("DB_PORT", "5432")
    name = os.getenv("DB_NAME", "escala_ministerial")
    user = os.getenv("DB_USERNAME", "")
    password = os.getenv("DB_PASSWORD", "")
    return f"postgresql+psycopg2://{user}:{password}@{host}:{port}/{name}"


_ssl = os.getenv("DB_SSL", "false").lower() == "true"
_connect_args = {"sslmode": "require"} if _ssl else {}

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
