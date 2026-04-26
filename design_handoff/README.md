# Design Handoff — Escala Ministerial

## Visão Geral

**Escala Ministerial** é um sistema de gestão para coordenadores de ministérios paroquiais. Permite gerenciar ministros, eventos litúrgicos, escalas de serviço, feedbacks e auditoria de ações.

> ⚠️ **Importante:** Os arquivos HTML neste pacote são **protótipos de referência de design** — criados para demonstrar aparência, hierarquia e comportamento. O objetivo é **recriar estes designs no ambiente existente do projeto** (React Native, Flutter, Swift, Kotlin, etc.) usando os padrões e bibliotecas já estabelecidos — **não** fazer deploy direto do HTML.

---

## Fidelidade

**Alta fidelidade (hifi).** Os protótipos contêm cores finais, tipografia, espaçamentos, ícones, interações e estados. Implemente pixel a pixel usando o design system existente do projeto.

---

## Design Tokens

### Cores — Modo Claro

| Token | Hex | Uso |
|---|---|---|
| `primary` | `#583400` | Marrom escuro — ações principais, nav ativa, links |
| `primaryLight` | `#F5E6D3` | Fundo de badges, chips ativos, hover suave |
| `secondary` | `#0F766E` | Verde-azulado — destaque secundário |
| `secondaryLight` | `#CCFBF1` | Fundo de respostas, tags secundárias |
| `amber` | `#D97706` | Atenção, status proposta, estrelas |
| `amberLight` | `#FFF7ED` | Fundo de alertas |
| `success` | `#059669` | Confirmado, ativo |
| `successLight` | `#D1FAE5` | — |
| `error` | `#DC2626` | Cancelado, removido |
| `errorLight` | `#FEE2E2` | — |
| `info` | `#0284C7` | Auditoria, informativo |
| `infoLight` | `#E0F2FE` | — |
| `neutral` | `#6B7280` | Inativo, desabilitado |
| `neutralLight` | `#F3F4F6` | — |
| `surface` | `#FFFFFF` | Fundo de cards, modais |
| `surfaceVariant` | `#F8FAFC` | Fundo de inputs, áreas de detalhe |
| `onSurface` | `#0F172A` | Texto principal |
| `onSurfaceVariant` | `#64748B` | Texto secundário, ícones |
| `border` | `#E2E8F0` | Bordas, divisores |
| `appBg` | `#F1F5F9` | Fundo geral da tela |

### Cores — Modo Escuro

| Token | Hex |
|---|---|
| `primary` | `#D4956A` |
| `primaryLight` | `#3D2010` |
| `secondary` | `#2DD4BF` |
| `secondaryLight` | `#0D3330` |
| `amber` | `#FBBF24` |
| `amberLight` | `#3D2B00` |
| `success` | `#34D399` |
| `successLight` | `#052E1C` |
| `error` | `#F87171` |
| `errorLight` | `#3B0F0F` |
| `info` | `#38BDF8` |
| `infoLight` | `#0C2440` |
| `neutral` | `#94A3B8` |
| `neutralLight` | `#1E2535` |
| `surface` | `#1E2535` |
| `surfaceVariant` | `#252D3D` |
| `onSurface` | `#F1F5F9` |
| `onSurfaceVariant` | `#94A3B8` |
| `border` | `#2D3748` |
| `appBg` | `#141927` |

### Tipografia

| Papel | Família | Peso | Tamanho |
|---|---|---|---|
| Títulos de tela (H1) | Playfair Display | 800 | 22px / 22sp |
| Títulos de seção (H2) | Nunito | 700 | 15px / 15sp |
| Body padrão | Nunito | 400–600 | 14px / 14sp |
| Labels, badges | Nunito | 600–700 | 12–13px / 12–13sp |
| Micro-labels | Nunito | 600 | 10–11px / 10–11sp |

### Espaçamento

| Nome | Valor |
|---|---|
| xs | 4px |
| sm | 8px |
| md | 12px |
| base | 16px |
| lg | 20px |
| xl | 24px |
| 2xl | 28px |

### Border Radius

| Elemento | Raio |
|---|---|
| Cards | 14px |
| Botões, inputs | 9–10px |
| Badges/chips | 99px (pill) |
| Ícone-container (KPI, avatar-bg) | 10px |
| Avatar | 50% (circular) |
| Modais | 18px |
| Bottom sheet | 24px topo |

### Sombras

```
Card normal:   0 1px 3px rgba(15,23,42,0.06), 0 1px 2px rgba(15,23,42,0.04)
Card hover:    0 4px 16px rgba(88,52,0,0.12), 0 1px 3px rgba(15,23,42,0.06)
Modal:         0 24px 64px rgba(10,14,22,0.30)
Bottom Nav:    0 -4px 20px rgba(15,23,42,0.08)
Sidebar:       2px 0 12px rgba(15,23,42,0.04)
FAB:           0 4px 20px <primary>55
```

