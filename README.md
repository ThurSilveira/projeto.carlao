# Escala Ministerial

Sistema de gestão de escalas de ministros da eucaristia — backend FastAPI (Python), frontend React e apps mobile Android/iOS.

**Status de produção:** ✅ Backend deployed em Render, mobile clients sincronizados com API REST.

---

## Estrutura do repositório

```
pj_bc/
├── backend/        # API REST — FastAPI 0.104+ · Python 3.12 · SQLAlchemy · PostgreSQL
├── frontend/       # Web — React 18 · Vite · TypeScript · Tailwind CSS
├── android/        # App Android — Kotlin · Compose · Hilt · Retrofit (multi-módulo)
├── ios/            # App iOS — Swift · SwiftUI · URLSession
│
├── Dockerfile      # Build multi-stage para deploy no Render
├── render.yaml     # Blueprint do Render para deploy automático com 1 clique
├── Procfile        # Comando de start legado (Heroku-style compatibility)
|
│
├── .env            # Variáveis de ambiente locais (gitignored)
├── .gitignore
```

---

## Plataformas

| Camada    | Stack principal                               | URL/Deploy                           |
|-----------|-----------------------------------------------|--------------------------------------|
| Backend   | FastAPI · Python 3.12 · SQLAlchemy · Pydantic | https://escala-ministerial-api.onrender.com/api |
| Frontend  | React 18 · Vite · Tailwind · Axios            | Vercel (free)                       |
| Android   | Kotlin · Compose · Hilt · Retrofit            | Alvo: backend Render                |
| iOS       | Swift · SwiftUI · URLSession · Codable        | Alvo: backend Render                |
| Banco     | PostgreSQL 15+                                | Render managed (free tier)          |


---

## Variáveis de ambiente necessárias (backend)

| Variável               | Descrição                                     | Padrão/Exemplo         |
|------------------------|-----------------------------------------------|------------------------|
| `DATABASE_URL`         | PostgreSQL URL (Render normalizará para SQLAlchemy) | `postgres://user:pass@host/db` |
| `CORS_ORIGINS`         | Origins permitidos (separados por vírgula)    | `*` (local), domínio (prod)        |
| `ENVIRONMENT`          | `development` ou `production`                 | `production`                       |
| `PORT`                 | Porta do servidor                             | `8000`                             |

Copie `.env.example` para `.env` e preencha localmente. Render injeta as variáveis automaticamente.

---

## Backend — Estrutura

```
backend/
├── Dockerfile                  # Build multi-stage (Python 3.12 slim → runtime)
├── requirements.txt            # Dependências Python (FastAPI, SQLAlchemy, psycopg2, etc.)
│
└── app/
    ├── main.py                 # Ponto de entrada FastAPI, rotas, CORS, lifespan
    ├── database.py             # Engine SQLAlchemy, normalização de DATABASE_URL do Render
    ├── models.py               # ORM models (Ministro, Evento, Escala, etc.)
    ├── schemas.py              # Pydantic schemas (request/response, validação, camelCase)
    │
    ├── routers/
    │   ├── health.py           # GET /api/public/health — healthcheck Render
    │   ├── ministros.py        # CRUD ministros + indisponibilidades
    │   ├── eventos.py          # CRUD eventos + cancelar
    │   ├── escalas.py          # Gerar/aprovar/cancelar escalas
    │   ├── feedbacks.py        # Listar/responder feedbacks
    │   ├── auditoria.py        # Histórico de ações (audit log)
    │   ├── indisponibilidades.py # Gerenciar indisponibilidades de ministros
    │   └── seed.py             # Endpoints de seed (@ENVIRONMENT=development only)
    │
    ├── services/
    │   ├── ministro_service.py      # Lógica de negócio — ministros
    │   ├── evento_service.py        # Lógica de negócio — eventos (com audit)
    │   ├── escala_service.py        # Lógica de negócio — escalas (com audit)
    │   ├── auditoria_service.py     # Listar/criar audit logs
    │   └── indisponibilidade_service.py # Lógica de indisponibilidades
    │
    └── seed.py                 # Dados de teste para populate inicial
    │   │   │
    │   │   ├── service/                             # Regras de negócio
    │   │   │   ├── MinistroService.java             # Valida duplicatas, ativa/desativa ministros
    │   │   │   ├── EventoService.java               # Valida datas, cancela eventos com cascata
    │   │   │   ├── EscalaService.java               # Algoritmo de geração automática (ordena por carga mensal, sorteia disponíveis)
    │   │   │   ├── FeedbackService.java             # Salva resposta e muda status para RESPONDIDO
    │   │   │   ├── IndisponibilidadeService.java    # Controla períodos de indisponibilidade por ministro
    │   │   │   └── LogAuditoriaService.java         # Grava log a cada ação relevante no sistema
    │   │   │
    │   │   ├── repository/                          # Interfaces JPA — queries ao banco
    │   │   │   ├── MinistroRepository.java
    │   │   │   ├── EventoRepository.java
    │   │   │   ├── EscalaRepository.java
    │   │   │   ├── EscalaMinistroRepository.java    # Tabela de junção Escala ↔ Ministro
    │   │   │   ├── FeedbackRepository.java
    │   │   │   ├── IndisponibilidadeRepository.java
    │   │   │   └── LogAuditoriaRepository.java
    │   │   │
    │   │   ├── model/                               # Entidades JPA (mapeadas para tabelas do banco)
    │   │   │   ├── Ministro.java
    │   │   │   ├── Evento.java
    │   │   │   ├── Escala.java
    │   │   │   ├── EscalaMinistro.java              # Linha da tabela de junção (inclui função e confirmação)
    │   │   │   ├── Feedback.java
    │   │   │   ├── Indisponibilidade.java
    │   │   │   ├── LogAuditoria.java
    │   │   │   └── enums/
    │   │   │       ├── FuncaoMinistro.java          # MINISTRO_ORDINARIO, ACÓLITO, …
    │   │   │       ├── StatusEscala.java            # PROPOSTA, APROVADA, CONFIRMADA, CANCELADA
    │   │   │       ├── StatusFeedback.java          # PENDENTE, RESPONDIDO, ARQUIVADO
    │   │   │       ├── TipoAcao.java                # CRIADO, ATUALIZADO, APROVADO, CANCELADO, …
    │   │   │       └── TipoEvento.java              # MISSA_PAROQUIAL, CASAMENTO, BATISMO, OUTRO, …
    │   │   │
    │   │   └── dto/                                 # Objetos de transferência (o que a API recebe/retorna)
    │   │       ├── MinistroDTO.java
    │   │       ├── EventoDTO.java
    │   │       ├── EscalaDTO.java
    │   │       ├── EscalaMinistroDTO.java
    │   │       ├── FeedbackDTO.java
    │   │       ├── IndisponibilidadeDTO.java
    │   │       └── LogAuditoriaDTO.java
    │   │
    │   └── resources/
    │       ├── application.properties               # Config base: porta, JPA DDL, logging
    │       ├── application-local.properties         # Sobrescreve para dev local: banco H2 ou Postgres local
    │       ├── application-h2.properties            # Config do banco H2 em memória (testes rápidos)
    │       └── META-INF/spring.factories            # Registro de auto-configurações customizadas
    │
    └── test/
        └── java/com/exemplo/escala/service/
            ├── EscalaServiceTest.java               # Testes unitários do algoritmo de geração de escalas
            └── SimulacaoEscalasMain.java            # Script standalone para simular a geração manualmente
```

