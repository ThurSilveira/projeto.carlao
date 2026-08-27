import React from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';

import { ErrorBoundary } from '@/components/ErrorBoundary';
import { Layout } from '@/components/Layout';
import { Card, Spinner } from '@/components/ui';
import { AuthProvider } from '@/contexts/AuthContext';
import { useAuth } from '@/hooks/useAuth';
import { AuditoriaPage } from '@/pages/Auditoria';
import { Dashboard } from '@/pages/Dashboard';
import { EscalasPage } from '@/pages/Escalas';
import { EventosPage } from '@/pages/Eventos';
import { FeedbackPage } from '@/pages/Feedback';
import { MinistrosPage } from '@/pages/Ministros';
import { LoginPage } from '@/pages/Login';

const MinisterAccessPending: React.FC = () => (
  <Card title="Área do ministro">
    <p className="text-slate-600 dark:text-slate-300">
      Seu acesso está ativo. O calendário e as indisponibilidades serão habilitados na próxima etapa.
    </p>
  </Card>
);

const AppRoutes: React.FC = () => {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-neutral-100 dark:bg-neutral-900">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!user) {
    return (
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    );
  }

  if (user.perfil === 'MINISTRO') {
    return (
      <Layout>
        <Routes>
          <Route path="/meu-acesso" element={<MinisterAccessPending />} />
          <Route path="/login" element={<Navigate to="/meu-acesso" replace />} />
          <Route path="*" element={<Navigate to="/meu-acesso" replace />} />
        </Routes>
      </Layout>
    );
  }

  return (
    <Layout>
      <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/ministros" element={<MinistrosPage />} />
          <Route path="/eventos" element={<EventosPage />} />
          <Route path="/escalas" element={<EscalasPage />} />
          <Route path="/feedback" element={<FeedbackPage />} />
          <Route path="/auditoria" element={<AuditoriaPage />} />
        <Route path="/login" element={<Navigate to="/" replace />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
};

export const App: React.FC = () => (
  <ErrorBoundary>
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  </ErrorBoundary>
);
