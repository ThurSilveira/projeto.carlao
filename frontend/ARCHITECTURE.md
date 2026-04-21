# 🏗️ Arquitetura do Frontend

## Visão Geral

O frontend segue uma arquitetura modular e escalável baseada em React, com separação clara de responsabilidades:

```
Frontend (React + TypeScript)
├── Componentes Reutilizáveis (UI)
├── Páginas (Features)
├── Serviços (API Layer)
└── Tipos (Type Safety)
```

---

## Estrutura de Diretórios

```
frontend/
│
├── src/
│   │
│   ├── components/              # Componentes reutilizáveis
│   │   ├── ui.tsx              # Componentes de base (Button, Card, etc)
│   │   └── Layout.tsx          # Layout principal da app
│   │
│   ├── pages/                  # Páginas/Features principais
│   │   ├── Dashboard.tsx       # Página inicial /
│   │   ├── Ministros.tsx       # CRUD Ministros /ministros
│   │   ├── Eventos.tsx         # CRUD Eventos /eventos
│   │   ├── Escalas.tsx         # Gestão Escalas /escalas
│   │   ├── Feedback.tsx        # Análise Feedback /feedback
│   │   └── Auditoria.tsx       # Log Auditoria /auditoria
│   │
│   ├── services/               # Serviços/API layer
│   │   └── api.ts              # Chamadas à API + Mock data
│   │
│   ├── types.ts                # TypeScript interfaces globais
│   ├── index.css               # Estilos globais
│   └── main.tsx                # Entry point da aplicação
│
├── index.html                  # HTML principal
├── vite.config.ts              # Config Vite
├── tsconfig.json               # Config TypeScript
├── tailwind.config.js          # Config Tailwind CSS
├── postcss.config.js           # Config PostCSS
└── package.json                # Dependências
```

---

## Fluxo de Data

```
┌─────────────────────────────────────────────────────┐
│         User Interaction (Componente)               │
└──────────────┬──────────────────────────────────────┘
               │ onClick, onChange, etc
               ▼
┌─────────────────────────────────────────────────────┐
│         State Management (useState)                  │
│  - Local state in components                        │
│  - Ready for Context/Redux if needed               │
└──────────────┬──────────────────────────────────────┘
               │ Chamadas de funções
               ▼
┌─────────────────────────────────────────────────────┐
│    API Service Layer (src/services/api.ts)          │
│  - Abstração de chamadas HTTP                      │
│  - Mock data para desenvolvimento                  │
└──────────────┬──────────────────────────────────────┘
               │ fetch/axios request
               ▼
┌─────────────────────────────────────────────────────┐
│         Backend API (Java)                          │
│  - REST endpoints (port 8080)                      │
│  - Banco de dados                                  │
└──────────────┬──────────────────────────────────────┘
               │ JSON response
               ▼
┌─────────────────────────────────────────────────────┐
│    API Service Response Handler                     │
│  - Parse JSON                                      │
│  - Error handling                                  │
└──────────────┬──────────────────────────────────────┘
               │ Return data
               ▼
┌─────────────────────────────────────────────────────┐
│         Update State (useState)                     │
│  - setData(responseData)                           │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│    Component Re-render                             │
│  - React atualiza DOM                             │
└─────────────────────────────────────────────────────┘
```

---

## Padrões e Princípios

### 1. **Component-Based Architecture**
```typescript
// Componente reutilizável com props clara
interface ButtonProps extends React.ButtonHTMLAttributes {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant = 'primary', size = 'md', isLoading, ...props }, ref) => {
    // Implementação
  }
);
```

### 2. **Service Layer Pattern**
```typescript
// Abstração de API calls
export const MinistroService = {
  getAllMinistros: async (): Promise<Ministro[]> => {
    // Chamada HTTP ou mock
  },
  createMinistro: async (ministro: Ministro): Promise<Ministro> => {
    // POST
  }
};

// Uso em componentes
const [ministros, setMinistros] = useState<Ministro[]>([]);
const loadMinistros = async () => {
  const data = await MinistroService.getAllMinistros();
  setMinistros(data);
};
```

