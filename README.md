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

Os registros de demonstração em produção são sintéticos e servem somente para
testes pessoais. Métricas e listagens do frontend são calculadas integralmente a
partir da API; não há totais ou percentuais demonstrativos fixos na interface.
Aprovar uma escala altera somente o status da escala; o evento associado
permanece ativo e só é cancelado pela operação explícita de cancelamento.

## Arquitetura

```text
Navegador
   │
   ▼
React 19 + TypeScript + Vite 8 (Vercel)
   │ HTTPS / JSON camelCase
   │ cookie de sessão HttpOnly + proteção CSRF
   ▼
FastAPI + Pydantic + SQLAlchemy (Render/Docker)
   │ SSL
   ▼
PostgreSQL gerenciado (Render)
```

O frontend concentra as chamadas HTTP em `frontend/src/services/api.ts`. No
backend, routers tratam HTTP, schemas validam contratos, services concentram as
regras de negócio e models representam a persistência.

O acesso administrativo exige autenticação. As senhas usam Argon2id e as
sessões são identificadas por tokens aleatórios; somente o hash do token é
persistido. O navegador recebe o identificador em cookie `HttpOnly`, `Secure`
em produção, e operações autenticadas também exigem um token CSRF. Tentativas
de login repetidas são limitadas.

### Perfis de acesso

| Perfil | Consulta operacional | Alterações operacionais | Auditoria | Usuários e perfis |
|---|---:|---:|---:|---:|
| `ADMINISTRADOR` | Sim | Sim | Sim | Sim |
| `COORDENADOR` | Sim | Sim | Sim | Não |
| `CONSULTA` | Sim | Não | Não | Não |
| `MINISTRO` | Somente calendário pessoal | Somente indisponibilidades e feedback próprios | Não | Não |

O perfil `MINISTRO` não acessa dashboard, cadastros administrativos nem as
rotas operacionais gerais. Ele visualiza os eventos da igreja e suas escalas em
modo somente leitura, gerencia apenas as próprias indisponibilidades e envia um
único feedback depois de cada evento em que foi escalado.

A primeira conta do banco é promovida automaticamente a `ADMINISTRADOR` e
marcada como protegida. Ela não pode ser excluída, desativada, rebaixada ou ter
a senha redefinida pela administração de usuários. Sua senha só pode ser
alterada pela própria conta em `/api/auth/password`.

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
- Google Calendar API com autenticação OAuth 2.0;
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
# Edite AUTH_ADMIN_EMAIL e AUTH_ADMIN_PASSWORD antes da primeira execução.
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

A aplicação fica em `http://127.0.0.1:3000` e usa `/api`, encaminhado pelo Vite
ao backend local em `http://127.0.0.1:8080`.

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
python -m unittest discover -s tests -v
```

## Docker

Construir as duas imagens a partir da raiz:

```bash
docker build -t escala-ministerial-api ./backend
docker build -t escala-ministerial-web ./frontend
```

Executar o frontend estático:

```bash
docker run --rm \
  -e BACKEND_URL=https://escala-ministerial-api.onrender.com \
  -p 8081:8080 \
  escala-ministerial-web
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
| `AUTH_ADMIN_NAME` | Primeira execução | Nome do administrador inicial |
| `AUTH_ADMIN_EMAIL` | Primeira execução | E-mail do administrador inicial |
| `AUTH_ADMIN_PASSWORD` | Primeira execução | Frase-senha inicial, com no mínimo 12 caracteres |
| `AUTH_SESSION_HOURS` | Não | Duração da sessão, de 1 a 168 horas; padrão `8` |
| `GOOGLE_CALENDAR_ENABLED` | Não | Ativa a sincronização somente quando `true`; padrão `false` |
| `GOOGLE_CALENDAR_CLIENT_ID` | Google Calendar | Client ID OAuth da conta organizadora |
| `GOOGLE_CALENDAR_CLIENT_SECRET` | Google Calendar | Client secret OAuth, armazenado como secret |
| `GOOGLE_CALENDAR_REFRESH_TOKEN` | Google Calendar | Refresh token OAuth da conta organizadora |
| `GOOGLE_CALENDAR_ID` | Google Calendar | ID do calendário organizador; normalmente `primary` |
| `GOOGLE_CALENDAR_TIMEZONE` | Não | Fuso IANA dos eventos; padrão `America/Sao_Paulo` |
| `GOOGLE_CALENDAR_EVENT_DURATION_MINUTES` | Não | Duração usada no convite; padrão `120` |

