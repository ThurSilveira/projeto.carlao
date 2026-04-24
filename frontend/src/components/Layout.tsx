import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Menu, X, Moon, Sun } from 'lucide-react';
import clsx from 'clsx';
import { useTheme } from '@/hooks/useTheme';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isDarkMode, toggleTheme] = useTheme();
  const location = useLocation();

  const navItems = [
    { label: 'Dashboard', href: '/', icon: '📊' },
    { label: 'Ministros', href: '/ministros', icon: '👥' },
    { label: 'Eventos', href: '/eventos', icon: '📅' },
    { label: 'Escalas', href: '/escalas', icon: '📋' },
    { label: 'Feedback', href: '/feedback', icon: '💬' },
    { label: 'Auditoria', href: '/auditoria', icon: '📝' },
  ];

  const isActive = (href: string) => location.pathname === href;

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 transition-colors duration-300">
      {/* Header */}
      <header className="bg-white dark:bg-slate-800 shadow-sm border-b border-slate-200 dark:border-slate-700 sticky top-0 z-40 transition-colors duration-300">
        <div className="px-4 py-4 flex items-center justify-between">
          <a href="#skip-to-main" className="skip-to-content">
            Ir para conteúdo principal
          </a>

          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 group">
            <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center text-white font-bold group-hover:bg-primary-700 transition-colors">
              ⛪
            </div>
            <span className="font-bold text-slate-900 dark:text-white hidden sm:inline transition-colors duration-300">
              Gestão de Escalas
            </span>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex gap-1">
            {navItems.map((item) => (
              <Link
                key={item.href}
                to={item.href}
                className={clsx(
                  'px-3 py-2 rounded-lg text-sm font-medium transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500',
                  isActive(item.href)
                    ? 'bg-primary-100 text-primary-700 dark:bg-primary-900 dark:text-primary-100'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700',
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
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
              aria-label={isDarkMode ? 'Ativar modo claro' : 'Ativar modo escuro'}
            >
              {isDarkMode
                ? <Sun size={20} className="text-yellow-400" />
                : <Moon size={20} className="text-slate-600" />}
            </button>

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsSidebarOpen(!isSidebarOpen)}
              className="md:hidden p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
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
          <nav className="md:hidden border-t border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 py-2 transition-colors duration-300">
            {navItems.map((item) => (
              <Link
                key={item.href}
                to={item.href}
                onClick={() => setIsSidebarOpen(false)}
                className={clsx(
                  'block px-4 py-2 text-sm font-medium transition-colors',
                  isActive(item.href)
                    ? 'bg-primary-50 text-primary-700 dark:bg-primary-900 dark:text-primary-100'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white dark:hover:bg-slate-700',
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

      {/* Main Content */}
      <main id="skip-to-main" className="container mx-auto px-4 py-8">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-white dark:bg-slate-800 border-t border-slate-200 dark:border-slate-700 py-6 mt-12 transition-colors duration-300">
        <div className="container mx-auto px-4 text-center text-sm text-slate-600 dark:text-slate-400">
          <p>© 2026 Sistema de Gestão de Escalas - Paróquia São José</p>
          <p className="mt-2">Desenvolvido com React + TypeScript + Tailwind CSS</p>
        </div>
      </footer>
    </div>
  );
};
