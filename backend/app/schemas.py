from __future__ import annotations
from datetime import date, datetime, time as time_obj
from typing import List, Optional
from pydantic import BaseModel, ConfigDict, Field, field_validator
from pydantic.alias_generators import to_camel


def _normalize_time(value: str | None) -> str | None:
    if value is None:
        return None
    if isinstance(value, time_obj):
        return value.strftime("%H:%M")

    raw = str(value).strip()
    if not raw:
        return None

    normalized = raw.replace(".", ":").replace(",", ":").replace("h", ":").replace("H", ":").strip()
    if len(normalized) == 4 and normalized.isdigit():
        normalized = f"{normalized[:2]}:{normalized[2:]}"

    for fmt in ("%H:%M", "%H:%M:%S"):
        try:
            return datetime.strptime(normalized, fmt).time().strftime("%H:%M")
        except ValueError:
            continue

    raise ValueError(f"Horário inválido: {value}")


class BaseSchema(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        alias_generator=to_camel,
        populate_by_name=True,
    )


# ── Autenticação ─────────────────────────────────────────────────────────────

class LoginIn(BaseSchema):
    email: str = Field(min_length=3, max_length=254)
    senha: str = Field(min_length=1, max_length=128)

    @field_validator("email")
    @classmethod
    def normalizar_email(cls, value: str) -> str:
        return value.strip().lower()


class UsuarioOut(BaseSchema):
    id: int
    nome: str
    email: str
    perfil: str
    protegido: bool = False
    ministro_id: Optional[int] = None


class SessaoOut(BaseSchema):
    usuario: UsuarioOut
    csrf_token: str
    expira_em: datetime


class AlterarSenhaIn(BaseSchema):
    senha_atual: str = Field(min_length=1, max_length=128)
    nova_senha: str = Field(min_length=12, max_length=128)


class UsuarioAdminIn(BaseSchema):
    nome: str = Field(min_length=2, max_length=120)
    email: str = Field(min_length=3, max_length=254)
    senha: str = Field(min_length=12, max_length=128)
    perfil: str
    ativo: bool = True
    ministro_id: Optional[int] = None

    @field_validator("email")
    @classmethod
    def normalizar_email(cls, value: str) -> str:
        return value.strip().lower()


class UsuarioAdminUpdate(BaseSchema):
    nome: str = Field(min_length=2, max_length=120)
    email: str = Field(min_length=3, max_length=254)
    perfil: str
    ativo: bool
    ministro_id: Optional[int] = None

    @field_validator("email")
    @classmethod
    def normalizar_email(cls, value: str) -> str:
        return value.strip().lower()


class UsuarioAdminOut(BaseSchema):
    id: int
    nome: str
    email: str
    perfil: str
    ativo: bool
    protegido: bool
    ministro_id: Optional[int] = None
    criado_em: datetime
    atualizado_em: datetime


class RedefinirSenhaIn(BaseSchema):
    nova_senha: str = Field(min_length=12, max_length=128)


class PerfilOut(BaseSchema):
    nome: str
    descricao: str
    permissoes: List[str]


# ── Indisponibilidade ─────────────────────────────────────────────────────────

class IndisponibilidadeIn(BaseSchema):
    data: date
    horario_inicio: Optional[str] = None
    horario_fim: Optional[str] = None
    motivo: Optional[str] = None

    @field_validator("horario_inicio", "horario_fim", mode="before")
    def normalize_horario(cls, value):
        return _normalize_time(value)


class IndisponibilidadeOut(BaseSchema):
    id: int
    ministro_id: Optional[int] = None
    data: date
    horario_inicio: Optional[str] = None
    horario_fim: Optional[str] = None
    motivo: Optional[str] = None


# ── Ministro ──────────────────────────────────────────────────────────────────

class MinistroIn(BaseSchema):
    nome: str
    email: str
    telefone: Optional[str] = None
    data_nascimento: Optional[date] = None
    observacoes: Optional[str] = None
    ativo: bool = True
    visitas_ao_infermo: bool = False
    status_curso: bool = False
    funcao: Optional[str] = None
    funcao_especificada: Optional[str] = None


class MinistroOut(BaseSchema):
    id: int
    nome: str
    email: str
    telefone: Optional[str] = None
    data_nascimento: Optional[date] = None
    observacoes: Optional[str] = None
    ativo: bool = True
    visitas_ao_infermo: bool = False
    status_curso: bool = False
    escalas_mes: Optional[int] = 0
    funcao: Optional[str] = None
    funcao_especificada: Optional[str] = None
    indisponibilidades: List[IndisponibilidadeOut] = []
    escalas_agendadas: List[date] = []


