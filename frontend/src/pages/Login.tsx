import React, { FormEvent, useState } from 'react';
import { Eye, EyeOff, LockKeyhole, ShieldCheck } from 'lucide-react';

import { Alert, Button } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { getErrorMessage } from '@/utils/error';


export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      await login(email, password);
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'Não foi possível entrar. Tente novamente.'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="min-h-screen bg-neutral-100 dark:bg-neutral-900 flex items-center justify-center p-4 sm:p-8">
      <div className="w-full max-w-5xl overflow-hidden rounded-2xl bg-white dark:bg-neutral-850 border border-slate-200 dark:border-neutral-700 shadow-xl grid lg:grid-cols-[1.05fr_1fr]">
        <section className="hidden lg:flex flex-col justify-between bg-primary-700 text-white p-12 min-h-[620px] relative overflow-hidden">
          <div className="absolute -right-24 -top-24 size-80 rounded-full border border-white/10" />
          <div className="absolute -right-8 -top-8 size-48 rounded-full border border-white/10" />
          <div className="relative">
            <div className="size-12 rounded-xl bg-white/10 border border-white/15 flex items-center justify-center text-2xl mb-8">⛪</div>
            <p className="text-primary-100 text-sm font-bold tracking-[0.2em] uppercase mb-4">Área restrita</p>
            <h1 className="text-4xl leading-tight font-bold max-w-md">
              Gestão responsável das escalas ministeriais.
            </h1>
            <p className="mt-6 max-w-md text-primary-100 leading-relaxed">
              Um ambiente reservado para organizar ministros, celebrações e atividades da comunidade com clareza e cuidado.
            </p>
          </div>
          <div className="relative flex items-center gap-3 text-sm text-primary-100">
            <ShieldCheck size={22} aria-hidden="true" />
            <span>Sessão protegida e acesso restrito a usuários autorizados.</span>
          </div>
        </section>

        <section className="px-6 py-10 sm:px-12 lg:px-14 lg:py-16 flex flex-col justify-center">
          <div className="lg:hidden size-11 rounded-xl bg-primary-600 text-white flex items-center justify-center text-xl mb-8">⛪</div>
          <div className="mb-8">
            <p className="text-primary-600 dark:text-primary-200 text-sm font-bold uppercase tracking-wider mb-2">Escala Ministerial</p>
            <h2 className="text-3xl font-bold text-slate-900 dark:text-white">Acesse sua conta</h2>
            <p className="mt-3 text-slate-600 dark:text-slate-300">Informe suas credenciais para continuar.</p>
          </div>

          {error && <Alert variant="error">{error}</Alert>}

          <form onSubmit={submit} className="mt-6 space-y-5">
            <div>
              <label htmlFor="login-email" className="block text-sm font-semibold text-slate-700 dark:text-slate-200 mb-2">E-mail</label>
              <input
                id="login-email"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                autoComplete="username"
                required
                maxLength={254}
                className="w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 px-4 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
              />
            </div>

            <div>
              <label htmlFor="login-password" className="block text-sm font-semibold text-slate-700 dark:text-slate-200 mb-2">Senha</label>
              <div className="relative">
                <LockKeyhole className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} aria-hidden="true" />
                <input
                  id="login-password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  autoComplete="current-password"
                  required
                  maxLength={128}
                  className="w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 pl-11 pr-12 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((visible) => !visible)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-slate-500 dark:text-slate-300 rounded focus-visible:outline-2 focus-visible:outline-primary-500"
                  aria-label={showPassword ? 'Ocultar senha' : 'Exibir senha'}
                >
                  {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
                </button>
              </div>
            </div>

            <Button type="submit" size="lg" isLoading={isSubmitting} className="w-full justify-center mt-2">
              Entrar
            </Button>
          </form>
        </section>
      </div>
    </main>
  );
};
