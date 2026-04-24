# Escala Ministerial — Android

Aplicativo Android nativo (Kotlin + Jetpack Compose) para gerenciamento de escalas ministeriais da paróquia. Versão mobile do sistema web existente, consumindo a API Spring Boot.

## Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17+
- Backend Spring Boot rodando localmente na porta 8080

## Setup

### 1. Gerar o Gradle Wrapper

O arquivo `gradle-wrapper.jar` é binário e não está versionado. Gere-o com:

```bash
cd android/
gradle wrapper --gradle-version 8.7
```

Ou abra o projeto diretamente no Android Studio — ele baixa o wrapper automaticamente.

### 2. Configurar a URL da API

Por padrão, o app aponta para `http://10.0.2.2:8080/api/` (localhost do emulador Android).

Para mudar via `local.properties`:

```properties
# android/local.properties
BASE_URL=http://10.0.2.2:8080/api/
```

O `10.0.2.2` é o endereço que o emulador usa para acessar o `localhost` da máquina host.
Para dispositivo físico, use o IP da máquina na rede local (ex: `http://192.168.0.10:8080/api/`).

### 3. Iniciar o Backend

```bash
cd ../backend
./mvnw spring-boot:run
```

### 4. Rodar o App

**Via Android Studio:**
- Abra a pasta `android/` como projeto
- Selecione um emulador (API 26+) ou dispositivo físico
- Clique em Run ▶

**Via linha de comando:**

```bash
cd android/

# Listar emuladores disponíveis
$ANDROID_HOME/emulator/emulator -list-avds

# Iniciar emulador
$ANDROID_HOME/emulator/emulator -avd <NOME_DO_AVD> &

# Instalar e rodar no emulador conectado
./gradlew :app:installDebug && adb shell am start -n com.escala.ministerial.debug/.MainActivity
```

## Build

```bash
# Build de debug
./gradlew assembleDebug

# Build de release
./gradlew assembleRelease

# Lint
./gradlew lint

# Testes unitários
./gradlew test

# Testes instrumentados (emulador necessário)
./gradlew connectedAndroidTest

# Build completo + lint + testes
./gradlew build lint test
```

## Arquitetura

### Módulos Gradle

```
:app                    → Ponto de entrada, Activity, navegação, Hilt setup
:core:ui                → Tema Material 3, componentes compartilhados
:core:network           → Retrofit, OkHttp, ApiResult, NetworkModule
:core:data              → Room Database, DAOs, DataStore, DataModule
:feature:dashboard      → Tela de dashboard com estatísticas agregadas
:feature:ministros      → CRUD de ministros
:feature:eventos        → CRUD de eventos, cancelamento
:feature:escalas        → Listagem, aprovação, cancelamento e geração automática de escalas
:feature:feedback       → Listagem e resposta de feedbacks
:feature:auditoria      → Timeline de logs de auditoria (read-only)
```

### Clean Architecture (por feature)

```
feature/
├── data/
│   ├── dto/           → DTOs serializáveis para a API (kotlinx.serialization)
│   ├── datasource/    → Interface Retrofit (ApiService)
│   ├── repository/    → Implementação: network-first + cache Room
│   └── di/            → Hilt module (binds + provides)
├── domain/
│   ├── model/         → Modelos de domínio puros (Kotlin data classes)
│   ├── repository/    → Interface do repositório
│   └── usecase/       → Use cases (um por responsabilidade)
└── presentation/
    ├── *UiState.kt    → sealed interface com Loading/Success/Error
    ├── *ViewModel.kt  → @HiltViewModel, StateFlow, Channel para eventos
    └── *Screen.kt     → Composables (e dialogs de formulário)
```

### Stack Técnica

| Camada | Tecnologia |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose (type-safe routes) |
| DI | Hilt (Dagger) |
| Async | Kotlin Coroutines + Flow |
| Rede | Retrofit 2 + OkHttp + kotlinx.serialization |
| Cache local | Room Database |
| Preferências | DataStore Preferences |
| Imagens | Coil |
| Testes | JUnit 5 + MockK + Turbine |

### Estratégia Offline

O app usa **network-first com cache local**:
1. Ao abrir cada tela, o Flow do Room emite dados cached imediatamente
2. Uma chamada de `refresh()` atualiza do backend e grava no Room
3. Se a rede falha, os dados cached continuam disponíveis
4. Operações de escrita (create/update/delete) só funcionam com rede

## Decisões Técnicas

- **Kotlin DSL + Version Catalogs**: toda dependência centralizada em `gradle/libs.versions.toml`, sem duplicação
- **Convention plugins**: build logic reutilizável em `build-logic/`, evitando copiar configs entre módulos
- **`10.0.2.2` no emulador**: o emulador Android não reconhece `localhost` como a máquina host — `10.0.2.2` é o alias correto
- **Sealed `ApiResult`**: evita exceções não tratadas; o caller decide o que fazer com Error/Success
- **Channel para side effects**: eventos de UI (Snackbar, navigation) passam por `Channel`, não StateFlow, para evitar reexecução em recomposição
- **`fallbackToDestructiveMigration()`**: desenvolvimento inicial sem migrations formais; trocar por migrations explícitas em produção

## Pontos de Extensão

- **Autenticação**: adicionar interceptor de JWT no `NetworkModule` e tela de login
- **Push notifications**: Firebase Cloud Messaging para notificar ministros sobre escalas
- **Offline-write**: fila de operações pendentes com WorkManager para sync quando voltar a rede
- **Biometria**: proteger dados sensíveis com BiometricPrompt
- **Flavor de ambiente**: `debug` / `staging` / `release` com URLs distintas via `local.properties`
- **Baseline profiles**: adicionar para reduzir cold start em release