### 3. **Type Safety com TypeScript**
```typescript
// Interfaces claras e reutilizáveis
export interface Ministro extends Pessoa {
  funcao: FuncaoMinistro;
  aptidoes: TipoEvento[];
  indisponibilidades: Indisponibilidade[];
}

// Enum para valores fixos
export enum FuncaoMinistro {
  EUCARISTIA = 'EUCARISTIA',
  ACOLITO = 'ACOLITO',
}
```

### 4. **State Management Local**
```typescript
// useState para state local
const [ministros, setMinistros] = useState<Ministro[]>([]);
const [searchTerm, setSearchTerm] = useState('');
const [isLoading, setIsLoading] = useState(false);

// Pronto para Context API ou Redux se escalar
```

### 5. **Accessibility First**
```typescript
// ARIA labels em componentes
<input
  aria-invalid={!!error}
  aria-describedby={error ? `${id}-error` : undefined}
/>

// Semantic HTML
<nav role="navigation">
  <main id="skip-to-main">
    <section>...</section>
  </main>
</nav>
```

### 6. **Responsive Design com Tailwind**
```typescript
// Mobile-first approach
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
  {/* 1 coluna mobile, 2 tablet, 3 desktop */}
</div>
```

---

## Fluxo de Página (Exemplo: Ministros)

```
┌──────────────────────────────────────────────────────┐
│ /ministros → MinistrosPage Component                │
└──────────────────┬───────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
  useEffect             State Management
  loadMinistros()       - ministros[]
                        - searchTerm
                        - filterAtivo
                        - isModalOpen
                        - editingMinistro
        │                     │
        └──────────┬──────────┘
                   │
      ┌────────────┴────────────┐
      │                         │
      ▼                         ▼
  MinistroService         JSX Rendering
  getAllMinistros()       - Header
                          - Filters (Input, Select)
      │                   - Grid Cards
      │                   - Edit/Delete Buttons
      ▼                   - Modal Form
  API Response
  setMinistros(data)
```

---

## State Management Strategy

### Atual: Local State (useState)
```typescript
const MinistrosPage = () => {
  const [ministros, setMinistros] = useState<Ministro[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Funciona bem para componentes individuais
};
```

### Escalação: Context API
```typescript
// Quando múltiplas páginas precisam dos mesmos dados
const MinistrosContext = createContext<Ministro[]>([]);

const MinistrosProvider = ({ children }) => {
  const [ministros] = useState<Ministro[]>([]);
  return (
    <MinistrosContext.Provider value={ministros}>
      {children}
    </MinistrosContext.Provider>
  );
};
```

### Escalação: Redux (se necessário)
```typescript
// Para state complexo global ou time grande
import { configureStore, createSlice } from '@reduxjs/toolkit';

const ministrosSlice = createSlice({
  name: 'ministros',
  initialState: [],
  reducers: { /* ... */ }
});
```

---

## API Integration

### Antes de Conectar (Mock)
```typescript
export const MinistroService = {
  getAllMinistros: async (): Promise<Ministro[]> => {
    return new Promise((resolve) => {
      setTimeout(() => resolve(mockData), 300); // Mock delay
    });
  }
};
```

### Depois de Conectar (Real)
```typescript
export const MinistroService = {
  getAllMinistros: async (): Promise<Ministro[]> => {
    const response = await fetch(`${API_BASE_URL}/ministros`);
    if (!response.ok) throw new Error('Failed to fetch');
    return response.json();
  }
};
```

### Configuração de CORS (Backend)
```java
// No backend Java, configure CORS
@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:3000")
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .credentials(true);
  }
}
```

---

## Error Handling

### Pattern por Toda a App
```typescript
try {
  const data = await MinistroService.getAllMinistros();
  setMinistros(data);
} catch (error) {
  setAlertMessage('Erro ao carregar ministros');
  console.error(error);
} finally {
  setLoading(false);
}
```

