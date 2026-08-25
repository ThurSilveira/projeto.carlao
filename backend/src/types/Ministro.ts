export interface CreateMinistroDTO {
  nome: string;
  email?: string;
  telefone?: string;
  dataNascimento?: Date;
  observacoes?: string;
  ativo?: boolean;
  visitasAoInfermo?: boolean;
  statusCurso?: boolean;
  escalasMes?: number;
  funcaoEspecífica?: string;
}

export interface UpdateMinistroDTO {
  nome?: string;
  email?: string;
  telefone?: string;
  dataNascimento?: Date;
  observacoes?: string;
  ativo?: boolean;
  visitasAoInfermo?: boolean;
  statusCurso?: boolean;
  escalasMes?: number;
  funcaoEspecífica?: string;
}