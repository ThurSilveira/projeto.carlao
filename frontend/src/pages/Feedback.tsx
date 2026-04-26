import React, { useState, useEffect } from 'react';
import { FeedbackService } from '@/services/api';
import { Card, Badge, Spinner, Button, Modal, Select, Alert } from '@/components/ui';
import { Feedback, StatusFeedback } from '@/types';
import { Send } from 'lucide-react';
import { formatDate } from '@/utils/date';

export const FeedbackPage: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState(true);
  const [isReplyModalOpen, setIsReplyModalOpen] = useState(false);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertVariant, setAlertVariant] = useState<'success' | 'error'>('success');
  const [selectedFeedback, setSelectedFeedback] = useState<Feedback | null>(null);
  const [reply, setReply] = useState('');
  const [filterStatus, setFilterStatus] = useState('');

  useEffect(() => { loadFeedbacks(); }, []);

  const loadFeedbacks = async () => {
    try {
      setLoading(true);
      setFeedbacks(await FeedbackService.getAllFeedbacks());
    } catch {
      showAlert('Erro ao carregar feedbacks', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showAlert = (msg: string, variant: 'success' | 'error') => {
    setAlertMessage(msg);
    setAlertVariant(variant);
  };

  const handleReply = async () => {
    if (!reply.trim() || selectedFeedback?.id == null) return;
    try {
      await FeedbackService.answerFeedback(selectedFeedback.id, reply);
      showAlert('Resposta enviada com sucesso!', 'success');
      await loadFeedbacks();
      setIsReplyModalOpen(false);
      setReply('');
      setSelectedFeedback(null);
    } catch {
      showAlert('Erro ao enviar resposta', 'error');
    }
  };

  const getStatusVariant = (status: StatusFeedback): 'primary' | 'success' | 'warning' | 'danger' => {
    switch (status) {
      case StatusFeedback.PENDENTE:    return 'warning';
      case StatusFeedback.RESPONDIDO:  return 'success';
      case StatusFeedback.ARQUIVADO:   return 'danger';
      default:                         return 'primary';
    }
  };

  const filteredFeedbacks = feedbacks.filter((f) => !filterStatus || f.status === filterStatus);
  const averageRating = feedbacks.length > 0
    ? (feedbacks.reduce((sum, f) => sum + f.nota, 0) / feedbacks.length).toFixed(1)
    : '0';

  if (loading && feedbacks.length === 0) {
    return <div className="flex items-center justify-center h-96"><Spinner size="lg" /></div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">💬 Feedbacks</h1>
        <p className="text-slate-600 dark:text-slate-400 mt-1">Gerencie feedback dos ministros sobre os eventos</p>
      </div>

      {alertMessage && (
        <Alert variant={alertVariant} onClose={() => setAlertMessage('')}>{alertMessage}</Alert>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card><div className="text-center">
          <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">Total</p>
          <p className="text-3xl font-bold text-slate-900 dark:text-white">{feedbacks.length}</p>
        </div></Card>
        <Card><div className="text-center">
          <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">Satisfação Média</p>
          <p className="text-3xl font-bold text-slate-900 dark:text-white">⭐ {averageRating}</p>
        </div></Card>
        <Card><div className="text-center">
          <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">Pendentes</p>
          <p className="text-3xl font-bold text-slate-900 dark:text-white">
            {feedbacks.filter((f) => f.status === StatusFeedback.PENDENTE).length}
          </p>
        </div></Card>
      </div>

      <Card>
        <Select
          label="Filtrar por Status"
          options={[
            { value: '', label: 'Todos' },
            ...Object.values(StatusFeedback).map((s) => ({ value: s, label: s })),
          ]}
          onChange={(e) => setFilterStatus(e.target.value)}
        />
      </Card>

      <div className="space-y-4">
        {filteredFeedbacks.length > 0 ? (
          filteredFeedbacks.map((feedback) => (
            <Card key={feedback.id}>
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-2">
                    <span className="text-2xl">⭐ {feedback.nota}</span>
                    <Badge variant={getStatusVariant(feedback.status)}>{feedback.status}</Badge>
                  </div>
                  <p className="font-semibold text-slate-900 dark:text-white">{feedback.comentario}</p>
                  <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">
                    📅 {formatDate(feedback.dataEnvio)}
                  </p>
                </div>
                {feedback.status === StatusFeedback.PENDENTE && (
                  <Button size="sm" onClick={() => { setSelectedFeedback(feedback); setIsReplyModalOpen(true); }}>
                    <Send size={16} className="mr-1" /> Responder
                  </Button>
                )}
              </div>
              {feedback.resposta && (
                <div className="mt-4 pt-4 border-t border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-700/50 p-3 rounded-lg">
                  <p className="font-semibold text-sm text-slate-700 dark:text-slate-300 mb-1">Resposta:</p>
                  <p className="text-sm text-slate-600 dark:text-slate-400">{feedback.resposta}</p>
                </div>
              )}
            </Card>
          ))
        ) : (
          <div className="text-center py-12">
            <p className="text-slate-600 dark:text-slate-400 text-lg">Nenhum feedback encontrado</p>
          </div>
        )}
      </div>

      <Modal
        isOpen={isReplyModalOpen}
        title="Responder Feedback"
        onClose={() => { setIsReplyModalOpen(false); setSelectedFeedback(null); setReply(''); }}
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => { setIsReplyModalOpen(false); setReply(''); }}>Cancelar</Button>
            <Button onClick={handleReply}>Enviar</Button>
          </div>
        }
      >
        {selectedFeedback && (
          <div className="space-y-4">
            <div className="p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
              <p className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1">Feedback Original:</p>
              <p className="text-sm text-slate-700 dark:text-slate-300">{selectedFeedback.comentario}</p>
            </div>
            <textarea
              value={reply}
              onChange={(e) => setReply(e.target.value)}
              placeholder="Digite sua resposta..."
              rows={4}
              className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500 transition-colors"
            />
          </div>
        )}
      </Modal>
    </div>
  );
};
