import React, { FormEvent, useState } from 'react';
import { KeyRound, LogOut, ShieldCheck } from 'lucide-react';

import { Alert, Button } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { getErrorMessage } from '@/utils/error';


export const TrocaSenhaObrigatoriaPage: React.FC = () => {
  const { changePassword, logout, user } = useAuth();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmation, setConfirmation] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');

    if (newPassword.length < 12) {
      setError('A nova senha deve conter pelo menos 12 caracteres.');
      return;
    }
    if (newPassword !== confirmation) {
      setError('A confirmação não corresponde à nova senha.');
      return;
    }

    setIsSubmitting(true);
    try {
      await changePassword(currentPassword, newPassword);
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'Não foi possível alterar a senha.'));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLogout = async () => {
    setError('');
    try {
      await logout();
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'Não foi possível encerrar a sessão.'));
    }
  };

  return (
    <main className="min-h-screen bg-neutral-100 dark:bg-neutral-900 flex items-center justify-center p-4 sm:p-8">
      <section className="w-full max-w-xl rounded-2xl bg-white dark:bg-neutral-850 border border-slate-200 dark:border-neutral-700 shadow-xl px-6 py-10 sm:px-12">
        <div className="size-12 rounded-xl bg-primary-100 dark:bg-primary-900/60 text-primary-700 dark:text-primary-200 flex items-center justify-center mb-7">
          <KeyRound size={25} aria-hidden="true" />
        </div>

        <p className="text-primary-600 dark:text-primary-200 text-sm font-bold uppercase tracking-wider mb-2">
          Primeiro acesso
        </p>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">
          Crie sua senha definitiva
        </h1>
        <p className="mt-3 text-slate-600 dark:text-slate-300 leading-relaxed">
          Olá, {user?.nome}. Para proteger sua conta, altere a senha temporária antes de usar o sistema.
        </p>

        <div className="mt-6 flex items-start gap-3 rounded-lg border border-blue-200 bg-blue-50 p-4 text-blue-800">
          <ShieldCheck size={20} className="mt-0.5 shrink-0" aria-hidden="true" />
          <p className="text-sm">A nova senha deve ter pelo menos 12 caracteres e ser diferente da senha temporária.</p>
        </div>

        {error && <div className="mt-6"><Alert variant="error">{error}</Alert></div>}

        <form onSubmit={submit} className="mt-7 space-y-5">
          <label htmlFor="current-password" className="block text-sm font-semibold text-slate-700 dark:text-slate-200">
            Senha temporária
            <input
              id="current-password"
              type="password"
              value={currentPassword}
              onChange={(event) => setCurrentPassword(event.target.value)}
              autoComplete="current-password"
              required
              maxLength={128}
              className="mt-2 w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 px-4 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
            />
          </label>

          <label htmlFor="new-password" className="block text-sm font-semibold text-slate-700 dark:text-slate-200">
            Nova senha
            <input
              id="new-password"
              type="password"
              value={newPassword}
              onChange={(event) => setNewPassword(event.target.value)}
              autoComplete="new-password"
              required
              minLength={12}
              maxLength={128}
              className="mt-2 w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 px-4 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
            />
          </label>

          <label htmlFor="password-confirmation" className="block text-sm font-semibold text-slate-700 dark:text-slate-200">
            Confirme a nova senha
            <input
              id="password-confirmation"
              type="password"
              value={confirmation}
              onChange={(event) => setConfirmation(event.target.value)}
              autoComplete="new-password"
              required
              minLength={12}
              maxLength={128}
              className="mt-2 w-full rounded-lg border border-slate-300 dark:border-neutral-600 bg-white dark:bg-neutral-800 px-4 py-3 text-slate-900 dark:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500"
            />
          </label>

          <Button type="submit" size="lg" isLoading={isSubmitting} className="w-full justify-center">
            Salvar nova senha e continuar
          </Button>
        </form>

        <button
          type="button"
          onClick={() => void handleLogout()}
          className="mt-5 mx-auto flex items-center gap-2 text-sm font-semibold text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500 rounded"
        >
          <LogOut size={17} aria-hidden="true" />
          Sair e alterar depois
        </button>
      </section>
    </main>
  );
};
