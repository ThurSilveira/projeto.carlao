import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import './index.css';
import { Layout } from '@/components/Layout';
import { ErrorBoundary } from '@/components/ErrorBoundary';

import { Dashboard } from '@/pages/Dashboard';
import { MinistrosPage } from '@/pages/Ministros';
import { EventosPage } from '@/pages/Eventos';
import { EscalasPage } from '@/pages/Escalas';
import { FeedbackPage } from '@/pages/Feedback';
import { AuditoriaPage } from '@/pages/Auditoria';

const App: React.FC = () => (
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

ReactDOM.createRoot(document.getElementById('root')!).render(<App />);
