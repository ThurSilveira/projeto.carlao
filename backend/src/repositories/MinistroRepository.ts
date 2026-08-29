import { prisma } from "../config/prisma";
import {
  CreateMinistroDTO,
  MinistroEntity,
  UpdateMinistroDTO,
} from "../types/Ministro";

export interface MinistroRepository {
  findAll(): Promise<MinistroEntity[]>;
  findById(id: number): Promise<MinistroEntity | null>;
  findByEmail(email: string): Promise<MinistroEntity | null>;
  create(data: CreateMinistroDTO): Promise<MinistroEntity>;
  update(id: number, data: UpdateMinistroDTO): Promise<MinistroEntity>;
  remove(id: number): Promise<void>;
}

export class PrismaMinistroRepository implements MinistroRepository {
  findAll(): Promise<MinistroEntity[]> {
    return prisma.ministro.findMany({ orderBy: { nome: "asc" } });
  }

  findById(id: number): Promise<MinistroEntity | null> {
    return prisma.ministro.findUnique({ where: { id } });
  }

  findByEmail(email: string): Promise<MinistroEntity | null> {
    return prisma.ministro.findUnique({ where: { email } });
  }

  create(data: CreateMinistroDTO): Promise<MinistroEntity> {
    return prisma.ministro.create({ data });
  }

  update(id: number, data: UpdateMinistroDTO): Promise<MinistroEntity> {
    return prisma.ministro.update({ where: { id }, data });
  }

  async remove(id: number): Promise<void> {
    await prisma.ministro.delete({ where: { id } });
  }
}

export const ministroRepository = new PrismaMinistroRepository();