### Frontend

| Variável | Obrigatória | Descrição |
|---|---:|---|
| `VITE_API_URL` | Não | URL base da API; use `/api` para manter a sessão na mesma origem |
| `VITE_ENV` | Não | Identificação do ambiente |
| `BACKEND_URL` | Docker | Destino do proxy Nginx em runtime, sem o sufixo `/api` |

Variáveis `VITE_*` são incorporadas ao bundle e, portanto, não podem conter
segredos.

Na Vercel, `/api` é encaminhado ao backend pelo `vercel.json`. Isso mantém o
cookie como primeira parte. Se já existir `VITE_API_URL` configurada com a URL
absoluta do Render, altere-a para `/api` ou remova-a.

Na imagem Docker do frontend, o mesmo caminho `/api` é encaminhado pelo Nginx.
Configure `BACKEND_URL` em runtime; o valor padrão aponta para o backend oficial
no Render e nenhuma credencial é incorporada ao bundle.

## API

Todos os recursos usam o prefixo `/api`:

- `/auth/login`, `/auth/me`, `/auth/logout` e `/auth/password`: sessão administrativa;
- `/usuarios` e `/usuarios/perfis`: administração RBAC, exclusiva para administradores;
- `/ministros`: CRUD de ministros;
- `/eventos`: CRUD e cancelamento de eventos;
- `/escalas`: geração, preview, aprovação, substituição e cancelamento;
- `/escalas/{id}/sincronizar-calendario`: reprocessamento manual dos convites;
- `/ministros/{id}/indisponibilidades`: gestão de indisponibilidades;
- `/feedbacks`: criação, listagem e resposta;
- `/portal/ministro/me`: cadastro pessoal vinculado à sessão;
- `/portal/ministro/calendario`: eventos da igreja e escalas pessoais, somente leitura;
- `/portal/ministro/indisponibilidades`: CRUD restrito ao próprio ministro;
- `/portal/ministro/feedbacks`: envio e histórico restritos ao próprio ministro;
- `/auditoria`: consulta de logs;
- `/public/health`: health check;
- `/seed`: disponível somente com `ENVIRONMENT=development`.

Os schemas usam nomes Python em `snake_case` internamente e JSON em `camelCase`
para o frontend.

Somente `/public/health`, a documentação OpenAPI e `/auth/login` são acessíveis
sem sessão. A senha definida por variável de ambiente é usada exclusivamente
para criar a primeira conta: reiniciar o serviço não redefine uma senha já
alterada no sistema.

### Administração pelo Postman

Após `POST /api/auth/login`, mantenha o cookie recebido e envie o campo
`csrfToken` da resposta no header `X-CSRF-Token` das operações de escrita.

Criar usuário:

```http
POST /api/usuarios
Content-Type: application/json
X-CSRF-Token: <token-do-login>
```

```json
{
  "nome": "Nome do usuário",
  "email": "usuario@paroquia.org.br",
  "senha": "frase-senha-com-12-ou-mais",
  "perfil": "COORDENADOR",
  "ativo": true
}
```

Também estão disponíveis `GET /api/usuarios`, `GET /api/usuarios/perfis`,
`PUT /api/usuarios/{id}`, `PUT /api/usuarios/{id}/senha` e
`DELETE /api/usuarios/{id}`.

#### Criar o acesso de um ministro

Primeiro crie ou consulte o cadastro operacional do ministro. O e-mail precisa
ser real, exclusivo e será usado tanto no login quanto no convite do Google
Calendar:

```http
POST /api/ministros
Content-Type: application/json
X-CSRF-Token: <token-do-login>
```

```json
{
  "nome": "Maria da Silva",
  "email": "maria@paroquia.org.br",
  "telefone": "",
  "ativo": true,
  "funcao": "LEITURA"
}
```

Copie o `id` retornado e crie o usuário com o mesmo e-mail:

```http
POST /api/usuarios
Content-Type: application/json
X-CSRF-Token: <token-do-login>
```

```json
{
  "nome": "Maria da Silva",
  "email": "maria@paroquia.org.br",
  "senha": "frase-senha-inicial-segura",
  "perfil": "MINISTRO",
  "ativo": true,
  "ministroId": 15
}
```

