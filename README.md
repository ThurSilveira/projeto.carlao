# Sistema de Gestão de Escalas Ministeriais

Sistema web para gerenciamento de escalas de ministros em paróquias: cadastro de ministros, eventos, escalas, feedbacks e auditoria completa de ações.

---

## Sumário

- [Stack](#stack)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Execução Local](#configuração-e-execução-local)
  - [Banco de Dados](#banco-de-dados)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Endpoints da API](#endpoints-da-api)
- [Deploy (Render)](#deploy-render)
- [Gerar Secrets Seguros](#gerar-secrets-seguros)

---

## Stack

| Camada | Tecnologia |
|---|---|
| **Backend** | Java 17 · Spring Boot 3.2 · Spring Security · Spring Data JPA |
| **Frontend** | React 18 · TypeScript 5 · Vite 5 · Tailwind CSS 3 |
| **Banco** | PostgreSQL 15 |
| **Auth** | JWT (jjwt 0.11) |
| **Deploy** | Render.com |

---

## Estrutura do Projeto

```
pj_bc/
├── backend/                        # API REST — Spring Boot
│   ├── src/main/java/com/exemplo/escala/
│   │   ├── config/                 # SecurityConfig, WebConfig (CORS), GlobalExceptionHandler
│   │   ├── controller/             # MinistroController, EventoController, EscalaController,
│   │   │                           # FeedbackController, LogAuditoriaController, HealthController
│   │   ├── dto/                    # DTOs de request/response
│   │   ├── model/
│   │   │   ├── enums/              # FuncaoMinistro, StatusEscala, StatusFeedback, TipoAcao, TipoEvento
│   │   │   └── *.java              # Ministro, Evento, Escala, EscalaMinistro, Feedback, LogAuditoria
│   │   ├── repository/             # Spring Data JPA repositories
│   │   └── service/                # Regras de negócio + auditoria automática
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/                       # SPA — React + TypeScript
│   ├── src/
│   │   ├── components/             # Layout, ui.tsx (Button, Card, Modal…), ErrorBoundary
│   │   ├── pages/                  # Dashboard, Ministros, Eventos, Escalas, Feedback, Auditoria
│   │   ├── services/api.ts         # Axios — todos os serviços REST
│   │   └── types.ts                # Interfaces e Enums TypeScript
│   ├── vite.config.ts
│   └── package.json
│
├── database-schema-postgresql.sql  # Schema de referência (estrutura completa)
├── database-seed-postgresql.sql    # Dados iniciais para desenvolvimento
├── GENERATE_SECRETS.sh             # Helper para gerar JWT_SECRET e DB_PASSWORD seguros
├── render.yaml                     # Configuração de deploy no Render
└── Procfile                        # Comando de start para o Render
```

---

## Pré-requisitos

- **Java 17+** — `java -version`
- **Maven 3.8+** — `mvn -version`
- **Node.js 18+** — `node -version`
- **PostgreSQL 15+** — rodando localmente ou via Docker
- (Opcional) **Docker** para subir o banco rapidamente

---

## Configuração e Execução Local

### Banco de Dados

**Opção 1 — PostgreSQL local:**
```bash
# Criar o banco (credenciais padrão: postgres/postgres)
createdb -U postgres escala_ministerial
```

**Opção 2 — Docker:**
```bash
docker run -d \
  --name escala-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=escala_ministerial \
  -p 5432:5432 \
  postgres:15
```

> O Hibernate cria/atualiza as tabelas automaticamente na primeira inicialização (`ddl-auto=update`).
> Para popular com dados de teste, execute `database-seed-postgresql.sql` **após** a primeira inicialização.

---

### Backend

```bash
cd backend

# 1. Copiar o arquivo de variáveis de ambiente
cp .env.example .env

# 2. Editar backend/.env com suas credenciais locais
#    DB_USERNAME, DB_PASSWORD, JWT_SECRET

# 3. Iniciar
mvn spring-boot:run
```

A API ficará disponível em **`http://localhost:8080/api`**.

Verificar saúde: `GET http://localhost:8080/api/public/health`

---

### Frontend

```bash
cd frontend

# 1. Instalar dependências
npm install

# 2. Copiar variáveis de ambiente
cp .env.example .env
# VITE_API_URL=http://localhost:8080/api  (já é o padrão)

# 3. Iniciar em modo desenvolvimento
npm run dev
```

O app ficará disponível em **`http://localhost:5173`**.

---

## Variáveis de Ambiente

### Backend (`backend/.env`)

| Variável | Padrão (dev) | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `escala_ministerial` | Nome do banco |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | **Trocar em produção** |
| `DATABASE_URL` | — | URL completa (sobrescreve os campos acima; usado pelo Render) |
| `JWT_SECRET` | valor padrão | **Obrigatório trocar em produção** (mín. 32 chars) |
| `JWT_EXPIRATION` | `86400` | Expiração do token em segundos (24h) |
| `SERVER_PORT` | `8080` | Porta do servidor |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Origens permitidas no CORS |
| `JPA_HIBERNATE_DDL_AUTO` | `update` | `validate` em produção |

### Frontend (`frontend/.env`)

| Variável | Padrão | Descrição |
|---|---|---|
| `VITE_API_URL` | `http://localhost:8080/api` | URL base da API |

---

## Endpoints da API

Base URL: `/api`

### Ministros
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/ministros` | Listar todos |
| `GET` | `/ministros/{id}` | Buscar por ID |
| `POST` | `/ministros` | Criar |
| `PUT` | `/ministros/{id}` | Atualizar |
| `DELETE` | `/ministros/{id}` | Deletar |

### Eventos
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/eventos` | Listar todos |
| `GET` | `/eventos/{id}` | Buscar por ID |
| `POST` | `/eventos` | Criar |
| `PUT` | `/eventos/{id}` | Atualizar |
| `PUT` | `/eventos/{id}/cancelar` | Cancelar evento |
| `DELETE` | `/eventos/{id}` | Deletar |

### Escalas
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/escalas` | Listar todas |
| `GET` | `/escalas/{id}` | Buscar por ID |
| `POST` | `/escalas` | Criar (status inicial: PROPOSTA) |
| `PUT` | `/escalas/{id}/aprovar` | Aprovar escala |
| `PUT` | `/escalas/{id}/cancelar` | Cancelar escala |
| `DELETE` | `/escalas/{id}` | Deletar |

### Feedbacks
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/feedbacks` | Listar todos |
| `POST` | `/feedbacks` | Criar |
| `PUT` | `/feedbacks/{id}/responder` | Responder feedback |

### Auditoria
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/auditoria` | Listar logs (ordem decrescente) |

### Saúde
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/public/health` | Status da API |

---

## Deploy (Render)

O projeto está configurado para deploy automático no [Render](https://render.com) via `render.yaml`.

**Passos:**

1. Crie uma conta no Render e conecte o repositório GitHub.
2. O `render.yaml` já define o serviço web e o banco de dados PostgreSQL.
3. Configure as variáveis de ambiente no painel do Render:
   - `JWT_SECRET` — gere com `openssl rand -base64 32`
   - `DB_PASSWORD` — senha forte gerada aleatoriamente
4. Para deploy manual: `git push origin main`

**Variáveis obrigatórias no Render:**

```
DATABASE_URL    → fornecida automaticamente pelo serviço de banco do Render
JWT_SECRET      → gerar com openssl (ver abaixo)
CORS_ORIGINS    → https://seu-frontend.onrender.com
JPA_HIBERNATE_DDL_AUTO → validate
```

---

## Gerar Secrets Seguros

```bash
# JWT Secret (≥ 32 caracteres, base64)
openssl rand -base64 32

# Senha do banco (hexadecimal)
openssl rand -hex 16
```

Ou execute o script incluído:
```bash
chmod +x GENERATE_SECRETS.sh
./GENERATE_SECRETS.sh
```

> **Nunca** commite `.env` com credenciais reais. Os arquivos `.env` já estão no `.gitignore`.
