from __future__ import annotations
from datetime import date, datetime
from typing import List, Optional
from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class BaseSchema(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        alias_generator=to_camel,
        populate_by_name=True,
    )


# ── Indisponibilidade ─────────────────────────────────────────────────────────

class IndisponibilidadeIn(BaseSchema):
    data: date
    horario_inicio: Optional[str] = None
    horario_fim: Optional[str] = None
    motivo: Optional[str] = None


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
    nota: int
    comentario: Optional[str] = None


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
    resposta: str


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