---

## Estrutura de Navegação

### Desktop (≥ 768px) — Sidebar fixa lateral

- Largura: 240px, altura 100vh, posição sticky
- Fundo: `surface`, borda direita: `border`
- Logo no topo (ícone + "Escala / MINISTERIAL")
- 6 itens de navegação com ícone + label
- Item ativo: fundo `primary + 14%`, texto e ícone em `primary`, ponto indicador à direita
- Item inativo: texto `onSurface`, ícone `onSurfaceVariant`
- Rodapé: avatar + nome do coordenador + paróquia

### Mobile (< 768px) — Bottom Navigation

- Altura fixa: 72px, posição fixed bottom
- Fundo: `surface`, borda topo: `border`
- 6 itens com ícone (20px) + label (10px)
- Item ativo: ícone com pill-bg `primary + 1A`, cor `primary`
- Item inativo: cor `onSurfaceVariant`
- Top Bar sticky: logo + nome do app + avatar do usuário

---

## Telas

### 1. Painel (Dashboard)

**Layout mobile:** scroll vertical, sem padding lateral na seção hero.

**Seção Hero (header)**
- Gradiente: `linear-gradient(135deg, primary 0%, primary + cc 100%)`
- Padding: 28px top/20px horizontal/20px bottom
- Border radius inferior: 24px
- Conteúdo: saudação (13px, branco 65%), título "Painel de Controle" (22px Playfair 800, branco), data atual (13px, branco 60%)

**Grade de KPIs (2 colunas, gap 12px)**
6 cards clicáveis (navegam para a tela correspondente):
- Padding interno: 16px
- Ícone em container 38×38px com bg colorido (primaryLight/secondaryLight/etc.)
- Valor em destaque: 26px Nunito 800, `onSurface`
- Label: 12px Nunito 500, `onSurfaceVariant`

| KPI | Ícone | Cor |
|---|---|---|
| Ministros Ativos | people/group | primary |
| Eventos este Mês | calendar | secondary |
| Escalas Aprovadas | list | #7C3AED |
| Nota Média | star | amber |
| Feedbacks Pendentes | reply | error |
| Registros de Auditoria | clock | info |

**Próximos Eventos (lista vertical, gap 10px)**
- Cabeçalho: "Próximos Eventos" + link "Ver todos"
- Card por evento: data em bloco 44×44px (dia em 16px 800, mês em 9px uppercase), nome, horário e local, chip de tipo

**Escalas Recentes**
- Card único agrupado com divisores internos
- Cada linha: nome do evento + data + qtd ministros (esquerda) + StatusBadge (direita)

---

### 2. Ministros

**Layout:** SearchBar no topo, lista de cards (gap 10px).

**Card de Ministro**
- Padding: 14px 16px
- Avatar 44px (iniciais, cor baseada em ativo/inativo)
- Nome (15px 700) + badge "Inativo" se desativado
- Função (13px, `onSurfaceVariant`)
- Telefone com ícone phone (12px)
- Botões de ação (34×34px, borderRadius 8px): Indisponibilidades (ícone calendar, cor secondary), Editar (edit), Remover (trash, cor error)

**Modal de Criação/Edição**
- Campos: Nome completo (obrigatório), Função (select), Telefone, E-mail, Checkbox "Ministro ativo"
- CTA: "Cancelar" (ghost) + "Adicionar" / "Salvar" (primary)

**Modal de Indisponibilidades**
- Lista de datas com motivo em pill vermelho, botão X para remover
- Formulário inferior em `surfaceVariant`: date picker + input motivo + botão "Registrar"

**FAB (mobile):** posição fixed bottom-right (90px do bottom), cor primary, ícone "+" expandindo para label no hover

---

### 3. Eventos

**Layout:** idêntico ao de Ministros (search + lista de cards).

**Card de Evento**
- Nome + StatusBadge (Ativo/Concluído) no topo
- Metadados em linha: data (ícone calendar), horário (ícone clock), local (ícone mapPin) — 12px cada
- Rodapé separado por divisor: chip de tipo + capacidade (esquerda), botões Editar + Cancelar (direita)

**Modal de Evento**
- Campos: Nome, Data + Horário (grid 2 colunas), Local, Tipo (select) + Capacidade (grid 2 colunas)

---

### 4. Escalas

**Layout:** FilterChips horizontais com scroll + lista de cards.

**FilterChips:** Todos | Proposta | Aprovada | Confirmada | Cancelada

**Card de Escala**
- Cabeçalho: nome do evento + data (esquerda), StatusBadge (direita)
- Seção "Ministros escalados": lista de linhas com avatar 28px + nome + função + status (Confirmado com ✓ verde ou "Pendente" cinza)
- Rodapé com botões de ação por status:
  - PROPOSTA → "Aprovar" (primary)
  - APROVADA → "Confirmar" (success) + "Cancelar" (secondary/error)

