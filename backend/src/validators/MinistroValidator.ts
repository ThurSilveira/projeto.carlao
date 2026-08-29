import { AppError } from "../middlewares/AppError";
import {
  CreateMinistroDTO,
  FUNCOES_MINISTRO,
  FuncaoMinistro,
  UpdateMinistroDTO,
} from "../types/Ministro";

const CAMPOS_PERMITIDOS = new Set([
  "nome",
  "email",
  "telefone",
  "dataNascimento",
  "observacoes",
  "ativo",
  "visitasAoInfermo",
  "statusCurso",
  "escalasMes",
  "funcao",
  "funcaoEspecificada",
]);

function asBody(input: unknown): Record<string, unknown> {
  if (!input || typeof input !== "object" || Array.isArray(input)) {
    throw new AppError("O corpo da requisição deve ser um objeto JSON.");
  }

  const body = input as Record<string, unknown>;
  const desconhecidos = Object.keys(body).filter(
    (campo) => !CAMPOS_PERMITIDOS.has(campo),
  );

  if (desconhecidos.length > 0) {
    throw new AppError(`Campo(s) não permitido(s): ${desconhecidos.join(", ")}.`);
  }

  return body;
}

function requiredString(
  body: Record<string, unknown>,
  campo: "nome" | "email",
  maxLength: number,
): string {
  const value = body[campo];

  if (typeof value !== "string" || value.trim() === "") {
    throw new AppError(`O campo ${campo} é obrigatório.`);
  }

  const normalized = value.trim();
  if (normalized.length > maxLength) {
    throw new AppError(`O campo ${campo} deve ter no máximo ${maxLength} caracteres.`);
  }

  return normalized;
}

function optionalString(
  body: Record<string, unknown>,
  campo: "telefone" | "observacoes" | "funcaoEspecificada",
  maxLength: number,
): string | null | undefined {
  if (!(campo in body)) return undefined;

  const value = body[campo];
  if (value === null) return null;
  if (typeof value !== "string") {
    throw new AppError(`O campo ${campo} deve ser um texto ou nulo.`);
  }

  const normalized = value.trim();
  if (normalized.length > maxLength) {
    throw new AppError(`O campo ${campo} deve ter no máximo ${maxLength} caracteres.`);
  }

  return normalized || null;
}

function email(body: Record<string, unknown>): string {
  const normalized = requiredString(body, "email", 254).toLowerCase();
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailPattern.test(normalized)) {
    throw new AppError("O campo email deve conter um endereço válido.");
  }

  return normalized;
}

function optionalBoolean(
  body: Record<string, unknown>,
  campo: "ativo" | "visitasAoInfermo" | "statusCurso",
): boolean | undefined {
  if (!(campo in body)) return undefined;
  if (typeof body[campo] !== "boolean") {
    throw new AppError(`O campo ${campo} deve ser booleano.`);
  }
  return body[campo];
}

function optionalNonNegativeInteger(
  body: Record<string, unknown>,
): number | undefined {
  if (!("escalasMes" in body)) return undefined;
  const value = body.escalasMes;

  if (!Number.isInteger(value) || (value as number) < 0) {
    throw new AppError("O campo escalasMes deve ser um inteiro não negativo.");
  }

  return value as number;
}

function optionalDate(body: Record<string, unknown>): Date | null | undefined {
  if (!("dataNascimento" in body)) return undefined;
  const value = body.dataNascimento;
  if (value === null || value === "") return null;

  if (typeof value !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    throw new AppError("O campo dataNascimento deve usar o formato AAAA-MM-DD.");
  }

  const parsed = new Date(`${value}T00:00:00.000Z`);
  if (Number.isNaN(parsed.getTime()) || parsed.toISOString().slice(0, 10) !== value) {
    throw new AppError("O campo dataNascimento contém uma data inválida.");
  }

  return parsed;
}

function optionalFuncao(
  body: Record<string, unknown>,
): FuncaoMinistro | undefined {
  if (!("funcao" in body)) return undefined;
  const value = body.funcao;

  if (
    typeof value !== "string" ||
    !FUNCOES_MINISTRO.includes(value as FuncaoMinistro)
  ) {
    throw new AppError(
      `O campo funcao deve ser um destes valores: ${FUNCOES_MINISTRO.join(", ")}.`,
    );
  }

  return value as FuncaoMinistro;
}

function applyOptionalFields(
  body: Record<string, unknown>,
  data: UpdateMinistroDTO,
): void {
  const telefone = optionalString(body, "telefone", 30);
  const dataNascimento = optionalDate(body);
  const observacoes = optionalString(body, "observacoes", 2000);
  const ativo = optionalBoolean(body, "ativo");
  const visitasAoInfermo = optionalBoolean(body, "visitasAoInfermo");
  const statusCurso = optionalBoolean(body, "statusCurso");
  const escalasMes = optionalNonNegativeInteger(body);
  const funcao = optionalFuncao(body);
  const funcaoEspecificada = optionalString(body, "funcaoEspecificada", 120);

  if (telefone !== undefined) data.telefone = telefone;
  if (dataNascimento !== undefined) data.dataNascimento = dataNascimento;
  if (observacoes !== undefined) data.observacoes = observacoes;
  if (ativo !== undefined) data.ativo = ativo;
  if (visitasAoInfermo !== undefined) data.visitasAoInfermo = visitasAoInfermo;
  if (statusCurso !== undefined) data.statusCurso = statusCurso;
  if (escalasMes !== undefined) data.escalasMes = escalasMes;
  if (funcao !== undefined) data.funcao = funcao;
  if (funcaoEspecificada !== undefined) {
    data.funcaoEspecificada = funcaoEspecificada;
  }
}

export function validateCreateMinistro(input: unknown): CreateMinistroDTO {
  const body = asBody(input);
  const nome = requiredString(body, "nome", 120);

  if (nome.length < 3) {
    throw new AppError("O campo nome deve ter ao menos 3 caracteres.");
  }

  const data: CreateMinistroDTO = { nome, email: email(body) };
  applyOptionalFields(body, data);
  return data;
}

export function validateUpdateMinistro(input: unknown): UpdateMinistroDTO {
  const body = asBody(input);
  if (Object.keys(body).length === 0) {
    throw new AppError("Informe ao menos um campo para atualização.");
  }

  const data: UpdateMinistroDTO = {};

  if ("nome" in body) {
    const nome = requiredString(body, "nome", 120);
    if (nome.length < 3) {
      throw new AppError("O campo nome deve ter ao menos 3 caracteres.");
    }
    data.nome = nome;
  }

  if ("email" in body) data.email = email(body);
  applyOptionalFields(body, data);
  return data;
}
