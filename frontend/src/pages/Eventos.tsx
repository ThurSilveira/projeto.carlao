import React, { useState, useEffect } from 'react';
import { EventoService } from '@/services/api';
import { Card, Button, Input, Badge, Spinner, Modal, Select, Alert } from '@/components/ui';
import { Evento, TipoEvento } from '@/types';
import { CalendarPlus, Trash2, Edit2 } from 'lucide-react';

export const EventosPage: React.FC = () => {
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingEvento, setEditingEvento] = useState<Evento | null>(null);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertVariant, setAlertVariant] = useState<'success' | 'error'>('success');
  const [formData, setFormData] = useState<Partial<Evento>>({
    nome: '', data: '', horario: '',
    tipoEvento: TipoEvento.MISSA_PAROQUIAL,
    maxMinistros: 6, local: '', cancelado: false,
  });

  useEffect(() => { loadEventos(); }, []);

  const loadEventos = async () => {
    try {
      setLoading(true);
      setEventos(await EventoService.getAllEventos());
    } catch {
      showAlert('Erro ao carregar eventos', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showAlert = (msg: string, variant: 'success' | 'error') => {
    setAlertMessage(msg);
    setAlertVariant(variant);
  };

  const handleSave = async () => {
    if (!formData.nome || !formData.data || !formData.horario) {
      showAlert('Preencha todos os campos obrigatórios', 'error');
      return;
    }
    try {
      if (editingEvento?.id != null) {
        await EventoService.updateEvento(editingEvento.id, formData);
        showAlert('Evento atualizado com sucesso!', 'success');
      } else {
        await EventoService.createEvento(formData);
        showAlert('Evento criado com sucesso!', 'success');
      }
      await loadEventos();
      setIsModalOpen(false);
      resetForm();
    } catch {
      showAlert('Erro ao salvar evento', 'error');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Tem certeza que deseja deletar este evento?')) return;
    try {
      await EventoService.deleteEvento(id);
      showAlert('Evento deletado com sucesso!', 'success');
      await loadEventos();
    } catch {
      showAlert('Erro ao deletar evento', 'error');
    }
  };

  const handleCancel = async (id: number) => {
    if (!confirm('Tem certeza que deseja cancelar este evento?')) return;
    try {
      await EventoService.cancelEvento(id);
      showAlert('Evento cancelado com sucesso!', 'success');
      await loadEventos();
    } catch {
      showAlert('Erro ao cancelar evento', 'error');
    }
  };

  const handleEdit = (evento: Evento) => {
    setEditingEvento(evento);
    setFormData({ ...evento });
    setIsModalOpen(true);
  };

  const resetForm = () => {
    setEditingEvento(null);
    setFormData({ nome: '', data: '', horario: '', tipoEvento: TipoEvento.MISSA_PAROQUIAL, maxMinistros: 6, local: '', cancelado: false });
  };

  const filteredEventos = eventos.filter((e) => {
    const matchesSearch =
      e.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (e.local ?? '').toLowerCase().includes(searchTerm.toLowerCase());
    return matchesSearch && (!filterType || e.tipoEvento === filterType);
  });

  if (loading && eventos.length === 0) {
    return <div className="flex items-center justify-center h-96"><Spinner size="lg" /></div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 dark:text-white">📅 Eventos</h1>
          <p className="text-slate-600 dark:text-slate-400 mt-1">Gerencie eventos e missas da paróquia</p>
        </div>
        <Button onClick={() => { resetForm(); setIsModalOpen(true); }}>
          <CalendarPlus size={18} className="mr-2" />
          Novo Evento
        </Button>
      </div>

      {alertMessage && (
        <Alert variant={alertVariant} onClose={() => setAlertMessage('')}>{alertMessage}</Alert>
      )}

      <Card>
        <div className="flex flex-col gap-4 md:flex-row md:items-end">
          <Input label="Buscar" placeholder="Por nome ou local..." value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)} className="flex-1" />
          <Select
            label="Tipo"
            value={filterType}
            options={[
              { value: '', label: 'Todos' },
              ...Object.values(TipoEvento).map((t) => ({ value: t, label: t.replace(/_/g, ' ') })),
            ]}
            onChange={(e) => setFilterType(e.target.value)}
          />
        </div>
      </Card>

      <div className="space-y-4">
        {filteredEventos.length > 0 ? (
          filteredEventos.map((evento) => (
            <Card key={evento.id}>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-2">
                    <h3 className="text-lg font-semibold text-slate-900 dark:text-white">{evento.nome}</h3>
                    {evento.cancelado && <Badge variant="danger">Cancelado</Badge>}
                  </div>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm mt-3">
                    <div>
                      <p className="text-slate-600 dark:text-slate-400">📅 Data</p>
                      <p className="font-medium text-slate-900 dark:text-white">{new Date(evento.data).toLocaleDateString('pt-BR')}</p>
                    </div>
                    <div>
                      <p className="text-slate-600 dark:text-slate-400">🕐 Horário</p>
                      <p className="font-medium text-slate-900 dark:text-white">{evento.horario}</p>
                    </div>
                    <div>
                      <p className="text-slate-600 dark:text-slate-400">📍 Local</p>
                      <p className="font-medium text-slate-900 dark:text-white">{evento.local}</p>
                    </div>
                    <div>
                      <p className="text-slate-600 dark:text-slate-400">👥 Máx. Ministros</p>
                      <p className="font-medium text-slate-900 dark:text-white">{evento.maxMinistros}</p>
                    </div>
                  </div>
                  <div className="mt-3">
                    <Badge variant="primary">{evento.tipoEvento.replace(/_/g, ' ')}</Badge>
                  </div>
                </div>

                {!evento.cancelado && evento.id != null && (
                  <div className="flex gap-2 ml-4">
                    <button onClick={() => handleEdit(evento)}
                      className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors" aria-label="Editar">
                      <Edit2 size={16} className="text-slate-600 dark:text-slate-400" />
                    </button>
                    <button onClick={() => handleCancel(evento.id!)}
                      className="p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors" aria-label="Cancelar">
                      <Trash2 size={16} className="text-red-600 dark:text-red-400" />
                    </button>
                  </div>
                )}
              </div>
            </Card>
          ))
        ) : (
          <div className="text-center py-12">
            <p className="text-slate-600 dark:text-slate-400 text-lg">Nenhum evento encontrado</p>
          </div>
        )}
      </div>

      <Modal
        isOpen={isModalOpen}
        title={editingEvento ? 'Editar Evento' : 'Novo Evento'}
        onClose={() => { setIsModalOpen(false); resetForm(); }}
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => { setIsModalOpen(false); resetForm(); }}>Cancelar</Button>
            <Button onClick={handleSave}>{editingEvento ? 'Atualizar' : 'Criar'}</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Input label="Nome *" value={formData.nome as string}
            onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
            placeholder="Ex: Missa Dominical" />
          <Input label="Data *" type="date" value={formData.data as string}
            onChange={(e) => setFormData({ ...formData, data: e.target.value })} />
          <Input label="Horário *" type="time" value={formData.horario as string}
            onChange={(e) => setFormData({ ...formData, horario: e.target.value })} />
          <Select
            label="Tipo de Evento"
            value={formData.tipoEvento as string}
            options={Object.values(TipoEvento).map((t) => ({ value: t, label: t.replace(/_/g, ' ') }))}
            onChange={(e) => setFormData({ ...formData, tipoEvento: e.target.value as TipoEvento })}
          />
          <Input label="Local" value={formData.local as string}
            onChange={(e) => setFormData({ ...formData, local: e.target.value })}
            placeholder="Ex: Igreja Matriz" />
          <Input label="Máximo de Ministros" type="number" min="1"
            value={formData.maxMinistros as number}
            onChange={(e) => setFormData({ ...formData, maxMinistros: parseInt(e.target.value) || 6 })} />
        </div>
      </Modal>
    </div>
  );
};
