import axios from 'axios';
import { Ministro, Evento, Escala, Feedback, LogAuditoria } from '@/types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// ── Ministros ─────────────────────────────────────────────────────────────────

export const MinistroService = {
  getAllMinistros: async (): Promise<Ministro[]> => {
    const res = await api.get('/ministros');
    return res.data;
  },

  getMinistroById: async (id: number): Promise<Ministro> => {
    const res = await api.get(`/ministros/${id}`);
    return res.data;
  },

  createMinistro: async (ministro: Partial<Ministro>): Promise<Ministro> => {
    const res = await api.post('/ministros', ministro);
    return res.data;
  },

  updateMinistro: async (id: number, ministro: Partial<Ministro>): Promise<Ministro> => {
    const res = await api.put(`/ministros/${id}`, ministro);
    return res.data;
  },

  deleteMinistro: async (id: number): Promise<void> => {
    await api.delete(`/ministros/${id}`);
  },
};

// ── Eventos ───────────────────────────────────────────────────────────────────

export const EventoService = {
  getAllEventos: async (): Promise<Evento[]> => {
    const res = await api.get('/eventos');
    return res.data;
  },

  getEventoById: async (id: number): Promise<Evento> => {
    const res = await api.get(`/eventos/${id}`);
    return res.data;
  },

  createEvento: async (evento: Partial<Evento>): Promise<Evento> => {
    const res = await api.post('/eventos', evento);
    return res.data;
  },

  updateEvento: async (id: number, evento: Partial<Evento>): Promise<Evento> => {
    const res = await api.put(`/eventos/${id}`, evento);
    return res.data;
  },

  cancelEvento: async (id: number): Promise<Evento> => {
    const res = await api.put(`/eventos/${id}/cancelar`);
    return res.data;
  },

  deleteEvento: async (id: number): Promise<void> => {
    await api.delete(`/eventos/${id}`);
  },
};

// ── Escalas ───────────────────────────────────────────────────────────────────

export const EscalaService = {
  getAllEscalas: async (): Promise<Escala[]> => {
    const res = await api.get('/escalas');
    return res.data;
  },

  getEscalaById: async (id: number): Promise<Escala> => {
    const res = await api.get(`/escalas/${id}`);
    return res.data;
  },

  createEscala: async (escala: Partial<Escala>): Promise<Escala> => {
    const res = await api.post('/escalas', escala);
    return res.data;
  },

  approveEscala: async (id: number): Promise<Escala> => {
    const res = await api.put(`/escalas/${id}/aprovar`);
    return res.data;
  },

  cancelEscala: async (id: number): Promise<Escala> => {
    const res = await api.put(`/escalas/${id}/cancelar`);
    return res.data;
  },

  deleteEscala: async (id: number): Promise<void> => {
    await api.delete(`/escalas/${id}`);
  },
};

// ── Feedbacks ─────────────────────────────────────────────────────────────────

export const FeedbackService = {
  getAllFeedbacks: async (): Promise<Feedback[]> => {
    const res = await api.get('/feedbacks');
    return res.data;
  },

  createFeedback: async (feedback: Partial<Feedback>): Promise<Feedback> => {
    const res = await api.post('/feedbacks', feedback);
    return res.data;
  },

  answerFeedback: async (id: number, resposta: string): Promise<Feedback> => {
    const res = await api.put(`/feedbacks/${id}/responder`, { resposta });
    return res.data;
  },
};

// ── Auditoria ─────────────────────────────────────────────────────────────────

export const LogAuditoriaService = {
  getLogs: async (): Promise<LogAuditoria[]> => {
    const res = await api.get('/auditoria');
    return res.data;
  },
};

// ── Health ────────────────────────────────────────────────────────────────────

export const healthCheck = async (): Promise<boolean> => {
  try {
    const res = await api.get('/public/health');
    return res.status === 200;
  } catch {
    return false;
  }
};

export default api;
