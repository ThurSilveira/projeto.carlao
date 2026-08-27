import os
import logging
from contextlib import asynccontextmanager
from fastapi import Depends, FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.database import SessionLocal, engine
from app.models import Base
from app.routers import auth, health, ministros, eventos, escalas, feedbacks, indisponibilidades, auditoria, seed, usuarios
from app.security import require_permission, require_resource_access
from app.services import auth_service


logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    Base.metadata.create_all(bind=engine)
    with SessionLocal() as db:
        auth_service.bootstrap_admin(db)
        if os.getenv("ENVIRONMENT", "production").lower() == "development":
            auth_service.bootstrap_test_minister(db)
    yield


app = FastAPI(
    title="Escala Ministerial API",
    version="2.0.0",
    lifespan=lifespan,
    docs_url="/api/docs",
    redoc_url="/api/redoc",
    openapi_url="/api/openapi.json",
    redirect_slashes=False,
)

_cors_origins = [o.strip() for o in os.getenv("CORS_ORIGINS", "http://localhost:3000").split(",") if o.strip()]
if os.getenv("ENVIRONMENT", "production").lower() == "production" and "*" in _cors_origins:
    raise RuntimeError("CORS_ORIGINS não pode usar '*' em produção.")
app.add_middleware(
    CORSMiddleware,
    allow_origins=_cors_origins,
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allow_headers=["Accept", "Content-Type", "X-CSRF-Token", "X-Requested-With"],
    max_age=3600,
)


@app.exception_handler(ValueError)
async def value_error_handler(request: Request, exc: ValueError):
    return JSONResponse(status_code=400, content={"message": str(exc)})


@app.exception_handler(Exception)
async def generic_error_handler(request: Request, exc: Exception):
    logger.exception("Erro não tratado em %s", request.url.path)
    return JSONResponse(status_code=500, content={"message": "Erro interno no servidor"})


@app.middleware("http")
async def security_headers(request: Request, call_next):
    response = await call_next(request)
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"] = "DENY"
    response.headers["Referrer-Policy"] = "no-referrer"
    response.headers["Permissions-Policy"] = "camera=(), microphone=(), geolocation=()"
    if os.getenv("ENVIRONMENT", "production").lower() == "production":
        response.headers["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains"
    if request.url.path.startswith("/api/auth"):
        response.headers["Cache-Control"] = "no-store"
    return response


# ── Routers ───────────────────────────────────────────────────────────────────
app.include_router(health.router, prefix="/api/public", tags=["health"])
app.include_router(auth.router, prefix="/api/auth", tags=["autenticação"])
app.include_router(usuarios.router, prefix="/api/usuarios", tags=["usuários"])
app.include_router(
    ministros.router,
    prefix="/api/ministros",
    tags=["ministros"],
    dependencies=[Depends(require_resource_access("ministros"))],
)
app.include_router(
    eventos.router,
    prefix="/api/eventos",
    tags=["eventos"],
    dependencies=[Depends(require_resource_access("eventos"))],
)
app.include_router(
    escalas.router,
    prefix="/api/escalas",
    tags=["escalas"],
    dependencies=[Depends(require_resource_access("escalas"))],
)
app.include_router(
    feedbacks.router,
    prefix="/api/feedbacks",
    tags=["feedbacks"],
    dependencies=[Depends(require_resource_access("feedbacks"))],
)
app.include_router(
    indisponibilidades.router,
    prefix="/api/ministros/{ministro_id}/indisponibilidades",
    tags=["indisponibilidades"],
    dependencies=[Depends(require_resource_access("ministros"))],
)
app.include_router(
    auditoria.router,
    prefix="/api/auditoria",
    tags=["auditoria"],
    dependencies=[Depends(require_resource_access("auditoria"))],
)

if os.getenv("ENVIRONMENT", "production").lower() == "development":
    app.include_router(
        seed.router,
        prefix="/api",
        tags=["seed"],
        dependencies=[Depends(require_permission("*"))],
    )
