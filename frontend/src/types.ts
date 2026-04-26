// Enums — sincronizados com backend model/enums/
export enum FuncaoMinistro {
  EUCARISTIA = 'EUCARISTIA',
  LEITURA = 'LEITURA',
  ACOLHIMENTO = 'ACOLHIMENTO',
  MUSICA = 'MUSICA',
  CATEQUESE = 'CATEQUESE',
  ADORACAO = 'ADORACAO',
  OUTRO = 'OUTRO',
}

export enum StatusEscala {
  PROPOSTA = 'PROPOSTA',
  APROVADA = 'APROVADA',
  CONFIRMADA = 'CONFIRMADA',
  CANCELADA = 'CANCELADA',
}

export enum StatusFeedback {
  PENDENTE = 'PENDENTE',
  RESPONDIDO = 'RESPONDIDO',
  ARQUIVADO = 'ARQUIVADO',
}

export enum TipoAcao {
  CRIADO = 'CRIADO',
  ATUALIZADO = 'ATUALIZADO',
  DELETADO = 'DELETADO',
  APROVADO = 'APROVADO',
  CANCELADO = 'CANCELADO',
  SUBSTITUIDO = 'SUBSTITUIDO',
  CONFIRMADO = 'CONFIRMADO',
  NOTIFICADO = 'NOTIFICADO',
}

export enum TipoEvento {
  MISSA_PAROQUIAL = 'MISSA_PAROQUIAL',
  MISSA_ESPECIAL = 'MISSA_ESPECIAL',
  RETIRO = 'RETIRO',
  BATIZADO = 'BATIZADO',
  CASAMENTO = 'CASAMENTO',
  ADORACAO = 'ADORACAO',
  OUTRO = 'OUTRO',
}

export enum TipoIndisponibilidade {
  PROGRAMADA = 'PROGRAMADA',
  SURPRESA = 'SURPRESA',
}

export interface Ministro {
  id?: number;
  nome: string;
  telefone: string;
  email: string;
  dataNascimento: string;
  observacoes?: string;
  ativo: boolean;
  funcao: FuncaoMinistro;
  funcaoEspecificada?: string;
  visitasAoInfermo: boolean;
  statusCurso: boolean;
  escalasMes?: number;
  aptidoes: TipoEvento[];
  indisponibilidades: Indisponibilidade[];
  escalasAgendadas?: string[];
  disponibilidadesRec: DisponibilidadeRecorrente[];
}

export interface Coordenador {
  id?: number;
  nome: string;
  telefone: string;
  email: string;
  dataNascimento: string;
  observacoes?: string;
  ativo: boolean;
  vidaSacramental: boolean;
  provisaoAtiva: boolean;
  tempoServico: number;
}

export interface Paroquia {
  id?: number;
  nome: string;
  cnpj: string;
  endereco: string;
  coordenadores: Coordenador[];
  ministros: Ministro[];
}

export interface Evento {
  id?: number;
  nome: string;
  data: string;
  horario: string;
  tipoEvento: TipoEvento;
  tipoEspecificado?: string;
  maxMinistros: number;
  local: string;
  cancelado: boolean;
}

export interface Escala {
  id?: number;
  dataAtribuicao: string;
  observacao: string;
  status: StatusEscala;
  eventoId: number;
  evento?: Evento;
  periodoEscalaId?: number;
  escalaMinistros: EscalaMinistro[];
}

export interface EscalaMinistro {
  id?: number;
  ministroId: number;
  ministroNome?: string;
  ministroFuncao?: string;
  escalaId: number;
  confirmacaoMinistro: boolean;
  dataConfirmacao?: string;
  substituido: boolean;
}

export interface Indisponibilidade {
  id?: number;
  ministroId?: number;
  data: string;
  horarioInicio: string;
  horarioFim: string;
  motivo: string;
}

export interface DisponibilidadeRecorrente {
  id?: number;
  diaSemana: number;
  horarioInicio: string;
  horarioFim: string;
  ativo: boolean;
}

export interface Substituicao {
  id?: number;
  ministroOriginalId: number;
  ministroSubstitutoId: number;
  dataSubstituicao: string;
  motivo: string;
}

export interface Feedback {
  id?: number;
  ministroId: number;
  eventoId: number;
  nota: number;
  comentario: string;
  dataEnvio: string;
  status: StatusFeedback;
  resposta?: string;
}

export interface LogAuditoria {
  id?: number;
  entidade: string;
  acao: TipoAcao;
  statusAnterior: string;
  statusNovo: string;
  realizadoPorId?: string;
  dataHora: string;
}