**Modal Gerar Escala**
- Select de evento + texto explicativo + CTA "Gerar Escala" (com ícone ⚡)

---

### 5. Feedback

**Layout:** 2 cards de stats no topo + FilterChips + lista de feedbacks.

**Cards de Stats (2 colunas)**
- Nota Média: valor 28px 800 amber, estrelas 12px, fundo `amberLight`
- Pendentes: valor 28px 800, cor error (>0) ou success (=0), fundo `errorLight`/`successLight`

**Card de Feedback**
- Avatar 38px + nome do ministro + evento e data
- Nota numérica: 20px 800, cor baseada em valor (≥8 success, ≥6 amber, <6 error)
- Comentário em itálico 13px
- Resposta da coordenação (se existir): container com borda esquerda `secondary` 3px, fundo `secondaryLight`, label "RESPOSTA DA COORDENAÇÃO" uppercase
- Rodapé: StatusBadge + botão "Responder" (apenas pendentes)

**Modal Responder**
- Resumo do feedback em container `surfaceVariant`
- Textarea de resposta
- CTA "Enviar Resposta" (com ícone send)

---

### 6. Auditoria

**Layout:** FilterChips por entidade (Todas | Escala | Ministro | Evento | Feedback) + card único com linhas.

**Linha de Log**
- Ícone 36×36px com bg/cor semântico por tipo de ação
- StatusBadge da ação + tag da entidade (surfaceVariant, border)
- Descrição 13px
- Data/hora + usuário em linha (11px, ícones clock e user)

**Tipos de ação e cores:**
| Ação | Cor |
|---|---|
| CRIAÇÃO | success |
| APROVAÇÃO | primary |
| CONFIRMAÇÃO | secondary |
| EDIÇÃO | amber |
| CANCELAMENTO | error |
| EXCLUSÃO | error |

---

## Componentes Reutilizáveis

### StatusBadge
Pill com dot colorido + label. Variantes: PROPOSTA (amber), APROVADA (primary), CONFIRMADA (success), CANCELADA (error), ATIVO (success), CONCLUIDO (neutral).

### Avatar
Círculo com iniciais do nome (2 primeiras palavras). Tamanhos: 28, 30, 32, 36, 38, 44px. Cor derivada do status.

### Card
Border radius 14px, borda 1px `border`, sombra leve. Estado hover: borda `primary + 28%`, sombra elevada, translateY(-1px).

### FilterChip
Pill com borda. Ativo: borda `primary`, fundo `primaryLight`, texto `primary` 700. Inativo: borda `border`, fundo `surface`.

### Toast
Fixed bottom-center, fundo `onSurface`, texto branco, border radius 12px, animação slideUp. Duração: 2,8s.

### Modal / BottomSheet
- Desktop: dialog centralizado, max-width 480px, backdrop blur 3px rgba(10,14,22,0.55)
- Mobile: **usar bottom sheet** que sobe de baixo, border radius 24px no topo, handle visual no topo
- Cabeçalho: título 17px 700 + botão fechar X

### Input / Select / Textarea
- Fundo: `surfaceVariant`
- Borda: 1.5px `border` → `primary` no focus
- Border radius: 9px
- Padding: 10px 12px
- Em modo escuro: `colorScheme: dark` para pickers nativos

---

## Comportamento e Interações

### Transições de tela
- Fade + slide horizontal suave (200ms ease)
- Ao trocar de aba no nav: nova tela entra pela esquerda

### Modais
- Entrada: slideUp 200ms ease
- Backdrop: fadeIn 150ms

### Cards clicáveis
- Hover: elevação + borda colorida (desktop)
- Press: scale(0.98) + feedback tátil (mobile)

### Fluxo de Escala
```
Gerar → PROPOSTA → [Aprovar] → APROVADA → [Confirmar] → CONFIRMADA
                               [Cancelar] → CANCELADA
```

### FAB (mobile)
- Posição: 90px acima do bottom (para não sobrepor nav bar)
- Hover/press: scale(1.04), sombra elevada

---

## Implementação Mobile (Android / iOS)

### Breakpoints
- Mobile: < 768px → Bottom Navigation + Top AppBar
- Tablet/Desktop: ≥ 768px → Sidebar lateral