# ── Evento ────────────────────────────────────────────────────────────────────

class EventoIn(BaseSchema):
    nome: str
    data: date
    horario: str
    tipo_evento: Optional[str] = None
    tipo_especificado: Optional[str] = None
    max_ministros: Optional[int] = 6
    local: Optional[str] = None
    cancelado: bool = False

    @field_validator("horario", mode="before")
    def normalize_horario(cls, value):
        return _normalize_time(value)


class EventoOut(BaseSchema):
    id: int
    nome: str
    data: date
    horario: str
    tipo_evento: Optional[str] = None
    tipo_especificado: Optional[str] = None
    max_ministros: Optional[int] = None
    local: Optional[str] = None
    cancelado: bool = False


# ── Escala ────────────────────────────────────────────────────────────────────

class EscalaMinistroOut(BaseSchema):
    id: int
    escala_id: Optional[int] = None
    ministro_id: Optional[int] = None
    ministro_nome: Optional[str] = None
    ministro_funcao: Optional[str] = None
    confirmacao_ministro: bool = False
    data_confirmacao: Optional[date] = None
    substituido: bool = False


class EscalaIn(BaseSchema):
    evento_id: int
    observacao: Optional[str] = None


class EscalaOut(BaseSchema):
    id: int
    data_atribuicao: Optional[date] = None
    observacao: Optional[str] = None
    status: Optional[str] = None
    evento_id: Optional[int] = None
    evento: Optional[EventoOut] = None
    escala_ministros: List[EscalaMinistroOut] = []


# ── Feedback ──────────────────────────────────────────────────────────────────

class FeedbackIn(BaseSchema):
    ministro_id: int
    evento_id: int
    nota: int = Field(ge=1, le=10)
    comentario: Optional[str] = Field(default=None, max_length=2000)


class FeedbackOut(BaseSchema):
    id: int
    ministro_id: Optional[int] = None
    evento_id: Optional[int] = None
    nota: int
    comentario: Optional[str] = None
    data_envio: Optional[datetime] = None
    status: Optional[str] = None
    resposta: Optional[str] = None


class FeedbackResponder(BaseSchema):
    resposta: str = Field(min_length=1, max_length=2000)


# ── Portal do ministro ───────────────────────────────────────────────────────

class MinistroPortalOut(BaseSchema):
    id: int
    nome: str
    email: str
    funcao: Optional[str] = None


class CalendarioMinistroEventoOut(BaseSchema):
    evento_id: int
    nome: str
    data: date
    horario: str
    tipo_evento: Optional[str] = None
    local: Optional[str] = None
    cancelado: bool = False
    escala_id: Optional[int] = None
    status_escala: Optional[str] = None
    escalado: bool = False
    funcao_ministro: Optional[str] = None
    confirmacao_ministro: bool = False
    feedback_enviado: bool = False
    feedback_disponivel: bool = False


class FeedbackMinistroIn(BaseSchema):
    evento_id: int
    nota: int = Field(ge=1, le=10)
    comentario: Optional[str] = Field(default=None, max_length=2000)


class FeedbackMinistroOut(FeedbackOut):
    evento_nome: str
    evento_data: date
    evento_horario: str
    evento_local: Optional[str] = None


# ── Escala Preview ────────────────────────────────────────────────────────────

class MinistroExcluidoOut(BaseSchema):
    id: int
    nome: str
    funcao: Optional[str] = None
    escalas_mes: int = 0
    motivo_exclusao: str


class MinistroEmpatadoOut(BaseSchema):
    id: int
    nome: str
    funcao: Optional[str] = None
    escalas_mes: int = 0


class PreviewEscalaOut(BaseSchema):
    evento_id: int
    vagas: int
    definitivos: List[MinistroEmpatadoOut]
    empatados: List[MinistroEmpatadoOut]
    vagas_no_empate: int
    selecionados_auto: List[int]
    excluidos: List[MinistroExcluidoOut]
    tem_empate: bool


class GerarEscalaIn(BaseSchema):
    ministro_ids_manuais: Optional[List[int]] = None


class SubstituirEscalaIn(BaseSchema):
    ministro_id: int
    substituto_id: Optional[int] = None


# ── LogAuditoria ──────────────────────────────────────────────────────────────

class LogAuditoriaOut(BaseSchema):
    id: int
    entidade: str
    acao: Optional[str] = None
    status_anterior: Optional[str] = None
    status_novo: Optional[str] = None
    realizado_por_id: Optional[str] = None
    data_hora: Optional[datetime] = None
