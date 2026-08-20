# Escala Ministerial

[![CI/CD](https://github.com/ThurSilveira/projeto.carlao/actions/workflows/ci-cd.yml/badge.svg?branch=main)](https://github.com/ThurSilveira/projeto.carlao/actions/workflows/ci-cd.yml)

Sistema web para administrar ministros, eventos, escalas, indisponibilidades,
substituições, feedbacks e registros de auditoria.

## Produção

| Componente | Plataforma | Endereço |
|---|---|---|
| Frontend | Vercel | [escala-ministerial.vercel.app](https://escala-ministerial.vercel.app) |
| Backend | Render | [escala-ministerial-api.onrender.com](https://escala-ministerial-api.onrender.com/api/public/health) |
| API Docs | Render | [OpenAPI/Swagger](https://escala-ministerial-api.onrender.com/api/docs) |
| Banco | Render PostgreSQL | Acesso privado pelo backend |

O PostgreSQL atual usa o plano gratuito do Render e tinha expiração informada
para 19/09/2026. Confirme o estado do recurso antes de depender de dados de
produção.

## Arquitetura

```text
Navegador
   │
   ▼
React 19 + TypeScript + Vite 8 (Vercel)
   │ HTTPS / JSON camelCase
   ▼
FastAPI + Pydantic + SQLAlchemy (Render/Docker)
   │ SSL
   ▼
PostgreSQL gerenciado (Render)
```

O frontend concentra as chamadas HTTP em `frontend/src/services/api.ts`. No
backend, routers tratam HTTP, schemas validam contratos, services concentram as
regras de negócio e models representam a persistência.

## Estrutura da `main`

```text
.
├── .github/
│   ├── workflows/ci-cd.yml    # Qualidade, Docker e deploy de produção
│   └── dependabot.yml         # Atualizações semanais de dependências
├── backend/
│   ├── app/
│   │   ├── routers/           # Endpoints FastAPI
│   │   ├── services/          # Regras de negócio e auditoria
│   │   ├── database.py        # Engine e sessões SQLAlchemy
│   │   ├── models.py          # Modelos ORM
│   │   ├── schemas.py         # Contratos Pydantic/camelCase
│   │   └── main.py            # Aplicação e middleware
│   ├── Dockerfile
│   ├── .dockerignore
│   └── requirements.txt
├── frontend/
│   ├── src/
│   │   ├── components/        # Componentes reutilizáveis
│   │   ├── hooks/             # Hooks React
│   │   ├── pages/             # Telas da aplicação
│   │   ├── services/          # Cliente Axios
│   │   ├── utils/             # Datas e tratamento de erros
│   │   ├── App.tsx            # Rotas da SPA
│   │   └── main.tsx           # Entrada React
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── eslint.config.js
│   ├── vercel.json
│   └── package.json
├── render.yaml                # Infraestrutura do backend no Render
└── Procfile                   # Compatibilidade de execução
```

Android, iOS e backend Java não fazem parte da `main`. Essas versões estão
preservadas e congeladas na branch `versoes-futuras-congeladas`.

## Tecnologias

### Frontend

- React 19.2;
- React Router 7;
- TypeScript 6;
- Vite 8;
- Tailwind CSS 4 com plugin oficial do Vite;
- Axios 1.19;
- ESLint 10 com regras para TypeScript, Hooks e Fast Refresh.

### Backend

- Python 3.12;
- FastAPI;
- Pydantic 2;
- SQLAlchemy 2;
- PostgreSQL e `psycopg2`;
- Uvicorn.

### Infraestrutura

- GitHub Actions para CI/CD;
- Docker para validar os dois componentes;
- Vercel para a SPA;
- Render para API e PostgreSQL;
- Dependabot para npm, pip, Docker e GitHub Actions.

## Executar localmente

### Pré-requisitos

- Node.js 24 recomendado;
- npm 11 ou compatível com o lockfile v3;
- Python 3.12;
- PostgreSQL;
- Docker opcional.

### Backend

```bash
cd backend
python3.12 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
cp .env.example .env
python -m uvicorn app.main:app --host 127.0.0.1 --port 8080 --env-file .env
```

Health check:

```bash
curl --fail http://127.0.0.1:8080/api/public/health
```

### Frontend

Em outro terminal:

```bash
cd frontend
npm ci
cp .env.example .env
npm run dev -- --host 127.0.0.1 --open=false
```

A aplicação fica em `http://127.0.0.1:3000` e usa, por padrão,
`http://localhost:8080/api`.

### Checks locais

```bash
cd frontend
npm run check
npm audit --audit-level=moderate
```

No backend:

```bash
cd backend
python -m pip check
python -m compileall -q app
```

## Docker

Construir as duas imagens a partir da raiz:

```bash
docker build -t escala-ministerial-api ./backend
docker build \
  --build-arg VITE_API_URL=https://escala-ministerial-api.onrender.com/api \
  -t escala-ministerial-web ./frontend
```

Executar o frontend estático:

```bash
docker run --rm -p 8081:8080 escala-ministerial-web
```

O contêiner do backend requer `DATABASE_URL` e as demais variáveis do ambiente.
Não passe credenciais diretamente em comandos que possam ficar no histórico do
shell; prefira um arquivo local ignorado pelo Git ou um gerenciador de secrets.

## Variáveis de ambiente

### Backend

| Variável | Obrigatória | Descrição |
|---|---:|---|
| `DATABASE_URL` | Sim | Conexão PostgreSQL |
| `DB_SSL` | Produção | Ativa SSL quando `true` |
| `DB_SSLMODE` | Não | Sobrescreve o modo SSL |
| `CORS_ORIGINS` | Sim | Origens web permitidas, separadas por vírgula |
| `ENVIRONMENT` | Sim | `development` ou `production` |
| `PORT` | Plataforma | Porta do servidor, padrão `8080` |

### Frontend

| Variável | Obrigatória | Descrição |
|---|---:|---|
| `VITE_API_URL` | Produção | URL base da API, incluindo `/api` |
| `VITE_ENV` | Não | Identificação do ambiente |

Variáveis `VITE_*` são incorporadas ao bundle e, portanto, não podem conter
segredos.

## API

Todos os recursos usam o prefixo `/api`:

- `/ministros`: CRUD de ministros;
- `/eventos`: CRUD e cancelamento de eventos;
- `/escalas`: geração, preview, aprovação, substituição e cancelamento;
- `/ministros/{id}/indisponibilidades`: gestão de indisponibilidades;
- `/feedbacks`: criação, listagem e resposta;
- `/auditoria`: consulta de logs;
- `/public/health`: health check;
- `/seed`: disponível somente com `ENVIRONMENT=development`.

Os schemas usam nomes Python em `snake_case` internamente e JSON em `camelCase`
para o frontend.

## CI/CD

O workflow `.github/workflows/ci-cd.yml` roda em pull requests e pushes para
`main`.

```text
Push/PR
  ├─ Frontend: npm ci -> lint -> typecheck -> build -> audit
  ├─ Backend: pip check -> compileall -> PostgreSQL -> API smoke test
  └─ Docker: build frontend + build backend
                  │
                  └─ push na main e todos os checks verdes
                       ├─ Vercel CLI: pull -> build -> deploy --prebuilt
                       └─ Render API: deploy do commit exato -> aguarda live -> health
```

Deploys automáticos diretos por Git estão desativados nas duas plataformas. A
Vercel usa `git.deploymentEnabled: false`; o Render usa
`autoDeployTrigger: off`. Isso impede publicação antes do CI.

### GitHub Secrets necessários

Configure em `Settings > Secrets and variables > Actions`:

| Secret | Uso |
|---|---|
| `VERCEL_TOKEN` | Autentica o Vercel CLI |
| `VERCEL_ORG_ID` | Identifica o usuário/time Vercel |
| `VERCEL_PROJECT_ID` | Identifica o projeto Vercel |
| `RENDER_API_KEY` | Autentica a API do Render |
| `RENDER_SERVICE_ID` | Identifica o serviço do backend |

Nunca salve os valores no repositório. O pipeline verifica apenas se eles
existem e o GitHub mascara seu uso nos logs.

## Segurança e limitações atuais

- o backend ainda não implementa autenticação e autorização;
- os dados incluem informações pessoais e devem ser tratados conforme a LGPD;
- produção restringe CORS ao domínio oficial da Vercel;
- `/api/seed` não é montado em produção;
- tabelas ainda são criadas por `Base.metadata.create_all`; migrations
  versionadas continuam sendo uma dívida técnica;
- o banco gratuito requer acompanhamento de expiração, backup e migração.

Não use a API para dados sensíveis ou múltiplos administradores antes de
implementar autenticação, autorização por papéis e uma política de retenção.

## Branches

- `main`: aplicação web ativa e infraestrutura;
- `versoes-futuras-congeladas`: Android, iOS e backend Java preservados;
- branches de feature/fix: devem abrir PR para `main` e passar pelo CI.

Commits, pushes e deploys devem conter somente alterações revisadas. Confira
`git status`, `git diff`, lint, build, Docker e auditoria antes de publicar.