### Android (Jetpack Compose ou XML)
- Bottom Navigation: `BottomNavigationView` ou `NavigationBar` (Material 3)
- Cards: `CardView` / `ElevatedCard` com `cornerRadius = 14dp`
- Top AppBar: `TopAppBar` com logo + avatar
- FAB: `ExtendedFloatingActionButton` posicionado acima da nav bar
- Modais de criação/edição: `BottomSheetDialog`
- Tipografia: usar `downloadable fonts` do Google Fonts (Nunito + Playfair Display)
- Tema escuro: usar `DynamicColorScheme` com seeds derivadas do `primary` (#583400 light / #D4956A dark)

### iOS (SwiftUI)
- Bottom Navigation: `TabView` com `.tabItem`
- Cards: `RoundedRectangle(cornerRadius: 14)` + `.shadow`
- Modais: `.sheet(isPresented:)` com bottom sheet
- FAB: `ZStack` com botão flutuante overlay acima do TabView
- Tipografia: `Font.custom("Nunito-Bold", size: 15)` — adicionar ao bundle
- Dark mode: usar `@Environment(\.colorScheme)` para alternar entre os dois conjuntos de tokens

### Flutter
- Bottom Navigation: `NavigationBar` (Material 3)
- Cards: `Card` com `shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14))`
- Tipografia: pacote `google_fonts` → `GoogleFonts.nunito()` + `GoogleFonts.playfairDisplay()`
- Temas: `ThemeData.light()` e `ThemeData.dark()` com `ColorScheme.fromSeed(seedColor: Color(0xFF583400))`
- FAB: `floatingActionButtonLocation: FloatingActionButtonLocation.endFloat` + `floatingActionButtonAnimator`

---

## Arquivos de Referência

| Arquivo | Conteúdo |
|---|---|
| `Escala Ministerial.html` | Protótipo completo — navegação, layout, dark mode, tweaks |
| `components.jsx` | Todos os componentes UI (Card, Avatar, Badge, Modal, etc.) com ThemeContext |
| `screens.jsx` | As 6 telas completas com toda a lógica de UI |
| `data.js` | Mock data (estrutura dos objetos: ministros, eventos, escalas, feedbacks, auditoria) |

---

## Estrutura de Dados

```typescript
// Ministro
interface Ministro {
  id: number;
  nome: string;
  funcao: 'Leitor' | 'Música' | 'Acólito' | 'Ministro da Eucaristia' | 'Coordenador' | string;
  telefone: string;
  email: string;
  ativo: boolean;
}

// Indisponibilidade
interface Indisponibilidade {
  ministroId: number;
  data: string; // YYYY-MM-DD
  motivo: string;
}

// Evento
interface Evento {
  id: number;
  nome: string;
  data: string;       // YYYY-MM-DD
  horario: string;    // HH:mm
  local: string;
  tipo: string;
  capacidade: number;
  status: 'ATIVO' | 'CONCLUIDO';
}

// Escala
interface Escala {
  id: number;
  eventoId: number;
  eventoNome: string;
  data: string;
  status: 'PROPOSTA' | 'APROVADA' | 'CONFIRMADA' | 'CANCELADA';
  ministros: Array<{
    ministroId: number;
    nome: string;
    funcao: string;
    confirmado: boolean;
  }>;
}

// Feedback
interface Feedback {
  id: number;
  ministroId: number;
  ministroNome: string;
  eventoNome: string;
  nota: number;       // 1–10
  comentario: string;
  resposta: string | null;
  data: string;
  respondido: boolean;
}

// Log de Auditoria
interface LogAuditoria {
  id: number;
  acao: 'CRIAÇÃO' | 'APROVAÇÃO' | 'CONFIRMAÇÃO' | 'EDIÇÃO' | 'CANCELAMENTO' | 'EXCLUSÃO';
  entidade: 'Escala' | 'Ministro' | 'Evento' | 'Feedback';
  descricao: string;
  usuario: string;
  data: string; // "YYYY-MM-DD HH:mm"
}
```

---

## Prompt para Claude Code

Copie e cole o seguinte prompt no Claude Code junto com os arquivos desta pasta:

---

```
Você receberá um pacote de design hifi (alta fidelidade) do app "Escala Ministerial" — sistema de gestão de ministérios paroquiais.

Os arquivos HTML são protótipos de referência. Sua tarefa é recriar este design no [framework do projeto: React Native / Flutter / SwiftUI / Jetpack Compose].

Siga o README.md à risca para:
1. Aplicar os design tokens (cores light/dark, tipografia, espaçamento, raios, sombras)
2. Implementar as 6 telas: Dashboard, Ministros, Eventos, Escalas, Feedback, Auditoria
3. Usar Bottom Navigation no mobile e Sidebar no tablet/desktop
4. Implementar dark mode completo (todos os tokens têm versão escura)
5. Respeitar os componentes reutilizáveis: Card, Avatar, StatusBadge, FilterChip, Modal/BottomSheet, Toast, FAB
6. Implementar os fluxos de interação descritos (especialmente o fluxo de aprovação de Escala)
7. Usar as fontes: Nunito (400/500/600/700/800) e Playfair Display (600/700)

Abra o arquivo Escala Ministerial.html no browser para ver o protótipo interativo completo com dark mode e tweaks de cor.
```
