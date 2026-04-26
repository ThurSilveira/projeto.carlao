// Escala Ministerial — Shared Components (with ThemeContext)

const LIGHT_COLORS = {
  primary: '#583400',
  primaryLight: '#F5E6D3',
  secondary: '#0F766E',
  secondaryLight: '#CCFBF1',
  amber: '#D97706',
  amberLight: '#FFF7ED',
  success: '#059669',
  successLight: '#D1FAE5',
  warning: '#D97706',
  warningLight: '#FFF7ED',
  error: '#DC2626',
  errorLight: '#FEE2E2',
  info: '#0284C7',
  infoLight: '#E0F2FE',
  neutral: '#6B7280',
  neutralLight: '#F3F4F6',
  surface: '#FFFFFF',
  surfaceVariant: '#F8FAFC',
  onSurface: '#0F172A',
  onSurfaceVariant: '#64748B',
  border: '#E2E8F0',
  appBg: '#F1F5F9',
};

const DARK_COLORS = {
  primary: '#D4956A',
  primaryLight: '#3D2010',
  secondary: '#2DD4BF',
  secondaryLight: '#0D3330',
  amber: '#FBBF24',
  amberLight: '#3D2B00',
  success: '#34D399',
  successLight: '#052E1C',
  warning: '#FBBF24',
  warningLight: '#3D2B00',
  error: '#F87171',
  errorLight: '#3B0F0F',
  info: '#38BDF8',
  infoLight: '#0C2440',
  neutral: '#94A3B8',
  neutralLight: '#1E2535',
  surface: '#1E2535',
  surfaceVariant: '#252D3D',
  onSurface: '#F1F5F9',
  onSurfaceVariant: '#94A3B8',
  border: '#2D3748',
  appBg: '#141927',
};

// Keep global COLORS pointing to light (for STATUS_MAP initialization); components use context
const COLORS = LIGHT_COLORS;
window.COLORS = COLORS;
window.LIGHT_COLORS = LIGHT_COLORS;
window.DARK_COLORS = DARK_COLORS;

const ThemeContext = React.createContext(LIGHT_COLORS);
window.ThemeContext = ThemeContext;

function useColors() { return React.useContext(ThemeContext); }
window.useColors = useColors;

// ── Icon ─────────────────────────────────────────────────────────────────────
const ICONS = {
  dashboard: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>,
  ministers: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>,
  events: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>,
  scales: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>,
  feedback: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>,
  audit: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>,
  plus: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>,
  search: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>,
  edit: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>,
  trash: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>,
  check: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"/></svg>,
  x: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>,
  chevronRight: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="9 18 15 12 9 6"/></svg>,
  chevronDown: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="6 9 12 15 18 9"/></svg>,
  filter: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></svg>,
  user: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>,
  calendar: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>,
  mapPin: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>,
  clock: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>,
  reply: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><polyline points="9 17 4 12 9 7"/><path d="M20 18v-2a4 4 0 0 0-4-4H4"/></svg>,
  alertCircle: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>,
  ban: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/></svg>,
  send: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>,
  close: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>,
  menu: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>,
  star: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>,
  starFilled: <svg viewBox="0 0 24 24" fill="#D97706" stroke="#D97706" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>,
  phone: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12 19.79 19.79 0 0 1 1.64 3.32 2 2 0 0 1 3.62 1h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L7.91 8.6a16 16 0 0 0 6 6l.96-.96a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/></svg>,
  mail: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>,
  zap: <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>,
};
window.ICONS = ICONS;

function Icon({ name, size = 20, color = 'currentColor', style = {} }) {
  const icon = ICONS[name];
  if (!icon) return null;
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: size, height: size, color, flexShrink: 0, ...style }}>
      {React.cloneElement(icon, { width: size, height: size })}
    </span>
  );
}
window.Icon = Icon;

// ── Avatar ────────────────────────────────────────────────────────────────────
function Avatar({ nome, size = 36, color }) {
  const C = useColors();
  const c = color || C.primary;
  const initials = nome.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase();
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: c + '22', border: `1.5px solid ${c}33`,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontFamily: "'Nunito', sans-serif", fontWeight: 700,
      fontSize: size * 0.38, color: c, flexShrink: 0,
      letterSpacing: '0.03em',
    }}>
      {initials}
    </div>
  );
}
window.Avatar = Avatar;

