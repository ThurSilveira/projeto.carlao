import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { AuthContext, type AuthContextValue } from '@/contexts/auth-context';
import { AUTH_UNAUTHORIZED_EVENT, AuthService, setCsrfToken } from '@/services/api';
import type { AuthSession } from '@/types';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const clearSession = useCallback(() => {
    setCsrfToken(null);
    setSession(null);
  }, []);

  useEffect(() => {
    let active = true;
    AuthService.me()
      .then((currentSession) => {
        if (active) setSession(currentSession);
      })
      .catch(() => {
        if (active) clearSession();
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });
    return () => { active = false; };
  }, [clearSession]);

  useEffect(() => {
    window.addEventListener(AUTH_UNAUTHORIZED_EVENT, clearSession);
    return () => window.removeEventListener(AUTH_UNAUTHORIZED_EVENT, clearSession);
  }, [clearSession]);

  const login = useCallback(async (email: string, password: string) => {
    setSession(await AuthService.login(email, password));
  }, []);

  const logout = useCallback(async () => {
    await AuthService.logout();
    clearSession();
  }, [clearSession]);

  const changePassword = useCallback(async (currentPassword: string, newPassword: string) => {
    setSession(await AuthService.changePassword(currentPassword, newPassword));
  }, []);

  const value = useMemo<AuthContextValue>(() => ({
    user: session?.usuario ?? null,
    isLoading,
    login,
    logout,
    changePassword,
  }), [session, isLoading, login, logout, changePassword]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
