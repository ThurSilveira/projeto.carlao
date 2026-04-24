import React, { useState, useEffect } from 'react';
import { EscalaService, EventoService } from '@/services/api';
import { Card, Badge, Spinner, Button, Modal, Select, Input, Alert } from '@/components/ui';
import { Escala, Evento, StatusEscala } from '@/types';
import { Plus, CheckCircle, XCircle } from 'lucide-react';

export const EscalasPage: React.FC = () => {
  const [escalas, setEscalas] = useState<Escala[]>([]);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertVariant, setAlertVariant] = useState<'success' | 'error'>('success');
  const [filterStatus, setFilterStatus] = useState('');
  const [formData, setFormData] = useState<Partial<Escala>>({
    eventoId: 0,
    observacao: '',
    status: StatusEscala.PROPOSTA,
    escalaMinistros: [],
  });

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [escalasData, eventosData] = await Promise.all([
        EscalaService.getAllEscalas(),
        EventoService.getAllEventos(),
      ]);
      setEscalas(escalasData);
      setEventos(eventosData);
    } catch {
      showAlert('Erro ao carregar escalas', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showAlert = (msg: string, variant: 'success' | 'error') => {
    setAlertMessage(msg);
    setAlertVariant(variant);
  };

  const handleSave = async () => {
    if (!formData.eventoId) {
      showAlert('Selecione um evento', 'error');
      return;
    }
    try {
      await EscalaService.createEscala(formData);
      showAlert('Escala criada com sucesso!', 'success');
      await loadData();
      setIsModalOpen(false);
      setFormData({ eventoId: 0, observacao: '', status: StatusEscala.PROPOSTA, escalaMinistros: [] });
    } catch {
      showAlert('Erro ao salvar escala', 'error');
    }
  };

  const handleApprove = async (id: number) => {
    try {
      await EscalaService.approveEscala(id);
      showAlert('Escala aprovada com sucesso!', 'success');
      await loadData();
    } catch {
      showAlert('Erro ao aprovar escala', 'error');
    }
  };

  const handleCancel = async (id: number) => {
    if (!confirm('Tem certeza que deseja cancelar esta escala?')) return;
    try {
      await EscalaService.cancelEscala(id);
      showAlert('Escala cancelada com sucesso!', 'success');
      await loadData();
    } catch {
      showAlert('Erro ao cancelar escala', 'error');
    }
  };

  const getStatusColor = (status: StatusEscala): 'primary' | 'success' | 'warning' | 'danger' => {
    switch (status) {
      case StatusEscala.PROPOSTA:    return 'warning';
      case StatusEscala.APROVADA:    return 'success';
      case StatusEscala.CONFIRMADA:  return 'primary';
      case StatusEscala.CANCELADA:   return 'danger';
      default:                       return 'primary';
    }
  };

  const filteredEscalas = escalas.filter((e) => !filterStatus || e.status === filterStatus);

  if (loading && escalas.length === 0) {
    return <div className="flex items-center justify-center h-96"><Spinner size="lg" /></div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 dark:text-white">📋 Escalas</h1>
          <p className="text-slate-600 dark:text-slate-400 mt-1">Gerencie as escalas de ministros</p>
        </div>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus size={18} className="mr-2" />
          Nova Escala
        </Button>
      </div>

      {alertMessage && (
        <Alert variant={alertVariant} onClose={() => setAlertMessage('')}>{alertMessage}</Alert>
      )}

      <Card>
        <Select
          label="Filtrar por Status"
          options={[
            { value: '', label: 'Todos' },
            ...Object.values(StatusEscala).map((s) => ({ value: s, label: s })),
          ]}
          onChange={(e) => setFilterStatus(e.target.value)}
        />
      </Card>

      <div className="space-y-4">
        {filteredEscalas.length > 0 ? (
          filteredEscalas.map((escala) => {
            const evento = escala.evento ?? eventos.find((e) => e.id === escala.eventoId);
            return (
              <Card key={escala.id}>
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900 dark:text-white">{evento?.nome ?? '—'}</h3>
                    <Badge variant={getStatusColor(escala.status)} className="mt-2">{escala.status}</Badge>
                  </div>
                  {escala.status === StatusEscala.PROPOSTA && escala.id != null && (
                    <div className="flex gap-2">
                      <Button size="sm" onClick={() => handleApprove(escala.id!)}>
                        <CheckCircle size={16} className="mr-1" /> Aprovar
                      </Button>
                      <Button size="sm" variant="danger" onClick={() => handleCancel(escala.id!)}>
                        <XCircle size={16} className="mr-1" /> Cancelar
                      </Button>
                    </div>
                  )}
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                  <div>
                    <p className="text-slate-600 dark:text-slate-400">📅 Data</p>
                    <p className="font-medium text-slate-900 dark:text-white">{evento?.data ? new Date(evento.data).toLocaleDateString('pt-BR') : '—'}</p>
                  </div>
                  <div>
                    <p className="text-slate-600 dark:text-slate-400">🕐 Horário</p>
                    <p className="font-medium text-slate-900 dark:text-white">{evento?.horario ?? '—'}</p>
                  </div>
                  <div>
                    <p className="text-slate-600 dark:text-slate-400">👥 Ministros</p>
                    <p className="font-medium text-slate-900 dark:text-white">{escala.escalaMinistros.length}/{evento?.maxMinistros ?? '?'}</p>
                  </div>
                  <div>
                    <p className="text-slate-600 dark:text-slate-400">📝 Atribuição</p>
                    <p className="font-medium text-slate-900 dark:text-white">{new Date(escala.dataAtribuicao).toLocaleDateString('pt-BR')}</p>
                  </div>
                </div>

                {escala.observacao && (
                  <div className="mt-4 pt-4 border-t border-slate-200 dark:border-slate-700">
                    <p className="text-sm text-slate-600 dark:text-slate-400">{escala.observacao}</p>
                  </div>
                )}
              </Card>
            );
          })
        ) : (
          <div className="text-center py-12">
            <p className="text-slate-600 dark:text-slate-400 text-lg">Nenhuma escala encontrada</p>
          </div>
        )}
      </div>

      <Modal
        isOpen={isModalOpen}
        title="Nova Escala"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>Cancelar</Button>
            <Button onClick={handleSave}>Criar</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Select
            label="Evento *"
            options={eventos
              .filter((e) => !e.cancelado)
              .map((e) => ({ value: String(e.id), label: e.nome }))}
            onChange={(e) => setFormData({ ...formData, eventoId: Number(e.target.value) })}
          />
          <Input
            label="Observação"
            value={formData.observacao as string}
            onChange={(e) => setFormData({ ...formData, observacao: e.target.value })}
            placeholder="Adicione observações..."
          />
        </div>
      </Modal>
    </div>
  );
};