// ── StatusBadge ───────────────────────────────────────────────────────────────
function getStatusMap(C) {
  return {
    PROPOSTA:    { label: 'Proposta',     bg: C.amberLight,   color: C.amber,    dot: C.amber },
    APROVADA:    { label: 'Aprovada',     bg: C.primaryLight, color: C.primary,  dot: C.primary },
    CONFIRMADA:  { label: 'Confirmada',   bg: C.successLight, color: C.success,  dot: C.success },
    CANCELADA:   { label: 'Cancelada',    bg: C.errorLight,   color: C.error,    dot: C.error },
    ATIVO:       { label: 'Ativo',        bg: C.successLight, color: C.success,  dot: C.success },
    CONCLUIDO:   { label: 'Concluído',    bg: C.neutralLight, color: C.neutral,  dot: C.neutral },
    SUCCESS:     { label: 'OK',           bg: C.successLight, color: C.success,  dot: C.success },
    WARNING:     { label: 'Aviso',        bg: C.warningLight, color: C.warning,  dot: C.warning },
    ERROR:       { label: 'Erro',         bg: C.errorLight,   color: C.error,    dot: C.error },
    INFO:        { label: 'Info',         bg: C.infoLight,    color: C.info,     dot: C.info },
    NEUTRAL:     { label: 'Neutro',       bg: C.neutralLight, color: C.neutral,  dot: C.neutral },
    'CRIAÇÃO':   { label: 'Criação',      bg: C.successLight, color: C.success,  dot: C.success },
    'APROVAÇÃO': { label: 'Aprovação',    bg: C.primaryLight, color: C.primary,  dot: C.primary },
    CANCELAMENTO:{ label: 'Cancelamento', bg: C.errorLight,   color: C.error,    dot: C.error },
    'CONFIRMAÇÃO':{ label: 'Confirmação', bg: C.secondaryLight, color: C.secondary, dot: C.secondary },
    'EDIÇÃO':    { label: 'Edição',       bg: C.amberLight,   color: C.amber,    dot: C.amber },
    'EXCLUSÃO':  { label: 'Exclusão',     bg: C.errorLight,   color: C.error,    dot: C.error },
  };
}
// Static STATUS_MAP for backward compat
const STATUS_MAP = getStatusMap(LIGHT_COLORS);
window.STATUS_MAP = STATUS_MAP;

function StatusBadge({ status, label: overrideLabel }) {
  const C = useColors();
  const map = getStatusMap(C)[status] || getStatusMap(C).NEUTRAL;
  const label = overrideLabel || map.label;
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: 5,
      padding: '3px 10px', borderRadius: 99,
      background: map.bg, color: map.color,
      fontSize: 12, fontWeight: 600, letterSpacing: '0.02em',
      fontFamily: "'Nunito', sans-serif", whiteSpace: 'nowrap',
    }}>
      <span style={{ width: 6, height: 6, borderRadius: '50%', background: map.dot, flexShrink: 0 }} />
      {label}
    </span>
  );
}
window.StatusBadge = StatusBadge;

// ── SearchBar ─────────────────────────────────────────────────────────────────
function SearchBar({ value, onChange, placeholder = 'Buscar…' }) {
  const C = useColors();
  const [focused, setFocused] = React.useState(false);
  return (
    <div style={{ position: 'relative', width: '100%' }}>
      <span style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: C.onSurfaceVariant, pointerEvents: 'none' }}>
        <Icon name="search" size={16} />
      </span>
      <input
        type="text" value={value}
        onChange={e => onChange(e.target.value)}
        placeholder={placeholder}
        onFocus={() => setFocused(true)}
        onBlur={() => setFocused(false)}
        style={{
          width: '100%', boxSizing: 'border-box',
          padding: '10px 12px 10px 38px',
          borderRadius: 10, border: `1.5px solid ${focused ? C.primary : C.border}`,
          background: C.surface, color: C.onSurface,
          fontSize: 14, fontFamily: "'Nunito', sans-serif",
          outline: 'none', transition: 'border-color 0.15s',
        }}
      />
    </div>
  );
}
window.SearchBar = SearchBar;

// ── Card ──────────────────────────────────────────────────────────────────────
function Card({ children, onClick, style = {}, hover = true }) {
  const C = useColors();
  const [hovered, setHovered] = React.useState(false);
  return (
    <div
      onClick={onClick}
      onMouseEnter={() => hover && setHovered(true)}
      onMouseLeave={() => hover && setHovered(false)}
      style={{
        background: C.surface,
        borderRadius: 14,
        border: `1px solid ${hovered ? C.primary + '40' : C.border}`,
        boxShadow: hovered
          ? `0 4px 16px rgba(88,52,0,0.12), 0 1px 3px rgba(15,23,42,0.06)`
          : `0 1px 3px rgba(15,23,42,0.06), 0 1px 2px rgba(15,23,42,0.04)`,
        cursor: onClick ? 'pointer' : 'default',
        transition: 'box-shadow 0.18s, border-color 0.18s, transform 0.18s',
        transform: hovered && onClick ? 'translateY(-1px)' : 'none',
        ...style,
      }}
    >
      {children}
    </div>
  );
}
window.Card = Card;

