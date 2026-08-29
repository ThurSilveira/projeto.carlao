export const FUNCOES_MINISTRO = [
  "EUCARISTIA",
  "LEITURA",
  "ACOLHIMENTO",
  "MUSICA",
  "CATEQUESE",
  "ADORACAO",
  "OUTRO",
] as const;

export type FuncaoMinistro = (typeof FUNCOES_MINISTRO)[number];

export interface MinistroEntity {
  id: number;
  nome: string;
  email: string;
  telefone: string | null;
  dataNascimento: Date | null;
  observacoes: string | null;
  ativo: boolean;
  visitasAoInfermo: boolean;
  statusCurso: boolean;
  escalasMes: number;
  funcao: string;
  funcaoEspecificada: string | null;
}

export interface CreateMinistroDTO {
  nome: string;
  email: string;
  telefone?: string | null;
  dataNascimento?: Date | null;
  observacoes?: string | null;
  ativo?: boolean;
  visitasAoInfermo?: boolean;
  statusCurso?: boolean;
  escalasMes?: number;
  funcao?: FuncaoMinistro;
  funcaoEspecificada?: string | null;
}

export type UpdateMinistroDTO = Partial<CreateMinistroDTO>;

export interface MinistroResponse
  extends Omit<MinistroEntity, "dataNascimento"> {
  dataNascimento: string | null;
}

export function toMinistroResponse(ministro: MinistroEntity): MinistroResponse {
  return {
    ...ministro,
    dataNascimento: ministro.dataNascimento
      ? ministro.dataNascimento.toISOString().slice(0, 10)
      : null,
  };
}
