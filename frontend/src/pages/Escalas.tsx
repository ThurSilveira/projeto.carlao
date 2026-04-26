import React, { useState, useEffect } from 'react';
import { EscalaService, EventoService } from '@/services/api';
import { Card, Badge, Spinner, Button, Modal, Select, Alert } from '@/components/ui';
import { Escala, EscalaMinistro, Evento, StatusEscala } from '@/types';
import { CheckCircle, XCircle, Zap, Users, Trash2 } from 'lucide-react';

export const EscalasPage: React.FC = () => {
  const [escalas, setEscalas] = useState<Escala[]>([]);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertVariant, setAlertVariant] = useState<'success' | 'error'>('success');
  const [filterStatus, setFilterStatus] = useState('');

  // modal geração automática
  const [isGerarOpen, setIsGerarOpen] = useState(false);
  const [gerarEventoId, setGerarEventoId] = useState<number>(0);
  const [gerarLoading, setGerarLoading] = useState(false);

  // modal resultado da geração
  const [escalasGerada, setEscalaGerada] = useState<Escala | null>(null);

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

  const handleGerar = async () => {
    if (!gerarEventoId) { showAlert('Selecione um evento', 'error'); return; }
    setGerarLoading(true);
    try {
      const resultado = await EscalaService.gerarEscala(gerarEventoId);
      setIsGerarOpen(false);
      setGerarEventoId(0);
      setEscalaGerada(resultado);
      await loadData();
    } catch (err: any) {
      showAlert(err?.response?.data?.message || 'Erro ao gerar escala', 'error');
    } finally {
      setGerarLoading(false);
    }
  };

  const handleApprove = async (id: number) => {
    try {
      await EscalaService.approveEscala(id);
      showAlert('Escala aprovada!', 'success');
      await loadData();
    } catch {
      showAlert('Erro ao aprovar escala', 'error');
    }
  };

  const handleCancel = async (id: number) => {
    if (!confirm('Cancelar esta escala?')) return;
    try {
      await EscalaService.cancelEscala(id);
      showAlert('Escala cancelada', 'success');
      await loadData();
    } catch {
      showAlert('Erro ao cancelar escala', 'error');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Deletar permanentemente esta escala?')) return;
    try {
      await EscalaService.deleteEscala(id);
      showAlert('Escala deletada', 'success');
      await loadData();
    } catch {
      showAlert('Erro ao deletar escala', 'error');
    }
  };

  const getStatusColor = (status: StatusEscala): 'primary' | 'success' | 'warning' | 'danger' => {
    switch (status) {
      case StatusEscala.PROPOSTA:   return 'warning';
      case StatusEscala.APROVADA:   return 'success';
      case StatusEscala.CONFIRMADA: return 'primary';
      case StatusEscala.CANCELADA:  return 'danger';
      default:                      return 'primary';
    }
  };

  const eventosDisponiveis = eventos.filter((e) => !e.cancelado);
  const filteredEscalas = escalas.filter((e) => !filterStatus || e.status === filterStatus);

  if (loading && escalas.length === 0) {
    return <div className="flex items-center justify-center h-96"><Spinner size="lg" /></div>;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 dark:text-white">📋 Escalas</h1>
          <p className="text-slate-600 dark:text-slate-400 mt-1">Gerencie as escalas de ministros</p>
        </div>
        <Button onClick={() => setIsGerarOpen(true)}>
          <Zap size={18} className="mr-2" />
          Gerar Escala
        </Button>
      </div>

      {alertMessage && (
        <Alert variant={alertVariant} onClose={() => setAlertMessage('')}>{alertMessage}</Alert>
      )}

      {/* Filtro */}
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

      {/* Lista de escalas */}
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
                  {escala.id != null && (
                    <div className="flex gap-2">
                      {escala.status === StatusEscala.PROPOSTA && (
                        <>
                          <Button size="sm" onClick={() => handleApprove(escala.id!)}>
                            <CheckCircle size={16} className="mr-1" /> Aprovar
                          </Button>
                          <Button size="sm" variant="danger" onClick={() => handleCancel(escala.id!)}>
                            <XCircle size={16} className="mr-1" /> Cancelar
                          </Button>
                        </>
                      )}
                      <button
                        onClick={() => handleDelete(escala.id!)}
                        className="p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                        aria-label="Deletar escala"
                        title="Deletar"
                      >
                        <Trash2 size={16} className="text-red-500" />
                      </button>
                    </div>
                  )}
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm mb-4">
                  <div>
                    <p className="text-slate-500 dark:text-slate-400">📅 Data</p>
                    <p className="font-medium">{evento?.data ? new Date(evento.data).toLocaleDateString('pt-BR') : '—'}</p>
                  </div>
                  <div>
                    <p className="text-slate-500 dark:text-slate-400">🕐 Horário</p>
                    <p className="font-medium">{evento?.horario ?? '—'}</p>
                  </div>
                  <div>
                    <p className="text-slate-500 dark:text-slate-400">📍 Local</p>
                    <p className="font-medium">{evento?.local ?? '—'}</p>
                  </div>
                  <div>
                    <p className="text-slate-500 dark:text-slate-400">👥 Vagas</p>
                    <p className="font-medium">{escala.escalaMinistros.length}/{evento?.maxMinistros ?? '?'}</p>
                  </div>
                </div>

                {/* Ministros escalados */}
                {escala.escalaMinistros.length > 0 && (
                  <div className="border-t border-slate-200 dark:border-slate-700 pt-4">
                    <p className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-2 flex items-center gap-1">
                      <Users size={14} /> Ministros escalados
                    </p>
                    <div className="flex flex-wrap gap-2">
                      {escala.escalaMinistros.map((em: EscalaMinistro, idx: number) => (
                        <span
                          key={em.id ?? idx}
                          className="inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300"
                        >
                          {em.ministroNome ?? `Ministro #${em.ministroId}`}
                          {em.ministroFuncao && (
                            <span className="text-blue-500 dark:text-blue-400">· {em.ministroFuncao}</span>
                          )}
                          {em.confirmacaoMinistro && <CheckCircle size={12} className="text-green-500" />}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {escala.observacao && (
                  <div className="mt-3 pt-3 border-t border-slate-200 dark:border-slate-700">
                    <p className="text-xs text-slate-500 dark:text-slate-400">{escala.observacao}</p>
                  </div>
                )}
              </Card>
            );
          })
        ) : (
          <div className="text-center py-12">
            <p className="text-slate-500 dark:text-slate-400 text-lg">Nenhuma escala encontrada</p>
          </div>
        )}
      </div>

      {/* Modal: Gerar Escala automática */}
      <Modal
        isOpen={isGerarOpen}
        title="⚡ Gerar Escala Automaticamente"
        onClose={() => { setIsGerarOpen(false); setGerarEventoId(0); }}
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => { setIsGerarOpen(false); setGerarEventoId(0); }}>
              Cancelar
            </Button>
            <Button onClick={handleGerar} disabled={gerarLoading}>
              {gerarLoading ? <Spinner size="sm" /> : <><Zap size={16} className="mr-1" /> Gerar</>}
            </Button>
          </div>
        }
      >
        <div className="space-y-3">
          <p className="text-sm text-slate-600 dark:text-slate-400">
            Selecione o evento. O sistema irá ordenar os ministros ativos por número de escalas no mês e sortear os disponíveis automaticamente.
          </p>
          <Select
            label="Evento *"
            options={[
              { value: '', label: 'Selecione um evento...' },
              ...eventosDisponiveis.map((e) => ({
                value: String(e.id),
                label: `${e.nome} — ${e.data ? new Date(e.data).toLocaleDateString('pt-BR') : ''} ${e.horario}`,
              })),
            ]}
            onChange={(e) => setGerarEventoId(Number(e.target.value))}
          />
        </div>
      </Modal>

      {/* Modal: Resultado da geração */}
      {escalasGerada && (
        <Modal
          isOpen={true}
          title="✅ Escala Gerada com Sucesso!"
          onClose={() => setEscalaGerada(null)}
          actions={
            <Button onClick={() => setEscalaGerada(null)}>Fechar</Button>
          }
        >
          <div className="space-y-4">
            <div className="bg-green-50 dark:bg-green-900/20 rounded-lg p-3 text-sm text-green-800 dark:text-green-300">
              {escalasGerada.observacao}
            </div>
            <div>
              <p className="font-semibold text-slate-800 dark:text-slate-200 mb-2 flex items-center gap-1">
                <Users size={16} /> Ministros selecionados ({escalasGerada.escalaMinistros.length})
              </p>
              <ul className="space-y-2">
                {escalasGerada.escalaMinistros.map((em: EscalaMinistro, idx: number) => (
                  <li
                    key={em.id ?? idx}
                    className="flex items-center justify-between p-2 rounded-lg bg-slate-50 dark:bg-slate-800"
                  >
                    <span className="font-medium text-slate-900 dark:text-white">
                      {em.ministroNome ?? `Ministro #${em.ministroId}`}
                    </span>
                    {em.ministroFuncao && (
                      <Badge variant="primary">{em.ministroFuncao}</Badge>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </Modal>
      )}

    </div>
  );
};
