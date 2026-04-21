# 🤝 Contributing Guide

Obrigado por considerar contribuir para este projeto! Este guia ajudará você a entender como colaborar de forma eficaz.

---

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Commit Guidelines](#commit-guidelines)
5. [Creating Components](#creating-components)
6. [Making PRs](#making-prs)
7. [Testing](#testing)
8. [Coding Standards](#coding-standards)
9. [Common Issues](#common-issues)

---

## Code of Conduct

- Seja respeitoso com contribuintes
- Mantenha discussões profissionais e construtivas
- Reportar bugs de segurança em privado via email
- Rejeitar discriminação de qualquer tipo

---

## Getting Started

### Prerequisites
- Node.js 16+ ou superior
- npm ou yarn
- Git
- VS Code (recomendado) ou editor de sua preferência

### Clone o Repositório
```bash
git clone https://github.com/seu-usuario/pj-bc-frontend.git
cd pj-bc-frontend
```

### Crie uma Branch
```bash
# Para uma nova feature
git checkout -b feature/nome-descritivo

# Para um bugfix
git checkout -b fix/nome-descritivo

# Para melhorias
git checkout -b docs/nome-descritivo
```

---

## Development Setup

### 1. Instale Dependências
```bash
npm install
# ou
yarn install
```

### 2. Execute o Dev Server
```bash
npm run dev
# → http://localhost:3000
```

### 3. Verifique o Build
```bash
npm run build
# Verifica se há erros de compilação TypeScript
```

### 4. Lint do Código (futuro)
```bash
npm run lint
# Verificará code style
```

---

## Commit Guidelines

### Format
```
<tipo>(<escopo>): <descrição breve>

<descrição detalhada (opcional)>

<tags (optional)>
```

### Tipos de Commits
- **feat**: Nova feature
- **fix**: Correção de bug
- **docs**: Documentação
- **style**: Formatting, sem mudanças lógicas
- **refactor**: Refatoração de código
- **perf**: Melhorias de performance
- **test**: Adicionar/atualizar testes
- **ci**: Alterações CI/CD

### Exemplos
```bash
git commit -m "feat(ministros): adicionar filtro por funcao"
git commit -m "fix(Dashboard): corrigir calculo da media de feedback"
git commit -m "docs(README): melhorar secao de instalacao"
git commit -m "refactor(api.ts): extrair logica de erro para helpers"
```

### Mensagens Boas vs Ruins

✅ **Bom:**
```
feat(escalas): adicionar validacao de conflitos de horario

- Implementar verificacao de ministros duplicados
- Mostrar alerta quando ha sobreposicao
- Adicionar testes unitarios
```

❌ **Ruim:**
```
update files
fix stuff
melhor
```

---

## Creating Components

### 1. Componente Simples (UI)

Adicione em `src/components/ui.tsx`:

```typescript
interface MyComponentProps {
  label?: string;
  disabled?: boolean;
  className?: string;
}

export const MyComponent = forwardRef<HTMLDivElement, MyComponentProps>(
  ({ label, disabled, className, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={clsx(
          'my-component',
          disabled && 'opacity-50',
          className
        )}
        {...props}
      >
        {label}
      </div>
    );
  }
);

MyComponent.displayName = 'MyComponent';
```

### 2. Componente de Página (Feature)

Crie `src/pages/NewPage.tsx`:

```typescript
import { useEffect, useState } from 'react';
import { Button, Card, Input } from '@/components/ui';
import { NewPageService } from '@/services/api';
import type { DataType } from '@/types';

export const NewPage = () => {
  const [data, setData] = useState<DataType[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      const result = await NewPageService.getData();
      setData(result);
    } catch (err) {
      setError('Erro ao carregar dados');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) return <div>Carregando...</div>;

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Nova Página</h1>
      
      {error && <div className="text-red-600">{error}</div>}
      
      <div className="grid gap-4">
        {data.map((item) => (
          <Card key={item.id}>
            {/* Conteúdo */}
          </Card>
        ))}
      </div>
    </div>
  );
};
```

### 3. Adicione à Rota

Em `src/main.tsx`:

```typescript
import { NewPage } from '@/pages/NewPage';

const routes = [
  // ... andere rotas
  {
    path: '/nova-pagina',
    element: <NewPage />,
  },
];
```

---

## Making PRs

### 1. Prepare Sua Branch
```bash
git pull origin main
git rebase main  # Ou git merge main
```

### 2. Push para Fork
```bash
git push origin feature/seu-nome
```

### 3. Abra um Pull Request

**Título:**
```
[Feature/Fix] Descrição breve do que foi implementado
```

**Descrição:**
```markdown
## Descrição
Explique brevemente o que foi feito e por quê.

## Mudanças
- [ ] Feature X
- [ ] Feature Y

## Testing
Como foi testado? Está rodando localmente?

## Checklist
- [ ] Código segue as convenções do projeto
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Sem warnings do linter
- [ ] Build passa com sucesso

## Screenshots (se aplicável)
Adicione imagens de novo visual ou comportamento.
```

### 4. Code Review

- Responda aos comentários prontamente
- Faça as mudanças pedidas em novos commits
- Use `git push` novamente, PR atualiza automaticamente
- Não reescreva a história (evite force push)

---

## Testing

### Setup (Futuro)
```bash
npm install --save-dev vitest @testing-library/react
```

### Estrutura
```
__tests__/
├── components/
├── pages/
└── services/
```

### Exemplo: Teste de Componente
```typescript
// __tests__/components/Button.test.tsx
import { render, screen } from '@testing-library/react';
import { Button } from '@/components/ui';

describe('Button', () => {
  it('renders with text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('handles click', () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Click</Button>);
    screen.getByText('Click').click();
    expect(handleClick).toHaveBeenCalled();
  });
});
```

### Rodando Testes
```bash
npm run test
npm run test:ui      # UI interativa
npm run test:watch   # Watch mode
```

---

## Coding Standards

### 1. TypeScript

Sempre use tipos explícitos:
```typescript
// ✅ Bom
const users: User[] = [];
const handleSubmit = (data: FormData): void => {};

// ❌ Ruim
const users = [];
const handleSubmit = (data) => {};
```

### 2. Naming Conventions

```typescript
// Components - PascalCase
const UserCard = () => {};
const FormModal = () => {};

// Functions - camelCase
const getUserById = () => {};
const handleSubmit = () => {};

// Constants - UPPER_SNAKE_CASE
const MAX_RETRIES = 3;
const API_TIMEOUT = 5000;

// Types/Interfaces - PascalCase
interface UserProps {}
type StatusType = 'active' | 'inactive';
```

### 3. File Organization

```typescript
// 1. Imports
import React from 'react';
import { Button } from '@/components/ui';
import { service } from '@/services/api';
import type { MyType } from '@/types';

// 2. Types/Interfaces
interface ComponentProps {
  label?: string;
}

// 3. Component
export const Component = (props: ComponentProps) => {
  return <div />;
};

// 4. Exports
export default Component;
```

### 4. Comments

```typescript
// ✅ Explicar POR QUE, não O QUE
// Usamos getTime() ao invés de Date.now() porque browser antigos
// não suportam Date.now()
const timestamp = new Date().getTime();

// ❌ Óbvio demais
// Incrementar counter
counter++;
```

### 5. Acessibilidade

```typescript
// ✅ Bom
<button aria-label="Fechar modal">
  <X />
</button>

<input
  aria-invalid={!!error}
  aria-describedby="error-message"
/>

// ❌ Ruim
<button>x</button>

<input />
```

### 6. Responsividade

```typescript
// ✅ Mobile-first
<div className="px-4 md:px-6 lg:px-8">
  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
    {/* Conteúdo */}
  </div>
</div>

// ❌ Desktop-first (difícil de manter)
<div className="px-8 sm:px-6 xs:px-4">
```

---

## Common Issues

### Problema: TypeScript Error - Type Mismatch
```
TS2322: Type 'any[]' is not assignable to type 'User[]'
```

**Solução:**
```typescript
// ❌ Antes
const users: User[] = await service.getUsers();

// ✅ Depois
const users = await service.getUsers() as User[];
// ou melhor (arrume a tipagem no serviço)
```

### Problema: Componente não renderiza após mudança
```typescript
// ❌ Esqueceu return
export const MyComponent = () => {
  <div>Conteúdo</div>  // Erro!
};

// ✅ Correto
export const MyComponent = () => {
  return <div>Conteúdo</div>;
};
```

### Problema: Hook chamado fora de componente
```
React Hook "useState" is called conditionally
```

**Solução:**
```typescript
// ❌ Errado - Hooks dentro de if
const Component = () => {
  if (someCondition) {
    const [state] = useState();  // ERRO!
  }
};

// ✅ Correto - Hooks sempre executam
const Component = () => {
  const [state] = useState();
  if (someCondition) {
    // usar state
  }
};
```

### Problema: API calls fora de sync
```typescript
// ❌ Errado - API call somente com dependency array vazio
useEffect(() => {
  const users = await service.getUsers();  // Que roda continuamente!
}, []);

// ✅ Correto
useEffect(() => {
  const fetchUsers = async () => {
    const users = await service.getUsers();
    setUsers(users);
  };
  fetchUsers();
}, []);
```

### Problema: Performance - Componente rende múltiplas vezes

**Solução:** Use React DevTools Profiler
```bash
npm install react-devtools
```

---

## Resources

- [React Docs](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS](https://tailwindcss.com/docs)
- [React Router](https://reactrouter.com/)
- [Vite Guide](https://vitejs.dev/guide/)
- [Web Accessibility](https://www.a11y-101.com/)

---

## Questions?

- 💬 Abra uma **Discussion** no GitHub
- 🐛 Reporte bugs com **Issues**
- 📧 Contato direto com maintainers

---

## Thank You! 🙏

Sua contribuição ajuda a tornar este projeto melhor para todos!

**Happy coding!** 🚀
