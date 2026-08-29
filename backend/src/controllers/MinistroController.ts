import { Request, Response } from "express";
import { AppError } from "../middlewares/AppError";
import type { MinistroServiceContract } from "../services/MinistroService";

function parseId(value: string | string[] | undefined): number {
  if (Array.isArray(value)) {
    throw new AppError("O id do ministro deve ser um inteiro positivo.");
  }
  const id = Number(value);
  if (!Number.isSafeInteger(id) || id <= 0) {
    throw new AppError("O id do ministro deve ser um inteiro positivo.");
  }
  return id;
}

export function createMinistroController(service: MinistroServiceContract) {
  return {
    list: async (_request: Request, response: Response): Promise<void> => {
      response.json(await service.list());
    },

    getById: async (request: Request, response: Response): Promise<void> => {
      response.json(await service.getById(parseId(request.params.id)));
    },

    create: async (request: Request, response: Response): Promise<void> => {
      response.status(201).json(await service.create(request.body));
    },

    update: async (request: Request, response: Response): Promise<void> => {
      response.json(await service.update(parseId(request.params.id), request.body));
    },

    remove: async (request: Request, response: Response): Promise<void> => {
      await service.remove(parseId(request.params.id));
      response.status(204).send();
    },
  };
}
