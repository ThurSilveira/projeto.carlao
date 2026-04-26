import React from 'react';
import clsx from 'clsx';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  children: React.ReactNode;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant = 'primary', size = 'md', isLoading = false, className, children, ...props }, ref) => {
    const baseStyles = 'font-medium rounded-lg transition-colors duration-200 focus-visible:outline-2 focus-visible:outline-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

    const variantStyles = {
      primary: 'bg-primary-600 text-white hover:bg-primary-700 focus-visible:outline-primary-500',
      secondary: 'bg-slate-200 text-slate-900 hover:bg-slate-300 focus-visible:outline-slate-500',
      danger: 'bg-red-600 text-white hover:bg-red-700 focus-visible:outline-red-500',
    };

    const sizeStyles = {
      sm: 'px-3 py-1 text-sm',
      md: 'px-4 py-2 text-base',
      lg: 'px-6 py-3 text-lg',
    };

    return (
      <button
        ref={ref}
        className={clsx(baseStyles, variantStyles[variant], sizeStyles[size], className)}
        disabled={isLoading || props.disabled}
        aria-busy={isLoading}
        {...props}
      >
        {isLoading ? 'Carregando...' : children}
      </button>
    );
  },
);

Button.displayName = 'Button';

interface CardProps {
  className?: string;
  children: React.ReactNode;
  title?: string;
}

export const Card: React.FC<CardProps> = ({ className, children, title }) => (
  <div className={clsx('bg-white dark:bg-neutral-850 rounded-xl shadow-sm border border-slate-200 dark:border-neutral-700 p-6', className)}>
    {title && <h3 className="text-lg font-semibold mb-4 text-slate-900 dark:text-white">{title}</h3>}
    {children}
  </div>
);

// Badge Component — pill with colored dot
interface BadgeProps {
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  children: React.ReactNode;
  className?: string;
}

const badgeVariantStyles: Record<NonNullable<BadgeProps['variant']>, { pill: string; dot: string }> = {
  primary:   { pill: 'bg-primary-100 text-primary-600 dark:bg-primary-900/60 dark:text-primary-200',       dot: 'bg-primary-600' },
  secondary: { pill: 'bg-secondary-100 text-secondary-600 dark:bg-secondary-800 dark:text-secondary-300',  dot: 'bg-secondary-600' },
  success:   { pill: 'bg-success-100 text-success-600 dark:bg-green-900/40 dark:text-green-300',           dot: 'bg-success-600' },
  warning:   { pill: 'bg-amber-100 text-amber-600 dark:bg-amber-900/40 dark:text-amber-300',               dot: 'bg-amber-600' },
  danger:    { pill: 'bg-red-100 text-red-600 dark:bg-red-900/40 dark:text-red-300',                       dot: 'bg-red-600' },
  info:      { pill: 'bg-info-100 text-info-600 dark:bg-blue-900/40 dark:text-blue-300',                   dot: 'bg-info-600' },
  neutral:   { pill: 'bg-neutral-200 text-neutral-600 dark:bg-neutral-700 dark:text-neutral-300',          dot: 'bg-neutral-500' },
};

export const Badge: React.FC<BadgeProps> = ({ variant = 'primary', children, className }) => {
  const { pill, dot } = badgeVariantStyles[variant];
  return (
    <span className={clsx('inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold', pill, className)}>
      <span className={clsx('w-1.5 h-1.5 rounded-full flex-shrink-0', dot)} />
      {children}
    </span>
  );
};

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helperText, className, id, ...props }, ref) => {
    const inputId = id || `input-${Math.random()}`;

    return (
      <div className="flex flex-col gap-1">
        {label && (
          <label htmlFor={inputId} className="text-sm font-medium text-slate-700">
            {label}
          </label>
        )}
        <input
          ref={ref}
          id={inputId}
          className={clsx(
            'px-3 py-2 border rounded-lg focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500 disabled:bg-slate-100 disabled:cursor-not-allowed transition-colors',
            error ? 'border-red-500' : 'border-slate-300',
            className,
          )}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined}
          {...props}
        />
        {error && (
          <span id={`${inputId}-error`} className="text-sm text-red-600">
            {error}
          </span>
        )}
        {helperText && !error && (
          <span id={`${inputId}-helper`} className="text-sm text-slate-500">
            {helperText}
          </span>
        )}
      </div>
    );
  },
);