// ── FAB ───────────────────────────────────────────────────────────────────────
function FAB({ onClick, label }) {
  const C = useColors();
  const [hovered, setHovered] = React.useState(false);
  return (
    <button onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        position: 'fixed', bottom: 90, right: 20,
        display: 'flex', alignItems: 'center', gap: 8,
        padding: hovered && label ? '14px 20px 14px 16px' : '14px',
        borderRadius: 99, border: 'none',
        background: C.primary, color: '#fff',
        boxShadow: `0 4px 20px ${C.primary}55`,
        cursor: 'pointer', zIndex: 100,
        fontFamily: "'Nunito', sans-serif", fontWeight: 700, fontSize: 14,
        transition: 'all 0.2s',
        transform: hovered ? 'scale(1.04)' : 'scale(1)',
      }}>
      <Icon name="plus" size={22} color="#fff" />
      {hovered && label && <span>{label}</span>}
    </button>
  );
}
window.FAB = FAB;

function FABDesktop({ onClick, label }) {
  const C = useColors();
  const [hovered, setHovered] = React.useState(false);
  return (
    <button onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        display: 'flex', alignItems: 'center', gap: 8,
        padding: '10px 18px', borderRadius: 10, border: 'none',
        background: C.primary, color: '#fff',
        cursor: 'pointer', fontFamily: "'Nunito', sans-serif",
        fontWeight: 700, fontSize: 14, transition: 'all 0.15s',
        transform: hovered ? 'translateY(-1px)' : 'none',
        boxShadow: hovered ? `0 6px 16px ${C.primary}50` : `0 2px 8px ${C.primary}40`,
      }}>
      <Icon name="plus" size={18} color="#fff" />
      {label}
    </button>
  );
}
window.FABDesktop = FABDesktop;

// ── Modal ─────────────────────────────────────────────────────────────────────
function Modal({ open, onClose, title, children, maxWidth = 480 }) {
  const C = useColors();
  if (!open) return null;
  return (
    <div onClick={onClose} style={{
      position: 'fixed', inset: 0, zIndex: 500,
      background: 'rgba(10,14,22,0.55)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      padding: 16, backdropFilter: 'blur(3px)',
      animation: 'fadeIn 0.15s ease',
    }}>
      <div onClick={e => e.stopPropagation()} style={{
        background: C.surface, borderRadius: 18,
        width: '100%', maxWidth, maxHeight: '90vh',
        overflow: 'hidden', display: 'flex', flexDirection: 'column',
        boxShadow: '0 24px 64px rgba(10,14,22,0.30)',
        animation: 'slideUp 0.2s ease',
        border: `1px solid ${C.border}`,
      }}>
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '20px 24px 18px', borderBottom: `1px solid ${C.border}`,
        }}>
          <h2 style={{ margin: 0, fontSize: 17, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>{title}</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 4, borderRadius: 8, color: C.onSurfaceVariant, display: 'flex' }}>
            <Icon name="close" size={20} />
          </button>
        </div>
        <div style={{ padding: '20px 24px 24px', overflowY: 'auto', background: C.surface }}>{children}</div>
      </div>
    </div>
  );
}
window.Modal = Modal;

// ── Form Controls ─────────────────────────────────────────────────────────────
function FormField({ label, children, required }) {
  const C = useColors();
  return (
    <div style={{ marginBottom: 16 }}>
      <label style={{ display: 'block', fontSize: 13, fontWeight: 600, color: C.onSurface, marginBottom: 6, fontFamily: "'Nunito', sans-serif" }}>
        {label}{required && <span style={{ color: C.error, marginLeft: 2 }}>*</span>}
      </label>
      {children}
    </div>
  );
}
window.FormField = FormField;

