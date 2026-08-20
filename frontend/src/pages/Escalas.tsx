import React, { useCallback, useEffect, useState } from 'react';
import { EscalaService, EventoService } from '@/services/api';
import { Card, Badge, Spinner, Button, Modal, Select, Alert } from '@/components/ui';
import { Escala, EscalaMinistro, Evento, StatusEscala, PreviewEscala, MinistroSituacao } from '@/types';
import { CheckCircle, XCircle, Zap, Users, Trash2, AlertTriangle } from 'lucide-react';
import { formatDate } from '@/utils/date';
import { getErrorMessage } from '@/utils/error';

export const EscalasPage: React.FC = () => {
  const [escalas, setEscalas] = useState<Escala[]>([]);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertVariant, setAlertVariant] = useState<'success' | 'error'>('success');
  const [filterStatus, setFilterStatus] = useState('');

  // modal: selecionar evento
  const [isGerarOpen, setIsGerarOpen] = useState(false);
  const [gerarEventoId, setGerarEventoId] = useState<number>(0);
  const [previewLoading, setPreviewLoading] = useState(false);

  // modal: pré-visualização
  const [preview, setPreview] = useState<PreviewEscala | null>(null);
  const [isPreviewOpen, setIsPreviewOpen] = useState(false);
  const [empatadosSelecionados, setEmpatadosSelecionados] = useState<Set<number>>(new Set());
  const [confirmarLoading, setConfirmarLoading] = useState(false);

  const [substituirPreview, setSubstituirPreview] = useState<PreviewEscala | null>(null);
  const [isSubstituirPreviewOpen, setIsSubstituirPreviewOpen] = useState(false);
  const [substituirPreviewEscalaId, setSubstituirPreviewEscalaId] = useState<number | null>(null);
  const [substituirPreviewTargetId, setSubstituirPreviewTargetId] = useState<number | null>(null);
  const [substituirPreviewSelectedId, setSubstituirPreviewSelectedId] = useState<number | null>(null);
  const [substituirPreviewLoading, setSubstituirPreviewLoading] = useState(false);
  const [substituirPreviewConfirmLoading, setSubstituirPreviewConfirmLoading] = useState(false);

  // modal: resultado
  const [escalasGerada, setEscalaGerada] = useState<Escala | null>(null);
  const [isSubstituirOpen, setIsSubstituirOpen] = useState(false);
  const [escalaParaSubstituir, setEscalaParaSubstituir] = useState<Escala | null>(null);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const [escalasData, eventosData] = await Promise.all([
        EscalaService.getAllEscalas(),
        EventoService.getAllEventos(),
      ]);
      setEscalas(escalasData);
      setEventos(eventosData);
    } catch {
      setAlertMessage('Erro ao carregar escalas');
      setAlertVariant('error');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let isMounted = true;

    const loadInitialData = async () => {
      try {
        const [escalasData, eventosData] = await Promise.all([
          EscalaService.getAllEscalas(),
          EventoService.getAllEventos(),
        ]);
        if (isMounted) {
          setEscalas(escalasData);
          setEventos(eventosData);
        }
      } catch {
        if (isMounted) {
          setAlertMessage('Erro ao carregar escalas');
          setAlertVariant('error');
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    void loadInitialData();
    return () => { isMounted = false; };
  }, []);

  const showAlert = (msg: string, variant: 'success' | 'error') => {
    setAlertMessage(msg);
    setAlertVariant(variant);
  };

  const handlePreview = async () => {
    if (!gerarEventoId) { showAlert('Selecione um evento', 'error'); return; }
    setPreviewLoading(true);
    try {
      const prev = await EscalaService.previewEscala(gerarEventoId);
      setPreview(prev);
      setEmpatadosSelecionados(new Set(prev.selecionadosAuto));
      setIsGerarOpen(false);
      setIsPreviewOpen(true);
    } catch (error: unknown) {
      showAlert(getErrorMessage(error, 'Erro ao pré-visualizar escala'), 'error');
    } finally {
      setPreviewLoading(false);
    }
  };

  const toggleEmpatado = (id: number) => {
    setEmpatadosSelecionados((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  };

  const handleConfirmar = async (useSystem: boolean) => {
    if (!preview) return;
    setConfirmarLoading(true);
    try {
      const definitivosIds = preview.definitivos.map((m) => m.id);
      const empatadosIds = useSystem ? preview.selecionadosAuto : [...empatadosSelecionados];
      const resultado = await EscalaService.gerarEscala(gerarEventoId, [...definitivosIds, ...empatadosIds]);
      setIsPreviewOpen(false);
      setPreview(null);
      setGerarEventoId(0);
      setEscalaGerada(resultado);
      await loadData();
    } catch (error: unknown) {
      showAlert(getErrorMessage(error, 'Erro ao gerar escala'), 'error');
    } finally {
      setConfirmarLoading(false);
    }
  };

  const fecharPreview = () => {
    setIsPreviewOpen(false);
    setPreview(null);
    setIsGerarOpen(true);
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

  const openSubstituirModal = (escala: Escala) => {
    setEscalaParaSubstituir(escala);
    setIsSubstituirOpen(true);
  };

  const closeSubstituirModal = () => {
    setIsSubstituirOpen(false);
    setEscalaParaSubstituir(null);
  };

  const handleOpenSubstituicaoPreview = async (escalaId: number, ministroId: number) => {
    setIsSubstituirOpen(false);
    setEscalaParaSubstituir(null);
    setSubstituirPreviewLoading(true);
    try {
      const prev = await EscalaService.previewSubstituicao(escalaId, ministroId);
      setSubstituirPreview(prev);
      setSubstituirPreviewEscalaId(escalaId);
      setSubstituirPreviewTargetId(ministroId);
      setSubstituirPreviewSelectedId(prev.selecionadosAuto[0] ?? null);
      setIsSubstituirPreviewOpen(true);
    } catch (error: unknown) {
      showAlert(getErrorMessage(error, 'Erro ao pré-visualizar substituição'), 'error');
      setIsSubstituirOpen(true);
    } finally {
      setSubstituirPreviewLoading(false);
    }
  };

  const closeSubstituirPreview = () => {
    setIsSubstituirPreviewOpen(false);
    setSubstituirPreview(null);
    setSubstituirPreviewEscalaId(null);
    setSubstituirPreviewTargetId(null);
    setSubstituirPreviewSelectedId(null);
  };

  const handleSubstituirPreviewToggle = (id: number) => {
    setSubstituirPreviewSelectedId((prev) => (prev === id ? null : id));
  };

  const handleConfirmarSubstituicao = async (useSystem: boolean) => {
    if (!substituirPreview || substituirPreviewTargetId == null) return;
    setSubstituirPreviewConfirmLoading(true);
    try {
      let selectedId: number | undefined;
      if (useSystem || !substituirPreview.temEmpate) {
        selectedId = substituirPreview.selecionadosAuto[0];
      } else {
        selectedId = substituirPreviewSelectedId ?? undefined;
      }
      if (!selectedId) {
        throw new Error('Selecione um substituto ou use a opção Sistema decide.');
      }
      if (!substituirPreviewEscalaId) {
        throw new Error('Escala não encontrada para substituição.');
      }
      await EscalaService.substituirEscala(substituirPreviewEscalaId, substituirPreviewTargetId, selectedId);
      showAlert('Substituição realizada com sucesso!', 'success');
      closeSubstituirPreview();
      await loadData();
    } catch (error: unknown) {
      showAlert(getErrorMessage(error, 'Erro ao confirmar substituição'), 'error');
    } finally {
      setSubstituirPreviewConfirmLoading(false);
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

  const substituirPreviewDisponiveis = substituirPreview
    ? [...substituirPreview.definitivos, ...substituirPreview.empatados]
    : [];
  // preview validation
  const empatadoCount = empatadosSelecionados.size;
  const vagasNoEmpate = preview?.vagasNoEmpate ?? 0;
  const selecaoValida = !preview?.temEmpate || empatadoCount === vagasNoEmpate;

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
                      {escala.status === StatusEscala.APROVADA && (
                        <Button size="sm" variant="secondary" onClick={() => openSubstituirModal(escala)}>
                          <Users size={16} className="mr-1" /> Editar
                        </Button>
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
                    <p className="font-medium">{evento?.data ? formatDate(evento.data) : '—'}</p>
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

      {/* Modal 1: Selecionar evento */}
      <Modal
        isOpen={isGerarOpen}
        title="⚡ Gerar Escala Automaticamente"
        onClose={() => { setIsGerarOpen(false); setGerarEventoId(0); }}
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => { setIsGerarOpen(false); setGerarEventoId(0); }}>
              Cancelar
            </Button>
            <Button onClick={handlePreview} disabled={previewLoading}>
              {previewLoading ? <Spinner size="sm" /> : <><Zap size={16} className="mr-1" /> Pré-visualizar</>}
            </Button>
          </div>
        }
      >
        <div className="space-y-3">
          <p className="text-sm text-slate-600 dark:text-slate-400">
            Selecione o evento. O sistema irá mostrar quais ministros estão disponíveis, excluídos e em empate antes de gerar.
          </p>
          <Select
            label="Evento *"
            options={[
              { value: '', label: 'Selecione um evento...' },
              ...eventosDisponiveis.map((e) => ({
                value: String(e.id),
                label: `${e.nome} — ${e.data ? formatDate(e.data) : ''} ${e.horario}`,
              })),
            ]}
            onChange={(e) => setGerarEventoId(Number(e.target.value))}
          />
        </div>
      </Modal>

      {/* Modal 2: Pré-visualização */}
      {preview && (
        <Modal
          isOpen={isPreviewOpen}
          title="🔍 Pré-visualização da Escala"
          onClose={fecharPreview}
          actions={
            <div className="flex gap-2 flex-wrap">
              <Button variant="secondary" onClick={fecharPreview}>
                Voltar
              </Button>
              {preview.temEmpate && (
                <Button
                  variant="secondary"
                  onClick={() => handleConfirmar(true)}
                  disabled={confirmarLoading}
                >
                  {confirmarLoading ? <Spinner size="sm" /> : 'Sistema decide'}
                </Button>
              )}
              <Button
                onClick={() => handleConfirmar(false)}
                disabled={confirmarLoading || !selecaoValida}
              >
                {confirmarLoading ? <Spinner size="sm" /> : <><CheckCircle size={16} className="mr-1" /> Confirmar seleção</>}
              </Button>
            </div>
          }
        >
          <div className="space-y-5 max-h-[60vh] overflow-y-auto pr-1">

            {/* Excluídos */}
            {preview.excluidos.length > 0 && (
              <section>
                <p className="text-sm font-semibold text-red-700 dark:text-red-400 mb-2 flex items-center gap-1">
                  <XCircle size={14} /> Ministros indisponíveis ({preview.excluidos.length})
                </p>
                <ul className="space-y-1">
                  {preview.excluidos.map((m) => (
                    <li key={m.id} className="flex items-start justify-between gap-2 p-2 rounded-lg bg-red-50 dark:bg-red-900/20 text-sm">
                      <span className="font-medium text-slate-800 dark:text-slate-200">
                        {m.nome}
                        {m.funcao && <span className="ml-1 text-slate-500">· {m.funcao}</span>}
                      </span>
                      <span className="text-red-600 dark:text-red-400 text-xs shrink-0 text-right">{m.motivoExclusao}</span>
                    </li>
                  ))}
                </ul>
              </section>
            )}

            {/* Definitivos */}
            {preview.definitivos.length > 0 && (
              <section>
                <p className="text-sm font-semibold text-green-700 dark:text-green-400 mb-2 flex items-center gap-1">
                  <CheckCircle size={14} /> Selecionados ({preview.definitivos.length})
                </p>
                <ul className="space-y-1">
                  {preview.definitivos.map((m) => (
                    <MinistroRow key={m.id} m={m} locked />
                  ))}
                </ul>
              </section>
            )}

            {/* Empatados */}
            {preview.temEmpate && preview.empatados.length > 0 && (
              <section>
                <p className="text-sm font-semibold text-amber-700 dark:text-amber-400 mb-1 flex items-center gap-1">
                  ⚖️ Em empate — escolha {vagasNoEmpate} de {preview.empatados.length}
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400 mb-2">
                  Todos com {preview.empatados[0]?.escalasMes ?? 0} escala(s) no mês. O sistema pré-selecionou {vagasNoEmpate}.
                </p>

                {/* Business rule warning */}
                {!selecaoValida && (
                  <div className="flex items-start gap-2 p-2 mb-2 rounded-lg bg-amber-50 dark:bg-amber-900/20 text-amber-800 dark:text-amber-300 text-xs">
                    <AlertTriangle size={14} className="shrink-0 mt-0.5" />
                    <span>
                      {empatadoCount < vagasNoEmpate
                        ? `Selecione mais ${vagasNoEmpate - empatadoCount} ministro(s) para preencher todas as vagas.`
                        : `Você selecionou ${empatadoCount - vagasNoEmpate} ministro(s) a mais — isso quebraria o equilíbrio de escalas.`}
                    </span>
                  </div>
                )}

                <ul className="space-y-1">
                  {preview.empatados.map((m) => (
                    <MinistroRow
                      key={m.id}
                      m={m}
                      checked={empatadosSelecionados.has(m.id)}
                      onToggle={() => toggleEmpatado(m.id)}
                    />
                  ))}
                </ul>
              </section>
            )}

            {/* Nenhum disponível */}
            {preview.definitivos.length === 0 && !preview.temEmpate && (
              <p className="text-sm text-slate-500 dark:text-slate-400 text-center py-4">
                Nenhum ministro disponível para este evento.
              </p>
            )}
          </div>
        </Modal>
      )}

      {substituirPreview && (
        <Modal
          isOpen={isSubstituirPreviewOpen}
          title="🔄 Pré-visualização da Substituição"
          onClose={closeSubstituirPreview}
          actions={
            <div className="flex gap-2 flex-wrap">
              <Button variant="secondary" onClick={closeSubstituirPreview}>
                Voltar
              </Button>
              {substituirPreview.temEmpate && (
                <Button
                  variant="secondary"
                  onClick={() => handleConfirmarSubstituicao(true)}
                  disabled={substituirPreviewConfirmLoading}
                >
                  {substituirPreviewConfirmLoading ? <Spinner size="sm" /> : 'Sistema decide'}
                </Button>
              )}
              <Button
                onClick={() => handleConfirmarSubstituicao(false)}
                disabled={substituirPreviewConfirmLoading || (substituirPreview.temEmpate && substituirPreviewSelectedId == null)}
              >
                {substituirPreviewConfirmLoading ? <Spinner size="sm" /> : <><CheckCircle size={16} className="mr-1" /> Confirmar substituição</>}
              </Button>
            </div>
          }
        >
          <div className="space-y-5 max-h-[60vh] overflow-y-auto pr-1">
            {substituirPreview.excluidos.length > 0 && (
              <section>
                <p className="text-sm font-semibold text-red-700 dark:text-red-400 mb-2 flex items-center gap-1">
                  <XCircle size={14} /> Ministros indisponíveis ({substituirPreview.excluidos.length})
                </p>
                <ul className="space-y-1">
                  {substituirPreview.excluidos.map((m) => (
                    <li key={m.id} className="flex items-start justify-between gap-2 p-2 rounded-lg bg-red-50 dark:bg-red-900/20 text-sm">
                      <span className="font-medium text-slate-800 dark:text-slate-200">
                        {m.nome}
                        {m.funcao && <span className="ml-1 text-slate-500">· {m.funcao}</span>}
                      </span>
                      <span className="text-red-600 dark:text-red-400 text-xs shrink-0 text-right">{m.motivoExclusao}</span>
                    </li>
                  ))}
                </ul>
              </section>
            )}

            {substituirPreview.definitivos.length > 0 && (
              <section>
                <p className="text-sm font-semibold text-green-700 dark:text-green-400 mb-2 flex items-center gap-1">
                  <CheckCircle size={14} /> Selecionados ({substituirPreview.definitivos.length})
                </p>
                <ul className="space-y-1">
                  {substituirPreview.definitivos.map((m) => (
                    <MinistroRow key={m.id} m={m} locked />
                  ))}
                </ul>
              </section>
            )}

            {substituirPreviewDisponiveis.length > 0 && (
              <section>
                <p className="text-sm font-semibold text-slate-900 dark:text-slate-200 mb-2 flex items-center gap-1">
                  <Users size={14} /> Disponíveis para substituição ({substituirPreviewDisponiveis.length})
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400 mb-3">
                  Escolha um substituto à mão ou use o botão "Sistema decide" para escolher automaticamente.
                </p>
                <ul className="space-y-2">
                  {substituirPreviewDisponiveis.map((m) => (
                    <li
                      key={m.id}
                      className={`flex items-center justify-between gap-2 p-3 rounded-lg border transition-colors cursor-pointer
                        ${substituirPreviewSelectedId === m.id ? 'border-primary-600 bg-primary-50 dark:border-primary-400 dark:bg-primary-950/30' : 'border-slate-200 bg-slate-50 dark:border-slate-700 dark:bg-slate-800'}
                      `}
                      onClick={() => handleSubstituirPreviewToggle(m.id)}
                    >
                      <div>
                        <p className="font-medium text-slate-900 dark:text-white">{m.nome}</p>
                        {m.funcao && <p className="text-xs text-slate-500 dark:text-slate-400">{m.funcao}</p>}
                      </div>
                      <div className="flex items-center gap-2 text-xs text-slate-500 dark:text-slate-400">
                        <span>{m.escalasMes} escala(s)/mês</span>
                        <span className="px-2 py-1 rounded-full bg-slate-100 dark:bg-slate-900 text-slate-600 dark:text-slate-300">
                          {substituirPreviewSelectedId === m.id ? 'Selecionado' : 'Selecionar'}
                        </span>
                      </div>
                    </li>
                  ))}
                </ul>
              </section>
            )}

            {substituirPreviewDisponiveis.length === 0 && (
              <p className="text-sm text-slate-500 dark:text-slate-400 text-center py-4">
                Nenhum ministro disponível para substituição.
              </p>
            )}
          </div>
        </Modal>
      )}

      {/* Modal 3: Resultado */}
      {escalasGerada && (
        <Modal
          isOpen={true}
          title="✅ Escala Gerada com Sucesso!"
          onClose={() => setEscalaGerada(null)}
          actions={<Button onClick={() => setEscalaGerada(null)}>Fechar</Button>}
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
                    {em.ministroFuncao && <Badge variant="primary">{em.ministroFuncao}</Badge>}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </Modal>
      )}

      {escalaParaSubstituir && (
        <Modal
          isOpen={isSubstituirOpen}
          title="✏️ Substituir Ministro"
          onClose={closeSubstituirModal}
          actions={
            <div className="flex gap-2">
              <Button variant="secondary" onClick={closeSubstituirModal}>Fechar</Button>
            </div>
          }
        >
          <div className="space-y-4">
            <p className="text-sm text-slate-600 dark:text-slate-400">
              Escolha o ministro que não poderá participar da escala aprovada. O sistema fará um novo sorteio automático de substituto disponível.
            </p>
            {escalaParaSubstituir.escalaMinistros.length > 0 ? (
              <div className="space-y-2">
                {escalaParaSubstituir.escalaMinistros.map((em) => (
                  <div key={em.id ?? em.ministroId} className="flex items-center justify-between gap-2 p-3 rounded-lg bg-slate-50 dark:bg-slate-800">
                    <div>
                      <p className="font-medium text-slate-900 dark:text-white">{em.ministroNome ?? `Ministro #${em.ministroId}`}</p>
                      {em.ministroFuncao && <p className="text-xs text-slate-500 dark:text-slate-400">{em.ministroFuncao}</p>}
                    </div>
                    <Button
                      size="sm"
                      variant="danger"
                      onClick={() => handleOpenSubstituicaoPreview(escalaParaSubstituir.id!, em.ministroId)}
                      disabled={substituirPreviewLoading}
                    >
                      {substituirPreviewLoading ? <Spinner size="sm" /> : 'Substituir'}
                    </Button>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-slate-500 dark:text-slate-400">Nenhum ministro encontrado para substituição.</p>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
};

interface MinistroRowProps {
  m: MinistroSituacao;
  locked?: boolean;
  checked?: boolean;
  onToggle?: () => void;
}

const MinistroRow: React.FC<MinistroRowProps> = ({ m, locked, checked, onToggle }) => (
  <li
    className={`flex items-center justify-between gap-2 p-2 rounded-lg text-sm cursor-pointer transition-colors
      ${locked
        ? 'bg-green-50 dark:bg-green-900/20'
        : checked
          ? 'bg-blue-50 dark:bg-blue-900/20'
          : 'bg-slate-50 dark:bg-slate-800 opacity-60'
      }`}
    onClick={!locked ? onToggle : undefined}
    role={!locked ? 'checkbox' : undefined}
    aria-checked={!locked ? checked : undefined}
    tabIndex={!locked ? 0 : undefined}
    onKeyDown={!locked ? (e) => { if (e.key === ' ' || e.key === 'Enter') onToggle?.(); } : undefined}
  >
    <div className="flex items-center gap-2 min-w-0">
      {!locked && (
        <input
          type="checkbox"
          readOnly
          checked={checked}
          className="w-4 h-4 accent-primary-600 shrink-0"
          tabIndex={-1}
        />
      )}
      {locked && <CheckCircle size={14} className="text-green-500 shrink-0" />}
      <span className="font-medium text-slate-900 dark:text-white truncate">{m.nome}</span>
      {m.funcao && <span className="text-slate-500 dark:text-slate-400 text-xs shrink-0">· {m.funcao}</span>}
    </div>
    <span className="text-xs text-slate-400 shrink-0">{m.escalasMes} escala(s)/mês</span>
  </li>
);