Input.displayName = 'Input';

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options: Array<{ value: string; label: string }>;
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, className, id, ...props }, ref) => {
    const selectId = id || `select-${Math.random()}`;

    return (
      <div className="flex flex-col gap-1">
        {label && (
          <label htmlFor={selectId} className="text-sm font-medium text-slate-700">
            {label}
          </label>
        )}
        <select
          ref={ref}
          id={selectId}
          className={clsx(
            'px-3 py-2 border rounded-lg focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500 disabled:bg-slate-100 disabled:cursor-not-allowed transition-colors',
            error ? 'border-red-500' : 'border-slate-300',
            className,
          )}
          aria-invalid={!!error}
          {...props}
        >
          <option value="">Selecionar...</option>
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        {error && <span className="text-sm text-red-600">{error}</span>}
      </div>
    );
  },
);

Select.displayName = 'Select';

export const Spinner: React.FC<{ size?: 'sm' | 'md' | 'lg' }> = ({ size = 'md' }) => {
  const sizes = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
  };

  return (
    <div
      className={clsx('animate-spin rounded-full border-4 border-slate-200 border-t-primary-600', sizes[size])}
      role="status"
      aria-label="Carregando..."
    />
  );
};

interface AlertProps {
  variant?: 'info' | 'success' | 'warning' | 'error';
  title?: string;
  children: React.ReactNode;
  onClose?: () => void;
}

export const Alert: React.FC<AlertProps> = ({ variant = 'info', title, children, onClose }) => {
  const variantStyles = {
    info: 'bg-blue-50 border-blue-200 text-blue-800',
    success: 'bg-green-50 border-green-200 text-green-800',
    warning: 'bg-yellow-50 border-yellow-200 text-yellow-800',
    error: 'bg-red-50 border-red-200 text-red-800',
  };

  return (
    <div className={clsx('border rounded-lg p-4 flex justify-between items-start gap-4', variantStyles[variant])} role="alert">
      <div>
        {title && <h4 className="font-semibold mb-1">{title}</h4>}
        <p className="text-sm">{children}</p>
      </div>
      {onClose && (
        <button
          onClick={onClose}
          className="text-sm font-semibold hover:underline"
          aria-label="Fechar alerta"
        >
          ✕
        </button>
      )}
    </div>
  );
};

interface ModalProps {
  isOpen: boolean;
  title: string;
  children: React.ReactNode;
  onClose: () => void;
  actions?: React.ReactNode;
}

export const Modal: React.FC<ModalProps> = ({ isOpen, title, children, onClose, actions }) => {
  if (!isOpen) return null;

  return (
    <>
      <div
        className="fixed inset-0 bg-black bg-opacity-50 z-40"
        onClick={onClose}
        role="presentation"
      />
      <div
        className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white dark:bg-slate-800 rounded-lg shadow-xl z-50 p-6 max-w-md w-11/12 max-h-[80vh] overflow-y-auto transition-colors"
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
      >
        <h2 id="modal-title" className="text-xl font-bold mb-4 text-slate-900 dark:text-white">
          {title}
        </h2>
        <div className="mb-6">{children}</div>
        {actions && <div className="flex gap-2 justify-end">{actions}</div>}
      </div>
    </>
  );
};

interface TabsProps {
  tabs: Array<{ label: string; id: string; content: React.ReactNode }>;
  defaultTabId?: string;
}

export const Tabs: React.FC<TabsProps> = ({ tabs, defaultTabId }) => {
  const [activeTab, setActiveTab] = React.useState(defaultTabId || tabs[0]?.id);

  return (
    <div>
      <div className="flex gap-2 border-b border-slate-200" role="tablist">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={clsx(
              'px-4 py-2 font-medium border-b-2 transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500',
              activeTab === tab.id
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-slate-600 hover:text-slate-900',
            )}
            role="tab"
            aria-selected={activeTab === tab.id}
            aria-controls={`panel-${tab.id}`}
          >
            {tab.label}
          </button>
        ))}
      </div>
      {tabs.map((tab) => (
        <div
          key={tab.id}
          id={`panel-${tab.id}`}
          className={activeTab === tab.id ? 'block' : 'hidden'}
          role="tabpanel"
          aria-labelledby={`tab-${tab.id}`}
        >
          {tab.content}
        </div>
      ))}
    </div>
  );
};