function Input({ value, onChange, placeholder, type = 'text', ...rest }) {
  const C = useColors();
  const [focused, setFocused] = React.useState(false);
  return (
    <input type={type} value={value}
      onChange={e => onChange(e.target.value)}
      placeholder={placeholder}
      onFocus={() => setFocused(true)}
      onBlur={() => setFocused(false)}
      style={{
        width: '100%', boxSizing: 'border-box',
        padding: '10px 12px', borderRadius: 9,
        border: `1.5px solid ${focused ? C.primary : C.border}`,
        background: C.surfaceVariant, color: C.onSurface,
        fontSize: 14, fontFamily: "'Nunito', sans-serif",
        outline: 'none', transition: 'border-color 0.15s',
        colorScheme: C.surface === DARK_COLORS.surface ? 'dark' : 'light',
      }}
      {...rest}
    />
  );
}
window.Input = Input;

function Select({ value, onChange, children, ...rest }) {
  const C = useColors();
  const [focused, setFocused] = React.useState(false);
  return (
    <select value={value} onChange={e => onChange(e.target.value)}
      onFocus={() => setFocused(true)}
      onBlur={() => setFocused(false)}
      style={{
        width: '100%', boxSizing: 'border-box',
        padding: '10px 12px', borderRadius: 9,
        border: `1.5px solid ${focused ? C.primary : C.border}`,
        background: C.surfaceVariant, color: C.onSurface,
        fontSize: 14, fontFamily: "'Nunito', sans-serif",
        outline: 'none', cursor: 'pointer', appearance: 'none',
        backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%2394A3B8' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E")`,
        backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center',
        paddingRight: 36,
        colorScheme: C.surface === DARK_COLORS.surface ? 'dark' : 'light',
      }}
      {...rest}
    >
      {children}
    </select>
  );
}
window.Select = Select;

function Textarea({ value, onChange, placeholder, rows = 3 }) {
  const C = useColors();
  const [focused, setFocused] = React.useState(false);
  return (
    <textarea value={value} onChange={e => onChange(e.target.value)}
      placeholder={placeholder} rows={rows}
      onFocus={() => setFocused(true)}
      onBlur={() => setFocused(false)}
      style={{
        width: '100%', boxSizing: 'border-box',
        padding: '10px 12px', borderRadius: 9,
        border: `1.5px solid ${focused ? C.primary : C.border}`,
        background: C.surfaceVariant, color: C.onSurface,
        fontSize: 14, fontFamily: "'Nunito', sans-serif",
        outline: 'none', resize: 'vertical', minHeight: 80,
      }}
    />
  );
}
window.Textarea = Textarea;

// ── Button ────────────────────────────────────────────────────────────────────
function Button({ children, onClick, variant = 'primary', size = 'md', disabled = false, color: colorProp }) {
  const C = useColors();
  const [hovered, setHovered] = React.useState(false);
  const c = colorProp || C.primary;
  const variants = {
    primary: { background: disabled ? C.border : (hovered ? c + 'ee' : c), color: disabled ? C.onSurfaceVariant : '#fff', border: 'none', boxShadow: hovered && !disabled ? `0 4px 12px ${c}40` : 'none' },
    secondary: { background: hovered ? c + '18' : 'transparent', color: c, border: `1.5px solid ${hovered ? c : C.border}`, boxShadow: 'none' },
    ghost: { background: hovered ? C.surfaceVariant : 'transparent', color: C.onSurfaceVariant, border: 'none', boxShadow: 'none' },
    danger: { background: hovered ? '#b91c1c' : C.error, color: '#fff', border: 'none', boxShadow: hovered ? '0 4px 12px rgba(220,38,38,0.4)' : 'none' },
  };
  const sizes = {
    sm: { padding: '7px 14px', fontSize: 13, borderRadius: 8 },
    md: { padding: '10px 20px', fontSize: 14, borderRadius: 10 },
    lg: { padding: '13px 26px', fontSize: 15, borderRadius: 11 },
  };
  return (
    <button onClick={disabled ? undefined : onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        display: 'inline-flex', alignItems: 'center', gap: 6,
        fontFamily: "'Nunito', sans-serif", fontWeight: 700,
        cursor: disabled ? 'not-allowed' : 'pointer',
        transition: 'all 0.15s', outline: 'none',
        ...sizes[size], ...variants[variant],
      }}>
      {children}
    </button>
  );
}
window.Button = Button;

// ── EmptyState ────────────────────────────────────────────────────────────────
function EmptyState({ icon = 'alertCircle', message = 'Nenhum resultado encontrado', sub }) {
  const C = useColors();
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 10, padding: '48px 24px', color: C.onSurfaceVariant }}>
      <Icon name={icon} size={40} color={C.border} />
      <p style={{ margin: 0, fontSize: 15, fontWeight: 600, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", textAlign: 'center' }}>{message}</p>
      {sub && <p style={{ margin: 0, fontSize: 13, color: C.neutral, fontFamily: "'Nunito', sans-serif", textAlign: 'center' }}>{sub}</p>}
    </div>
  );
}
window.EmptyState = EmptyState;

