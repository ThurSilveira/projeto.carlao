# 🎛️ Sistema de Gestão de Escalas - Frontend

Frontend moderno, dinâmico e acessível para o sistema de gestão de escalas de ministros da paróquia, desenvolvido em **React + TypeScript + Tailwind CSS**.

## 🌟 Características

### ✅ Moderno & Dinâmico
- **React 18** com Hooks e Context API
- **TypeScript** para type safety completo
- **Tailwind CSS** para design responsivo
- **Lucide React** para ícones modernos
- **Vite** para bundling rápido

### ♿ Acessível (WCAG 2.1 AA)
- Navegação por teclado completa
- ARIA labels em todos os componentes
- Estrutura semântica HTML5
- Contrast ratios adequados
- Skip to content link
- Focus visible states
- Modo claro/escuro com contraste

### 📱 Responsivo
- Mobile-first design
- Suporte total a tablets e desktop
- Menu mobile colapsável
- Grid adaptável

### 🎨 UI/UX de Qualidade
- Design consistente com Tailwind
- Animações suaves
- Feedback visual em todas as interações
- Componentes reutilizáveis
- Dark mode nativo
- Loading states
- Error handling

## 🚀 Início Rápido

### Pré-requisitos
- Node.js 16+ 
- npm ou pnpm

### Instalação

```bash
# Clone ou acesse a pasta frontend
cd frontend

# Instale as dependências
npm install

# Copie o arquivo .env
cp .env.example .env

# Configure a API se necessário
# Edite .env com sua URL de API
```

### Desenvolvimento

```bash
# Inicie o servidor de desenvolvimento
npm run dev

# A aplicação será aberta em http://localhost:3000
```

### Build para Produção

```bash
# Build otimizado
npm run build

# Preview da build
npm run preview
```

## 📁 Estrutura do Projeto

```
frontend/
├── src/
│   ├── components/          # Componentes reutilizáveis
│   │   ├── ui.tsx          # UI components (Button, Card, etc)
│   │   └── Layout.tsx      # Layout principal
│   ├── pages/              # Páginas da aplicação
│   │   ├── Dashboard.tsx   # Home
│   │   ├── Ministros.tsx   # CRUD Ministros
│   │   ├── Eventos.tsx     # CRUD Eventos
│   │   ├── Escalas.tsx     # Gerenciar Escalas
│   │   ├── Feedback.tsx    # Feedback dos eventos
│   │   └── Auditoria.tsx   # Log de auditoria
│   ├── services/
│   │   └── api.ts          # Chamadas à API (com mock)
│   ├── types.ts            # TypeScript interfaces
│   ├── index.css           # Estilos globais
│   └── main.tsx            # Entry point
├── index.html              # HTML
├── package.json
├── vite.config.ts
├── tailwind.config.js
├── postcss.config.js
├── tsconfig.json
└── README.md
```

## 🎯 Páginas Principal

### Dashboard 📊
- Visão geral do sistema
- Estatísticas em tempo real
- Próximos eventos
- Ministros ativos
- Feedbacks recentes
- Taxa de confirmação e satisfação

### Ministros 👥
- Lista de todos os ministros
- Busca avançada
- Filtro por status (ativo/inativo)
- **CRUD completo**: Criar, editar, visualizar, deletar
- Informações: função, aptidões, contato
- Indicadores de vida sacramental

### Eventos 📅
- Calendário de eventos/missas
- Busca por nome/local
- Filtro por tipo de evento
- **CRUD completo**
- Status cancelado
- Informações: data, horário, local, capacidade

### Escalas 📋
- Gestão de escalas de ministros
- Filtro por status (Proposta, Aprovada, Cancelada)
- Aprovar/Cancelar escalas
- Visualização de conflitos potenciais
- Atribuição inteligente de ministros

### Feedback 💬
- Análise de satisfação
- Estatísticas (média, total, pendentes)
- Respostas do coordenador
- Rastreamento de status
- Filtro por status de resposta

### Auditoria 📝
- Histórico completo de ações
- Filtro por entidade e tipo de ação
- Rastreamento de quem fez o quê
- Data e hora de cada ação
- Status anterior e novo

