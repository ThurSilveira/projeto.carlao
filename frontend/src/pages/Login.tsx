import React, { FormEvent, useState } from 'react';

import { Alert, Button } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { getErrorMessage } from '@/utils/error';


export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
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
    <main className="min-h-screen bg-neutral-100 dark:bg-neutral-900 flex items-center justify-center p-4">
      <section className="w-full max-w-md rounded-2xl bg-white dark:bg-neutral-850 border border-slate-200 dark:border-neutral-700 shadow-xl p-8">
        <div className="size-12 rounded-xl bg-primary-600 text-white flex items-center justify-center text-2xl mb-7">⛪</div>
        <p className="text-primary-600 dark:text-primary-200 text-sm font-bold uppercase tracking-wider mb-2">Escala Ministerial</p>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Acesse sua conta</h1>
        <p className="mt-3 text-slate-600 dark:text-slate-300">Informe suas credenciais para continuar.</p>

        {error && <div className="mt-6"><Alert variant="error">{error}</Alert></div>}

        <form onSubmit={submit} className="mt-7 space-y-5">
          <label htmlFor="login-email" className="block text-sm font-semibold text-slate-700 dark:text-slate-200">
            E-mail
            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              autoComplete="username"
              required
              maxLength={254}
              className="mt-2 w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 px-4 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
            />
          </label>

          <label htmlFor="login-password" className="block text-sm font-semibold text-slate-700 dark:text-slate-200">
            Senha
            <input
              id="login-password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
              required
              maxLength={128}
              className="mt-2 w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 px-4 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
            />
          </label>

          <Button type="submit" size="lg" isLoading={isSubmitting} className="w-full justify-center">
            Entrar
          </Button>
        </form>
      </section>
    </main>
  );
};