// ── FilterChip ────────────────────────────────────────────────────────────────
function FilterChip({ label, active, onClick }) {
  const C = useColors();
  return (
    <button onClick={onClick} style={{
      padding: '6px 14px', borderRadius: 99,
      border: `1.5px solid ${active ? C.primary : C.border}`,
      background: active ? C.primaryLight : C.surface,
      color: active ? C.primary : C.onSurfaceVariant,
      fontSize: 13, fontWeight: active ? 700 : 500,
      fontFamily: "'Nunito', sans-serif",
      cursor: 'pointer', transition: 'all 0.15s', whiteSpace: 'nowrap',
    }}>
      {label}
    </button>
  );
}
window.FilterChip = FilterChip;

// ── Divider ───────────────────────────────────────────────────────────────────
function Divider() {
  const C = useColors();
  return <div style={{ height: 1, background: C.border }} />;
}
window.Divider = Divider;

// ── ConfirmDialog ─────────────────────────────────────────────────────────────
function ConfirmDialog({ open, onClose, onConfirm, title, message, confirmLabel = 'Confirmar', danger = false }) {
  const C = useColors();
  return (
    <Modal open={open} onClose={onClose} title={title} maxWidth={400}>
      <p style={{ margin: '0 0 24px', fontSize: 14, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", lineHeight: 1.6 }}>{message}</p>
      <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
        <Button variant="ghost" onClick={onClose}>Cancelar</Button>
        <Button variant={danger ? 'danger' : 'primary'} onClick={onConfirm}>{confirmLabel}</Button>
      </div>
    </Modal>
  );
}
window.ConfirmDialog = ConfirmDialog;

// ── Toast ─────────────────────────────────────────────────────────────────────
function Toast({ toasts }) {
  const C = useColors();
  return (
    <div style={{ position: 'fixed', bottom: 100, left: '50%', transform: 'translateX(-50%)', zIndex: 999, display: 'flex', flexDirection: 'column', gap: 8, width: 'max-content', maxWidth: 'calc(100vw - 32px)', pointerEvents: 'none' }}>
      {toasts.map(t => (
        <div key={t.id} style={{
          padding: '12px 18px', borderRadius: 12,
          background: t.type === 'error' ? C.error : C.onSurface,
          color: C.surface, fontSize: 14, fontWeight: 600,
          fontFamily: "'Nunito', sans-serif",
          boxShadow: '0 8px 24px rgba(10,14,22,0.3)',
          animation: 'slideUp 0.2s ease',
          display: 'flex', alignItems: 'center', gap: 8,
        }}>
          <Icon name={t.type === 'error' ? 'alertCircle' : 'check'} size={16} color={C.surface} />
          {t.message}
        </div>
      ))}
    </div>
  );
}
window.Toast = Toast;

// ── ScreenHeader / ActionBtn / helpers ───────────────────────────────────────
function ScreenHeader({ title, subtitle, onAdd, addLabel, isDesktop }) {
  const C = useColors();
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '20px 20px 16px' }}>
      <div>
        <h1 style={{ margin: 0, fontSize: 22, fontWeight: 800, color: C.onSurface, fontFamily: "'Playfair Display', serif" }}>{title}</h1>
        {subtitle && <p style={{ margin: '2px 0 0', fontSize: 13, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif" }}>{subtitle}</p>}
      </div>
      {isDesktop && onAdd && <FABDesktop onClick={onAdd} label={addLabel} />}
    </div>
  );
}
window.ScreenHeader = ScreenHeader;

function ActionBtn({ icon, onClick, title, color }) {
  const C = useColors();
  const c = color || C.onSurfaceVariant;
  const [h, setH] = React.useState(false);
  return (
    <button onClick={onClick} title={title}
      onMouseEnter={() => setH(true)}
      onMouseLeave={() => setH(false)}
      style={{
        width: 34, height: 34, borderRadius: 8, border: 'none',
        background: h ? c + '18' : 'transparent',
        color: h ? c : C.onSurfaceVariant,
        cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
        transition: 'all 0.15s',
      }}>
      <Icon name={icon} size={16} />
    </button>
  );
}
window.ActionBtn = ActionBtn;

function fmtDate(str) {
  if (!str) return '';
  const [y, m, d] = str.split('-');
  return `${d}/${m}/${y}`;
}
window.fmtDate = fmtDate;
