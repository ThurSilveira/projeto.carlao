import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import { ErrorBoundary } from '@/components/ErrorBoundary';
import { Layout } from '@/components/Layout';
import { AuditoriaPage } from '@/pages/Auditoria';
import { Dashboard } from '@/pages/Dashboard';
import { EscalasPage } from '@/pages/Escalas';
import { EventosPage } from '@/pages/Eventos';
import { FeedbackPage } from '@/pages/Feedback';
import { MinistrosPage } from '@/pages/Ministros';

export const App: React.FC = () => (
  <ErrorBoundary>
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/ministros" element={<MinistrosPage />} />
          <Route path="/eventos" element={<EventosPage />} />
          <Route path="/escalas" element={<EscalasPage />} />
          <Route path="/feedback" element={<FeedbackPage />} />
          <Route path="/auditoria" element={<AuditoriaPage />} />
          <Route path="*" element={<Dashboard />} />
        </Routes>
      </Layout>
    </Router>
  </ErrorBoundary>
);
