import axios from 'axios';
import type {
  AuthSession,
  Ministro,
  Evento,
  Escala,
  Feedback,
  LogAuditoria,
  Indisponibilidade,
  PreviewEscala,
} from '@/types';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';
const CSRF_STORAGE_KEY = 'escala_csrf_token';
export const AUTH_UNAUTHORIZED_EVENT = 'escala:unauthorized';

let csrfToken = sessionStorage.getItem(CSRF_STORAGE_KEY);

export const setCsrfToken = (token: string | null): void => {
  csrfToken = token;
  if (token) sessionStorage.setItem(CSRF_STORAGE_KEY, token);
  else sessionStorage.removeItem(CSRF_STORAGE_KEY);
};

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    'X-Requested-With': 'XMLHttpRequest',
  },
});

api.interceptors.request.use((config) => {
  const method = config.method?.toUpperCase() ?? 'GET';
  if (csrfToken && !['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    config.headers.set('X-CSRF-Token', csrfToken);
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
      err.message = 'O servidor está iniciando, aguarde alguns segundos e tente novamente.';
    } else if (!err.response) {
      err.message = 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    const requestUrl = String(err.config?.url ?? '');
    if (err.response?.status === 401 && !requestUrl.endsWith('/auth/login')) {
      setCsrfToken(null);
      window.dispatchEvent(new Event(AUTH_UNAUTHORIZED_EVENT));
    }
    return Promise.reject(err);
  },
);

// ── Autenticação ──────────────────────────────────────────────────────────────

export const AuthService = {
  login: async (email: string, senha: string): Promise<AuthSession> => {
    const res = await api.post<AuthSession>('/auth/login', { email, senha });
    setCsrfToken(res.data.csrfToken);
    return res.data;
  },

  me: async (): Promise<AuthSession> => {
    const res = await api.get<AuthSession>('/auth/me');
    setCsrfToken(res.data.csrfToken);
    return res.data;
  },

  logout: async (): Promise<void> => {
    await api.post('/auth/logout');
    setCsrfToken(null);
  },

  changePassword: async (senhaAtual: string, novaSenha: string): Promise<AuthSession> => {
    const res = await api.put<AuthSession>('/auth/password', { senhaAtual, novaSenha });
    setCsrfToken(res.data.csrfToken);
    return res.data;
  },
};

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

  previewEscala: async (eventoId: number): Promise<PreviewEscala> => {
    const res = await api.get(`/escalas/preview/${eventoId}`);
    return res.data;
  },

  previewSubstituicao: async (escalaId: number, ministroId: number): Promise<PreviewEscala> => {
    const res = await api.get(`/escalas/${escalaId}/substituir/preview/${ministroId}`);
    return res.data;
  },

  gerarEscala: async (eventoId: number, ministroIdsManuais?: number[]): Promise<Escala> => {
    const body = ministroIdsManuais ? { ministroIdsManuais } : undefined;
    const res = await api.post(`/escalas/gerar/${eventoId}`, body);
    return res.data;
  },

  approveEscala: async (id: number): Promise<Escala> => {
    const res = await api.put(`/escalas/${id}/aprovar`);
    return res.data;
  },

  substituirEscala: async (id: number, ministroId: number, substitutoId?: number): Promise<Escala> => {
    const body = { ministroId, substitutoId };
    const res = await api.put(`/escalas/${id}/substituir`, body);
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

// ── Indisponibilidades ────────────────────────────────────────────────────────

export const IndisponibilidadeService = {
  listar: async (ministroId: number): Promise<Indisponibilidade[]> => {
    const res = await api.get(`/ministros/${ministroId}/indisponibilidades`);
    return res.data;
  },
  criar: async (ministroId: number, dto: Partial<Indisponibilidade>): Promise<Indisponibilidade> => {
    const res = await api.post(`/ministros/${ministroId}/indisponibilidades`, dto);
    return res.data;
  },
  atualizar: async (ministroId: number, id: number, dto: Partial<Indisponibilidade>): Promise<Indisponibilidade> => {
    const res = await api.put(`/ministros/${ministroId}/indisponibilidades/${id}`, dto);
    return res.data;
  },
  deletar: async (ministroId: number, id: number): Promise<void> => {
    await api.delete(`/ministros/${ministroId}/indisponibilidades/${id}`);
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
