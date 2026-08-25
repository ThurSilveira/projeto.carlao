import { prisma } from "../config/prisma";
import { CreateMinistroDTO } from "../types/Ministro";

export function findAll() {
  return prisma.ministro.findMany();
}

export function findById(id: number) {
  return prisma.ministro.findUnique({
    where: { id },
  });
}

export function create(data: CreateMinistroDTO) {
  return prisma.ministro.create({ data });
}

import { UpdateMinistroDTO } from "../types/Ministro";

export function update(
  id: number,
  data: UpdateMinistroDTO
) {
  return prisma.ministro.update({
    where: { id },
    data,
  });
}

export function remove(id: number) {
  return prisma.ministro.delete({
    where: { id },
  });
}