import os
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.database import engine
from app.models import Base
from app.routers import health, ministros, eventos, escalas, feedbacks, indisponibilidades, auditoria, seed


@asynccontextmanager
async def lifespan(app: FastAPI):
    Base.metadata.create_all(bind=engine)
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

_cors_origins = [o.strip() for o in os.getenv("CORS_ORIGINS", "*").split(",")]
app.add_middleware(
    CORSMiddleware,
    allow_origins=_cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    max_age=3600,
)


@app.exception_handler(ValueError)
async def value_error_handler(request: Request, exc: ValueError):
    return JSONResponse(status_code=400, content={"message": str(exc)})


@app.exception_handler(Exception)
async def generic_error_handler(request: Request, exc: Exception):
    return JSONResponse(status_code=500, content={"message": "Erro interno no servidor"})


# ── Routers ───────────────────────────────────────────────────────────────────
app.include_router(health.router, prefix="/api/public", tags=["health"])
app.include_router(ministros.router, prefix="/api/ministros", tags=["ministros"])
app.include_router(eventos.router, prefix="/api/eventos", tags=["eventos"])
app.include_router(escalas.router, prefix="/api/escalas", tags=["escalas"])
app.include_router(feedbacks.router, prefix="/api/feedbacks", tags=["feedbacks"])
app.include_router(
    indisponibilidades.router,
    prefix="/api/ministros/{ministro_id}/indisponibilidades",
    tags=["indisponibilidades"],
)
app.include_router(auditoria.router, prefix="/api/auditoria", tags=["auditoria"])

if os.getenv("ENVIRONMENT", "production").lower() == "development":
    app.include_router(seed.router, prefix="/api", tags=["seed"])