## 🔌 Integração com Backend

O frontend está preparado para se conectar com a API Java. Atualmente, usa dados mock para demonstração.

### Para conectar com o backend real:

1. **Configure a URL da API** em `.env`:
```env
VITE_API_URL=http://localhost:8080/api
```

2. **Implemente os endpoints** em `src/services/api.ts`:
```typescript
export const MinistroService = {
  getAllMinistros: async (): Promise<Ministro[]> => {
    const response = await fetch(`${API_BASE_URL}/ministros`);
    return response.json();
  },
  // ... outros métodos
};
```

3. **Certifique-se que seu backend** expõe os endpoints necessários com CORS habilitado.

### Endpoints Esperados

```
GET    /api/ministros
POST   /api/ministros
GET    /api/ministros/{id}
PUT    /api/ministros/{id}
DELETE /api/ministros/{id}

GET    /api/eventos
POST   /api/eventos
GET    /api/eventos/{id}
PUT    /api/eventos/{id}
DELETE /api/eventos/{id}

GET    /api/escalas
POST   /api/escalas
GET    /api/escalas/{id}
PUT    /api/escalas/{id}

GET    /api/feedbacks
POST   /api/feedbacks
GET    /api/feedbacks/{id}
PUT    /api/feedbacks/{id}/answer

GET    /api/logs
GET    /api/logs/entidade/{entidade}
```

## 🎨 Customização

### Cores
Edite `tailwind.config.js` para customizar a paleta:
```javascript
colors: {
  primary: { /* sua cor primária */ },
  secondary: { /* sua cor secundária */ },
}
```

### Fontes
Personalize em `tailwind.config.js`:
```javascript
fontFamily: {
  sans: ['Sua Fonte', 'sans-serif'],
}
```

### Componentes
Adicione novos componentes em `src/components/` seguindo o padrão existente.

## ♿ Acessibilidade

O projeto segue **WCAG 2.1 AA**:

- ✅ Contraste mínimo 4.5:1 para texto
- ✅ Navegação por teclado (Tab, Enter, Escape)
- ✅ ARIA labels em inputs e buttons
- ✅ Focus visible em todos elementos interativos
- ✅ Elementos semânticos (main, nav, footer)
- ✅ Skip to content link
- ✅ Alt text em imagens/ícones
- ✅ Modo dark com boa legibilidade
- ✅ Suporte a screen readers

### Testar Acessibilidade
```bash
# Use ferramentas como:
# - axe DevTools (extensão Chrome)
# - WAVE (WebAIM)
# - Lighthouse (DevTools)
# - NVDA (screen reader gratuito)
```

## 📦 Dependências

```json
{
  "react": "^18.2.0",
  "react-dom": "^18.2.0",
  "react-router-dom": "^6.20.0",
  "tailwindcss": "^3.4.0",
  "typescript": "^5.3.0",
  "vite": "^5.0.0",
  "lucide-react": "^0.292.0",
  "clsx": "^2.0.0",
  "date-fns": "^2.30.0"
}
```

## 🐛 Troubleshooting

### Problema: "Cannot find module"
```bash
# Limpe cache
rm -rf node_modules package-lock.json
npm install
```

### Problema: Port 3000 já em uso
```bash
# Mude a porta em vite.config.ts:
server: {
  port: 3001,
}
```

### Problema: CORS na conexão API
- Verifique CORS settings no backend
- Configure headers corretos
- Use proxy em desenvolvimento se necessário

## 📚 Recursos Úteis

- [React Docs](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS](https://tailwindcss.com)
- [React Router](https://reactrouter.com)
- [A11y Project](https://www.a11yproject.com)
- [WCAG 2.1](https://www.w3.org/WAI/WCAG21/quickref/)

## 🤝 Contribuindo

1. Crie uma branch (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

## 📄 Licença

Este projeto é parte do Sistema de Gestão de Escalas - Paróquia São José.

## 👨‍💼 Suporte

Para suporte, abra uma issue no repositório ou entre em contato.

---

**Desenvolvido com ❤️ usando React, TypeScript e Tailwind CSS**
