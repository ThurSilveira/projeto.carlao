import { ErrorRequestHandler, RequestHandler } from "express";
import { AppError } from "./AppError";

interface ErrorWithCode {
  code?: unknown;
}

export const notFoundHandler: RequestHandler = (request, response) => {
  response.status(404).json({
    message: `Rota não encontrada: ${request.method} ${request.originalUrl}`,
  });
};

export const errorHandler: ErrorRequestHandler = (
  error: unknown,
  _request,
  response,
  _next,
) => {
  if (error instanceof AppError) {
    response.status(error.statusCode).json({ message: error.message });
    return;
  }

  if (error instanceof SyntaxError && "body" in error) {
    response.status(400).json({ message: "O corpo JSON da requisição é inválido." });
    return;
  }

  const code = (error as ErrorWithCode | null)?.code;
  if (code === "P2002") {
    response.status(409).json({ message: "Já existe um ministro com este e-mail." });
    return;
  }
  if (code === "P2025") {
    response.status(404).json({ message: "Ministro não encontrado." });
    return;
  }
  if (code === "P2003") {
    response.status(409).json({
      message: "O ministro possui registros relacionados e não pode ser excluído.",
    });
    return;
  }

  console.error(error);
  response.status(500).json({ message: "Erro interno no servidor." });
};
