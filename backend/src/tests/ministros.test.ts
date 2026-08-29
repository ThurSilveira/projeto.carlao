import assert from "node:assert/strict";
import type { Server } from "node:http";
import type { AddressInfo } from "node:net";
import test from "node:test";
import { createApp } from "../app";
import type { MinistroRepository } from "../repositories/MinistroRepository";
import { MinistroService } from "../services/MinistroService";
import {
  CreateMinistroDTO,
  MinistroEntity,
  UpdateMinistroDTO,
} from "../types/Ministro";

class InMemoryMinistroRepository implements MinistroRepository {
  private readonly data = new Map<number, MinistroEntity>();
  private nextId = 1;

  async findAll(): Promise<MinistroEntity[]> {
    return [...this.data.values()].sort((a, b) => a.nome.localeCompare(b.nome));
  }

  async findById(id: number): Promise<MinistroEntity | null> {
    return this.data.get(id) ?? null;
  }

  async findByEmail(email: string): Promise<MinistroEntity | null> {
    return [...this.data.values()].find((item) => item.email === email) ?? null;
  }

  async create(input: CreateMinistroDTO): Promise<MinistroEntity> {
    const ministro: MinistroEntity = {
      id: this.nextId++,
      nome: input.nome,
      email: input.email,
      telefone: input.telefone ?? null,
      dataNascimento: input.dataNascimento ?? null,
      observacoes: input.observacoes ?? null,
      ativo: input.ativo ?? true,
      visitasAoInfermo: input.visitasAoInfermo ?? false,
      statusCurso: input.statusCurso ?? false,
      escalasMes: input.escalasMes ?? 0,
      funcao: input.funcao ?? "LEITURA",
      funcaoEspecificada: input.funcaoEspecificada ?? null,
    };
    this.data.set(ministro.id, ministro);
    return ministro;
  }

  async update(id: number, input: UpdateMinistroDTO): Promise<MinistroEntity> {
    const current = this.data.get(id);
    if (!current) throw new Error("Registro ausente");
    const updated: MinistroEntity = { ...current, ...input };
    this.data.set(id, updated);
    return updated;
  }

  async remove(id: number): Promise<void> {
    this.data.delete(id);
  }
}

async function closeServer(server: Server): Promise<void> {
  await new Promise<void>((resolve, reject) => {
    server.close((error) => (error ? reject(error) : resolve()));
  });
}

test("executa o fluxo completo do CRUD de ministros", async () => {
  const repository = new InMemoryMinistroRepository();
  const app = createApp(new MinistroService(repository));
  const server = app.listen(0);

  try {
    const address = server.address() as AddressInfo;
    const baseUrl = `http://127.0.0.1:${address.port}`;

    const invalidCreate = await fetch(`${baseUrl}/api/ministros`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ nome: "A" }),
    });
    assert.equal(invalidCreate.status, 400);

    const createdResponse = await fetch(`${baseUrl}/api/ministros`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({
        nome: "  Maria da Silva  ",
        email: "MARIA@EXAMPLE.COM",
        telefone: "(11) 99999-9999",
        dataNascimento: "1990-04-15",
        funcao: "EUCARISTIA",
      }),
    });
    assert.equal(createdResponse.status, 201);
    const created = await createdResponse.json() as Record<string, unknown>;
    assert.equal(created.id, 1);
    assert.equal(created.nome, "Maria da Silva");
    assert.equal(created.email, "maria@example.com");
    assert.equal(created.dataNascimento, "1990-04-15");
    assert.equal(created.ativo, true);

    const duplicateResponse = await fetch(`${baseUrl}/api/ministros`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({
        nome: "Outra Maria",
        email: "maria@example.com",
      }),
    });
    assert.equal(duplicateResponse.status, 409);

    const listResponse = await fetch(`${baseUrl}/api/ministros`);
    assert.equal(listResponse.status, 200);
    const list = await listResponse.json() as unknown[];
    assert.equal(list.length, 1);

    const getResponse = await fetch(`${baseUrl}/api/ministros/1`);
    assert.equal(getResponse.status, 200);

    const updateResponse = await fetch(`${baseUrl}/api/ministros/1`, {
      method: "PUT",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ nome: "Maria Souza", ativo: false }),
    });
    assert.equal(updateResponse.status, 200);
    const updated = await updateResponse.json() as Record<string, unknown>;
    assert.equal(updated.nome, "Maria Souza");
    assert.equal(updated.ativo, false);
    assert.equal(updated.email, "maria@example.com");

    const invalidPatch = await fetch(`${baseUrl}/api/ministros/1`, {
      method: "PATCH",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ dataNascimento: "2026-02-30" }),
    });
    assert.equal(invalidPatch.status, 400);

    const invalidId = await fetch(`${baseUrl}/api/ministros/invalido`);
    assert.equal(invalidId.status, 400);

    const deleteResponse = await fetch(`${baseUrl}/api/ministros/1`, {
      method: "DELETE",
    });
    assert.equal(deleteResponse.status, 204);

    const missingResponse = await fetch(`${baseUrl}/api/ministros/1`);
    assert.equal(missingResponse.status, 404);

    const unknownRoute = await fetch(`${baseUrl}/rota-inexistente`);
    assert.equal(unknownRoute.status, 404);
  } finally {
    await closeServer(server);
  }
});
