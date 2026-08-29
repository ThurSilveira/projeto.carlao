# CRUD de ministros — TypeScript + Prisma

A API está na pasta `backend/` e expõe o CRUD de ministros em
`/api/ministros`.

## Como executar

```bash
cd backend
cp .env.example .env
npm install
npx prisma migrate dev
npm run dev
```

O PostgreSQL informado em `DATABASE_URL` deve estar disponível antes da
migração. A API inicia por padrão em `http://localhost:3000`.

## Rotas

| Método | Rota | Resultado |
| --- | --- | --- |
| `GET` | `/api/ministros` | Lista ministros |
| `GET` | `/api/ministros/:id` | Busca um ministro |
| `POST` | `/api/ministros` | Cria um ministro |
| `PUT`/`PATCH` | `/api/ministros/:id` | Atualiza campos informados |
| `DELETE` | `/api/ministros/:id` | Exclui um ministro |

Exemplo mínimo para criação:

```json
{
  "nome": "Maria da Silva",
  "email": "maria@example.com"
}
```

Use `npm run check` dentro de `backend/` para validar o schema, executar os
testes e compilar o projeto.
