# Escala Ministerial

Sistema de gestão de escalas de ministros da eucaristia — backend Spring Boot, frontend React e apps mobile Android/iOS.

---

## Estrutura do repositório

```
pj_bc/
├── backend/        # API REST — Spring Boot 3 + Java 17 + PostgreSQL
├── frontend/       # Web — React + Vite + TypeScript + Tailwind CSS
├── android/        # App Android — Kotlin + Jetpack Compose (multi-módulo)
├── ios/            # App iOS — Swift + SwiftUI
│
├── render.yaml     # Blueprint do Render para deploy do backend em 1 clique
├── Procfile        # Comando de start legado (Heroku-style)
├── RODAR_LOCAL.txt # Comandos prontos para rodar tudo localmente
│
├── .env            # Variáveis de ambiente locais e credenciais (gitignored)
├── .gitignore
```

---

## Plataformas

| Camada    | Stack principal                               | Deploy         |
|-----------|-----------------------------------------------|----------------|
| Backend   | Spring Boot · Maven · JPA · JWT               | Render (free)  |
| Frontend  | React 18 · Vite · Tailwind · Axios            | Vercel (free)  |
| Android   | Kotlin · Compose · Hilt · Retrofit            | —              |
| iOS       | Swift · SwiftUI · URLSession                  | —              |
| Banco     | PostgreSQL (Render managed)                   | Render (free)  |

---

## Rodando localmente

Veja `RODAR_LOCAL.txt` para os comandos exatos de cada plataforma.

**Resumo rápido:**

```bash
# Backend (http://localhost:8080)
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Frontend (http://localhost:5173)
cd frontend && npm install && npm run dev
```

Android e iOS: abrir no Android Studio / Xcode respectivamente.

---

## Variáveis de ambiente necessárias (backend)

| Variável               | Descrição                              |
|------------------------|----------------------------------------|
| `DATABASE_URL`         | JDBC URL do PostgreSQL                 |
| `DB_USERNAME`          | Usuário do banco                       |
| `DB_PASSWORD`          | Senha do banco                         |
| `JWT_SECRET`           | Chave secreta para assinar tokens JWT  |
| `CORS_ORIGINS`         | Origins permitidos pelo CORS           |
| `JPA_HIBERNATE_DDL_AUTO` | `update` em prod, `create` local     |

Copie `.env.example` para `.env` e preencha antes de subir.

---

## Backend — estrutura detalhada

```
backend/
├── Dockerfile                  # Build multi-stage (Maven → JRE Alpine) para deploy no Render
├── mvnw / mvnw.cmd             # Maven Wrapper — garante a versão certa do Maven sem instalar nada
├── pom.xml                     # Dependências e configuração do projeto Maven
│
└── src/
    ├── main/
    │   ├── java/com/exemplo/escala/
    │   │   │
    │   │   ├── EscalaMinisterialApplication.java   # Ponto de entrada — @SpringBootApplication
    │   │   │
    │   │   ├── config/
    │   │   │   ├── DatabaseUrlNormalizer.java       # Converte DATABASE_URL do Render para formato JDBC
    │   │   │   ├── GlobalExceptionHandler.java      # Trata exceções globais e retorna JSON padronizado
    │   │   │   ├── LocalDataSeeder.java             # Popula banco com dados de teste (@Profile("local") only)
    │   │   │   ├── SecurityConfig.java              # Configura Spring Security: rotas públicas, JWT filter
    │   │   │   └── WebConfig.java                  # Configura CORS (origens permitidas via env var)
    │   │   │
    │   │   ├── controller/                          # Recebem requisições HTTP e delegam para os services
    │   │   │   ├── MinistroController.java          # CRUD de ministros + ativar/desativar
    │   │   │   ├── EventoController.java            # CRUD de eventos + cancelar
    │   │   │   ├── EscalaController.java            # Gerar/aprovar/cancelar/deletar escalas
    │   │   │   ├── FeedbackController.java          # Listar feedbacks + responder
    │   │   │   ├── IndisponibilidadeController.java # Registrar/remover indisponibilidades de ministros
    │   │   │   ├── LogAuditoriaController.java      # Consultar histórico de ações (somente leitura)
    │   │   │   ├── HealthController.java            # GET /api/public/health — usado pelo Render p/ healthcheck
    │   │   │   └── LocalSeedController.java         # Endpoints de seed (@Profile("local") — não existe em prod)
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
