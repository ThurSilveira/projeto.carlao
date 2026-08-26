import React from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';

import { ErrorBoundary } from '@/components/ErrorBoundary';
import { Layout } from '@/components/Layout';
import { Spinner } from '@/components/ui';
import { AuthProvider } from '@/contexts/AuthContext';
import { useAuth } from '@/hooks/useAuth';
import { AuditoriaPage } from '@/pages/Auditoria';
import { Dashboard } from '@/pages/Dashboard';
import { EscalasPage } from '@/pages/Escalas';
import { EventosPage } from '@/pages/Eventos';
import { FeedbackPage } from '@/pages/Feedback';
import { MinistrosPage } from '@/pages/Ministros';
import { LoginPage } from '@/pages/Login';
import {
  CalendarioMinistroPage,
  FeedbackMinistroPage,
  IndisponibilidadesMinistroPage,
} from '@/pages/PortalMinistro';

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
          <Route path="/meu-calendario" element={<CalendarioMinistroPage />} />
          <Route path="/minhas-indisponibilidades" element={<IndisponibilidadesMinistroPage />} />
          <Route path="/meus-feedbacks" element={<FeedbackMinistroPage />} />
          <Route path="/login" element={<Navigate to="/meu-calendario" replace />} />
          <Route path="*" element={<Navigate to="/meu-calendario" replace />} />
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
