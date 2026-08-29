import { AppError } from "../middlewares/AppError";
import type { MinistroRepository } from "../repositories/MinistroRepository";
import {
  MinistroResponse,
  toMinistroResponse,
} from "../types/Ministro";
import {
  validateCreateMinistro,
  validateUpdateMinistro,
} from "../validators/MinistroValidator";

export interface MinistroServiceContract {
  list(): Promise<MinistroResponse[]>;
  getById(id: number): Promise<MinistroResponse>;
  create(input: unknown): Promise<MinistroResponse>;
  update(id: number, input: unknown): Promise<MinistroResponse>;
  remove(id: number): Promise<void>;
}

export class MinistroService implements MinistroServiceContract {
  constructor(private readonly repository: MinistroRepository) {}

  async list(): Promise<MinistroResponse[]> {
    const ministros = await this.repository.findAll();
    return ministros.map(toMinistroResponse);
  }

  async getById(id: number): Promise<MinistroResponse> {
    const ministro = await this.repository.findById(id);
    if (!ministro) throw new AppError("Ministro não encontrado.", 404);
    return toMinistroResponse(ministro);
  }

  async create(input: unknown): Promise<MinistroResponse> {
    const data = validateCreateMinistro(input);
    await this.ensureUniqueEmail(data.email);
    return toMinistroResponse(await this.repository.create(data));
  }

  async update(id: number, input: unknown): Promise<MinistroResponse> {
    const current = await this.repository.findById(id);
    if (!current) throw new AppError("Ministro não encontrado.", 404);

    const data = validateUpdateMinistro(input);
    if (data.email && data.email !== current.email) {
      await this.ensureUniqueEmail(data.email, id);
    }

    return toMinistroResponse(await this.repository.update(id, data));
  }

  async remove(id: number): Promise<void> {
    const current = await this.repository.findById(id);
    if (!current) throw new AppError("Ministro não encontrado.", 404);
    await this.repository.remove(id);
  }

  private async ensureUniqueEmail(email: string, currentId?: number): Promise<void> {
    const existing = await this.repository.findByEmail(email);
    if (existing && existing.id !== currentId) {
      throw new AppError("Já existe um ministro com este e-mail.", 409);
    }
  }
}
