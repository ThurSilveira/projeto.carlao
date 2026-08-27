import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LogOut, Menu, X, Moon, Sun, UserRound } from 'lucide-react';
import clsx from 'clsx';
import { useTheme } from '@/hooks/useTheme';
import { Modal, Button } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { getErrorMessage } from '@/utils/error';

const NOTICE_KEY = 'render_cold_start_notice_seen';

const RenderNotice: React.FC = () => {
  const [open, setOpen] = useState(() => !localStorage.getItem(NOTICE_KEY));
  const confirm = () => { localStorage.setItem(NOTICE_KEY, '1'); setOpen(false); };
  return (
    <Modal
      isOpen={open}
      title="⏳ Aviso sobre o servidor"
      onClose={confirm}
      actions={<Button onClick={confirm}>Entendido</Button>}
    >
      <p className="text-slate-700 dark:text-slate-300 text-sm leading-relaxed">
        O servidor está hospedado no plano gratuito do <strong>Render</strong>, que hiberna
        após 15 minutos de inatividade. Na primeira requisição após o sono, pode levar
        até <strong>50 segundos</strong> para responder — depois disso fica rápido.
      </p>
    </Modal>
  );
};

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [logoutError, setLogoutError] = useState('');
  const [isDarkMode, toggleTheme] = useTheme();
  const { user, logout } = useAuth();
  const location = useLocation();

  const navItems = user?.perfil === 'MINISTRO'
    ? [
        { label: 'Meu calendário', href: '/meu-calendario', icon: '📅' },
        { label: 'Indisponibilidades', href: '/minhas-indisponibilidades', icon: '🚫' },
        { label: 'Feedback', href: '/meus-feedbacks', icon: '💬' },
      ]
    : [
        { label: 'Dashboard', href: '/', icon: '📊' },
        { label: 'Ministros', href: '/ministros', icon: '👥' },
        { label: 'Eventos', href: '/eventos', icon: '📅' },
        { label: 'Escalas', href: '/escalas', icon: '📋' },
        { label: 'Feedback', href: '/feedback', icon: '💬' },
        { label: 'Auditoria', href: '/auditoria', icon: '📝' },
      ].filter((item) => item.href !== '/auditoria' || user?.perfil !== 'CONSULTA');

  const isActive = (href: string) => location.pathname === href;

  const handleLogout = async () => {
    setLogoutError('');
    try {
      await logout();
    } catch (error) {
      setLogoutError(getErrorMessage(error, 'Não foi possível encerrar a sessão.'));
    }
  };

  return (
    <div className="min-h-screen bg-neutral-100 dark:bg-neutral-900 transition-colors duration-300">
      <RenderNotice />
      {/* Header */}
      <header className="bg-white dark:bg-neutral-850 shadow-xs border-b border-slate-200 dark:border-neutral-700 sticky top-0 z-40 transition-colors duration-300">
        <div className="px-4 py-4 flex items-center justify-between">
          <a href="#skip-to-main" className="skip-to-content">
            Ir para conteúdo principal
          </a>

          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 group">
            <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center text-white font-bold group-hover:bg-primary-700 transition-colors">
              ⛪
            </div>
            <span className="font-bold font-serif text-slate-900 dark:text-white hidden sm:inline transition-colors duration-300">
              Escala Ministerial
            </span>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex gap-1">
            {navItems.map((item) => (
              <Link
                key={item.href}
                to={item.href}
                className={clsx(
                  'px-3 py-2 rounded-lg text-sm font-semibold transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-600',
                  isActive(item.href)
                    ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/60 dark:text-primary-100'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white hover:bg-neutral-200 dark:hover:bg-neutral-700',
                )}
                aria-current={isActive(item.href) ? 'page' : undefined}
              >
                <span className="mr-1">{item.icon}</span>
                {item.label}
              </Link>
            ))}
          </nav>

          {/* Right Actions */}
          <div className="flex items-center gap-2">
            <div className="hidden lg:flex items-center gap-2 px-2 text-left" title={user?.email}>
              <span className="size-8 rounded-full bg-primary-100 dark:bg-primary-900/60 text-primary-700 dark:text-primary-100 flex items-center justify-center">
                <UserRound size={17} />
              </span>
              <span className="max-w-32">
                <span className="block text-xs font-semibold text-slate-800 dark:text-white truncate">{user?.nome}</span>
                <span className="block text-[11px] text-slate-500 dark:text-slate-400">{user?.perfil}</span>
              </span>
            </div>
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg hover:bg-neutral-200 dark:hover:bg-neutral-700 transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-600"
              aria-label={isDarkMode ? 'Ativar modo claro' : 'Ativar modo escuro'}
            >
              {isDarkMode
                ? <Sun size={20} className="text-amber-400" />
                : <Moon size={20} className="text-slate-600" />}
            </button>

            <button
              onClick={() => void handleLogout()}
              className="p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-950/40 text-slate-600 dark:text-slate-300 hover:text-red-700 dark:hover:text-red-300 transition-colors"
              aria-label="Encerrar sessão"
              title="Encerrar sessão"
            >
              <LogOut size={20} />
            </button>

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsSidebarOpen(!isSidebarOpen)}
              className="md:hidden p-2 rounded-lg hover:bg-neutral-200 dark:hover:bg-neutral-700 transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-600"
              aria-label={isSidebarOpen ? 'Fechar menu' : 'Abrir menu'}
              aria-expanded={isSidebarOpen}
            >
              {isSidebarOpen
                ? <X size={20} className="text-slate-700 dark:text-slate-300" />
                : <Menu size={20} className="text-slate-700 dark:text-slate-300" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isSidebarOpen && (
          <nav className="md:hidden border-t border-slate-200 dark:border-neutral-700 bg-white dark:bg-neutral-850 py-2 transition-colors duration-300">
            {navItems.map((item) => (
              <Link
                key={item.href}
                to={item.href}
                onClick={() => setIsSidebarOpen(false)}
                className={clsx(
                  'block px-4 py-2 text-sm font-semibold transition-colors',
                  isActive(item.href)
                    ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/60 dark:text-primary-100'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white dark:hover:bg-neutral-700',
                )}
                aria-current={isActive(item.href) ? 'page' : undefined}
              >
                <span className="mr-2">{item.icon}</span>
                {item.label}
              </Link>
            ))}
          </nav>
        )}
      </header>

      {logoutError && (
        <div className="container mx-auto px-4 pt-4 text-sm text-red-700 dark:text-red-300" role="alert">
          {logoutError}
        </div>
      )}

      {/* Main Content */}
      <main id="skip-to-main" className="container mx-auto px-4 py-8">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-white dark:bg-neutral-850 border-t border-slate-200 dark:border-neutral-700 py-6 mt-12 transition-colors duration-300">
        <div className="container mx-auto px-4 text-center text-sm text-slate-600 dark:text-slate-400">
          <p>© Escala Ministerial — TIC Uni-FACEF</p>
        </div>
      </footer>
    </div>
  );
};
