from datetime import date, datetime
from sqlalchemy import Boolean, Column, Date, DateTime, ForeignKey, Integer, String
from sqlalchemy.orm import relationship
from app.database import Base


class Ministro(Base):
    __tablename__ = "ministro"

    id = Column(Integer, primary_key=True, index=True)
    nome = Column(String, nullable=False)
    email = Column(String, unique=True, nullable=False)
    telefone = Column(String)
    data_nascimento = Column(Date)
    observacoes = Column(String)
    ativo = Column(Boolean, nullable=False, default=True)
    visitas_ao_infermo = Column(Boolean, nullable=False, default=False)
    status_curso = Column(Boolean, nullable=False, default=False)
    escalas_mes = Column(Integer, default=0)
    funcao = Column(String, nullable=False, default="LEITURA")
    funcao_especificada = Column(String)

    indisponibilidades = relationship("Indisponibilidade", back_populates="ministro", cascade="all, delete-orphan")
    escala_ministros = relationship("EscalaMinistro", back_populates="ministro")
    feedbacks = relationship("Feedback", back_populates="ministro")


class Evento(Base):
    __tablename__ = "evento"

    id = Column(Integer, primary_key=True, index=True)
    nome = Column(String, nullable=False)
    data = Column(Date, nullable=False)
    horario = Column(String, nullable=False)
    tipo_evento = Column(String, nullable=False, default="MISSA_PAROQUIAL")
    max_ministros = Column(Integer, default=6)
    local = Column(String)
    cancelado = Column(Boolean, nullable=False, default=False)
    tipo_especificado = Column(String)

    escalas = relationship("Escala", back_populates="evento")
    feedbacks = relationship("Feedback", back_populates="evento")


class Escala(Base):
    __tablename__ = "escala"

    id = Column(Integer, primary_key=True, index=True)
    evento_id = Column(Integer, ForeignKey("evento.id"), nullable=False)
    data_atribuicao = Column(Date, default=date.today)
    observacao = Column(String)
    status = Column(String, nullable=False, default="PROPOSTA")

    evento = relationship("Evento", back_populates="escalas")
    escala_ministros = relationship("EscalaMinistro", back_populates="escala", cascade="all, delete-orphan")


class EscalaMinistro(Base):
    __tablename__ = "escala_ministro"

    id = Column(Integer, primary_key=True, index=True)
    escala_id = Column(Integer, ForeignKey("escala.id"), nullable=False)
    ministro_id = Column(Integer, ForeignKey("ministro.id"), nullable=False)
    confirmacao_ministro = Column(Boolean, nullable=False, default=False)
    data_confirmacao = Column(Date)
    substituido = Column(Boolean, nullable=False, default=False)

    escala = relationship("Escala", back_populates="escala_ministros")
    ministro = relationship("Ministro", back_populates="escala_ministros")


class Indisponibilidade(Base):
    __tablename__ = "indisponibilidade"

    id = Column(Integer, primary_key=True, index=True)
    ministro_id = Column(Integer, ForeignKey("ministro.id"), nullable=False)
    data = Column(Date, nullable=False)
    horario_inicio = Column(String)
    horario_fim = Column(String)
    motivo = Column(String)

    ministro = relationship("Ministro", back_populates="indisponibilidades")


class Feedback(Base):
    __tablename__ = "feedback"

    id = Column(Integer, primary_key=True, index=True)
    ministro_id = Column(Integer, ForeignKey("ministro.id"), nullable=False)
    evento_id = Column(Integer, ForeignKey("evento.id"), nullable=False)
    nota = Column(Integer, nullable=False)
    comentario = Column(String)
    data_envio = Column(DateTime, default=datetime.now)
    status = Column(String, nullable=False, default="PENDENTE")
    resposta = Column(String)

    ministro = relationship("Ministro", back_populates="feedbacks")
    evento = relationship("Evento", back_populates="feedbacks")


class LogAuditoria(Base):
    __tablename__ = "log_auditoria"

    id = Column(Integer, primary_key=True, index=True)
    entidade = Column(String, nullable=False)
    acao = Column(String, nullable=False)
    status_anterior = Column(String)
    status_novo = Column(String)
    realizado_por_id = Column(String)
    data_hora = Column(DateTime, nullable=False, default=datetime.now)