### Alert Component
```typescript
{alertMessage && (
  <Alert 
    variant={alertMessage.includes('Erro') ? 'error' : 'success'}
    onClose={() => setAlertMessage('')}
  >
    {alertMessage}
  </Alert>
)}
```

---

## Performance Optimizations

### 1. **Code Splitting**
React Router automaticamente faz code splitting por página.

### 2. **Memoization** (pronto para adicionar)
```typescript
const MinistrCard = React.memo(({ ministro }: Props) => {
  return <div>...</div>;
});
```

### 3. **Lazy Loading** (pronto para adicionar)
```typescript
const MinistrosPage = lazy(() => import('@/pages/Ministros'));

<Suspense fallback={<Spinner />}>
  <MinistrosPage />
</Suspense>
```

### 4. **Image Optimization** (pronto para adicionar)
Com Vite:
```typescript
import { lazy } from 'react';
const image = new URL('../image.png', import.meta.url).href;
```

---

## Testing Structure (Pronto para Implementar)

```
__tests__/
├── components/
│   ├── Button.test.tsx
│   └── Modal.test.tsx
├── pages/
│   ├── Dashboard.test.tsx
│   └── Ministros.test.tsx
└── services/
    └── api.test.ts
```

### Exemplo de Teste
```typescript
import { render, screen } from '@testing-library/react';
import { Button } from '@/components/ui';

test('Button renders with text', () => {
  render(<Button>Click me</Button>);
  expect(screen.getByText('Click me')).toBeInTheDocument();
});
```

---

## Build & Deployment

### Dev Environment
```bash
npm run dev
# → http://localhost:3000 (Vite HMR)
```

### Production Build
```bash
npm run build
# → /dist/ (otimizado com tree-shaking)
```

### Estrutura de Build
```
dist/
├── index.html
├── assets/
│   ├── main.xxxxx.js (bundled)
│   ├── main.xxxxx.css (bundled)
│   └── ... (code-split chunks)
```

---

## Roadmap de Melhorias

### Curto Prazo
- [ ] Conectar com Backend Java real
- [ ] Adicionar testes unitários
- [ ] Setup de linting (ESLint)
- [ ] Prettier para format automático

### Médio Prazo
- [ ] Context API para state compartilhado
- [ ] Lazy loading de componentes
- [ ] Caching estratégico de dados
- [ ] PWA para offline support
- [ ] Notificações em tempo real (WebSocket)

### Longo Prazo
- [ ] Redux se crescer complexidade de state
- [ ] GraphQL ao invés de REST
- [ ] E2E tests (Cypress/Playwright)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Monitoring e analytics

---

## Stack Tecnológico

```
┌─────────────────────────────────────────┐
│          Frontend Stack                  │
├─────────────────────────────────────────┤
│ React 18          (UI Framework)        │
│ TypeScript        (Type Safety)         │
│ React Router 6    (Navigation)          │
│ Tailwind CSS 3    (Styling)            │
│ Vite 5            (Bundler)            │
│ Lucide React      (Icons)              │
│ date-fns          (Date Handling)      │
│ clsx              (Conditional Classes) │
└─────────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────────┐
│         Backend (Java)                  │
├─────────────────────────────────────────┤
│ Spring Boot       (Framework)           │
│ PostgreSQL/MySQL  (Database)            │
│ REST API          (Communication)       │
└─────────────────────────────────────────┘
```

---

## Conclusão

A arquitetura foi projetada para ser:
- ✅ **Modular**: Componentes independentes e reutilizáveis
- ✅ **Escalável**: Pronta para crescer em complexidade
- ✅ **Maintível**: Code limpo e bem organizado
- ✅ **Type-Safe**: TypeScript em toda parte
- ✅ **Acessível**: WCAG 2.1 AA compliant
- ✅ **Performática**: Otimizada desde o start
- ✅ **Testável**: Estrutura pronta para testes

Este é um sólido foundation para o sistema crescer! 🚀