O backend recusa e-mail divergente, ministro inativo ou um segundo usuário para
o mesmo ministro. No Postman, mantenha o cookie da sessão habilitado. Para
testar o portal, faça novo login com o usuário criado e use o novo `csrfToken`.

### Google Calendar e notificações

Quando uma escala futura é aprovada, cada ministro ativo na escala recebe um
convite no e-mail cadastrado. Atualizações, substituições e cancelamentos também
são refletidos no evento do Google. A integração grava o ID remoto e o estado
da sincronização para evitar convites duplicados e permitir reprocessamento.

Para ativar:

1. crie um projeto no Google Cloud e habilite a **Google Calendar API**;
2. configure a tela de consentimento OAuth e um cliente OAuth para uma conta
   organizadora dedicada da paróquia;
3. autorize o escopo `https://www.googleapis.com/auth/calendar.events` e gere
   um refresh token dessa conta;
4. salve Client ID, Client Secret, Refresh Token e Calendar ID nos secrets do
   Render;
5. altere `GOOGLE_CALENDAR_ENABLED` para `true` e reimplante o backend;
6. reprocese escalas já aprovadas com
   `POST /api/escalas/{id}/sincronizar-calendario`.

Consulte a documentação oficial sobre
[autorização](https://developers.google.com/workspace/calendar/api/auth),
[convites](https://developers.google.com/workspace/calendar/api/concepts/inviting-attendees-to-events)
e [lembretes](https://developers.google.com/workspace/calendar/api/concepts/reminders).

O terceiro lembrete é programado para uma hora antes do início da escala.
Importante: os lembretes configurados no evento são privados da conta que os
define; o organizador não controla de forma garantida os lembretes particulares
do convidado. O convite contém as marcações de 7 dias, 3 dias e uma hora antes
da escala no calendário organizador, mas a entrega exata ao ministro depende das
preferências da conta Google dele. Garantia centralizada exige delegação para os
calendários dos ministros em um domínio Google Workspace ou um serviço de e-mail
agendado complementar. Contas de serviço novas também exigem delegação em nível
de domínio para convidar participantes; não configure uma chave de conta de
serviço comum como substituta do OAuth do organizador.

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
                       ├─ Vercel Deploy Hook: build da main -> confirma SHA publicado
                       └─ Render API: deploy do commit exato -> aguarda live -> health
```

Deploys automáticos diretos por Git estão desativados nas duas plataformas. A
Vercel usa `git.deploymentEnabled: false`; o Render usa
`autoDeployTrigger: off`. Isso impede publicação antes do CI.

### GitHub Secrets necessários

Configure em `Settings > Secrets and variables > Actions`:

| Secret | Uso |
|---|---|
| `VERCEL_DEPLOY_HOOK_URL` | Aciona o deploy da `main` após os gates e deve ser tratado como credencial |
| `RENDER_API_KEY` | Autentica a API do Render |
| `RENDER_SERVICE_ID` | Identifica o serviço do backend |

Nunca salve os valores no repositório. O pipeline verifica apenas se eles
existem e o GitHub mascara seu uso nos logs.

## Segurança e limitações atuais

- autenticação, sessões revogáveis, CSRF e autorização por perfis estão ativas;
- o administrador principal é protegido e todas as ações de usuários são auditadas;
- o portal do ministro valida o vínculo no backend e nunca aceita um `ministroId` fornecido pelo navegador;
- falhas de sincronização do Google Calendar ficam registradas e podem ser reprocessadas;
- os dados incluem informações pessoais e devem ser tratados conforme a LGPD;
- produção restringe CORS ao domínio oficial da Vercel;
- `/api/seed` não é montado em produção;
- tabelas ainda são criadas por `Base.metadata.create_all`; migrations
  versionadas continuam sendo uma dívida técnica;
- o banco gratuito requer acompanhamento de expiração, backup e migração.

As credenciais iniciais devem permanecer somente no gerenciador de secrets da
plataforma ou em `.env` local ignorado pelo Git. Recomenda-se definir também uma
política operacional de retenção, backup e revisão periódica dos acessos.

## Branches

- `main`: aplicação web ativa e infraestrutura;
- `versoes-futuras-congeladas`: Android, iOS e backend Java preservados;
- branches de feature/fix: devem abrir PR para `main` e passar pelo CI.

Commits, pushes e deploys devem conter somente alterações revisadas. Confira
`git status`, `git diff`, lint, build, Docker e auditoria antes de publicar.