---

## Frontend — estrutura detalhada

```
frontend/
├── index.html              # HTML raiz — ponto de entrada do Vite
├── vite.config.ts          # Config do bundler: alias @/ → src/, proxy de dev
├── tsconfig.json           # Config TypeScript do código da aplicação
├── tsconfig.node.json      # Config TypeScript para os arquivos de config do Vite
├── tailwind.config.js      # Tema personalizado (cores primary, dark mode class-based)
├── postcss.config.js       # Habilita Tailwind e Autoprefixer no pipeline CSS
├── vercel.json             # Rewrite SPA: toda rota → index.html (evita 404 no refresh)
├── package.json            # Dependências e scripts npm
├── package-lock.json       # Lock de versões exatas das dependências
│
└── src/
    ├── main.tsx            # Monta o React, define as rotas (React Router) e envolve no ThemeProvider
    ├── index.css           # Estilos globais e diretivas @tailwind
    ├── vite-env.d.ts       # Tipos do import.meta.env gerados pelo Vite
    ├── types.ts            # Todas as interfaces TypeScript e enums (Ministro, Evento, Escala, …)
    │
    ├── services/
    │   └── api.ts          # Cliente Axios + todos os serviços REST (MinistroService, EventoService, …)
    │                       # Timeout de 60s para aguentar cold-start do Render
    │
    ├── utils/
    │   └── date.ts         # parseLocalDate / formatDate — corrige bug de fuso horário em strings de data
    │
    ├── hooks/
    │   └── useTheme.ts     # Hook que lê/escreve o tema (light/dark) no localStorage
    │
    ├── components/
    │   ├── Layout.tsx      # Shell da aplicação: sidebar, topbar, slot de conteúdo, toggle de tema
    │   ├── ui.tsx          # Biblioteca interna de componentes: Button, Card, Badge, Modal,
    │   │                   # Input, Select, Alert, Spinner — todos com suporte a dark mode
    │   └── ErrorBoundary.tsx # Captura erros de renderização e exibe tela de fallback
    │
    └── pages/              # Uma página por rota
        ├── Dashboard.tsx   # Visão geral: contadores, próximos eventos, feedbacks recentes
        ├── Ministros.tsx   # CRUD de ministros + ativar/desativar + indisponibilidades
        ├── Eventos.tsx     # CRUD de eventos + cancelar
        ├── Escalas.tsx     # Listar escalas, gerar automaticamente, aprovar/cancelar/deletar
        ├── Feedback.tsx    # Listar feedbacks, filtrar por status, responder
        └── Auditoria.tsx   # Tabela de logs de auditoria com filtro por entidade e ação
```
