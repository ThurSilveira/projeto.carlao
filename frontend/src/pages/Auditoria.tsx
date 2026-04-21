import React, { useState, useEffect } from 'react';
import { LogAuditoriaService } from '@/services/api';
import { Card, Badge, Spinner, Select, Alert } from '@/components/ui';
import { LogAuditoria, TipoAcao } from '@/types';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

export const AuditoriaPage: React.FC = () => {
  const [logs, setLogs] = useState<LogAuditoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [alertMessage, setAlertMessage] = useState('');
  const [filterEntidade, setFilterEntidade] = useState('');
  const [filterAcao, setFilterAcao] = useState('');

  useEffect(() => { loadLogs(); }, []);

  const loadLogs = async () => {
    try {
      setLoading(true);
      setLogs(await LogAuditoriaService.getLogs());
    } catch {
      setAlertMessage('Erro ao carregar logs de auditoria');
    } finally {
      setLoading(false);
    }
  };

  const filteredLogs = logs.filter((log) => {
    const matchesEntidade = !filterEntidade || log.entidade === filterEntidade;
    const matchesAcao = !filterAcao || log.acao === filterAcao;
    return matchesEntidade && matchesAcao;
  });

  const getAcaoVariant = (acao: TipoAcao): 'primary' | 'success' | 'warning' | 'danger' => {
    switch (acao) {
      case TipoAcao.CRIADO:
      case TipoAcao.APROVADO:
      case TipoAcao.CONFIRMADO:  return 'success';
      case TipoAcao.CANCELADO:
      case TipoAcao.DELETADO:    return 'danger';
      case TipoAcao.SUBSTITUIDO:
      case TipoAcao.ATUALIZADO:  return 'warning';
      default:                   return 'primary';
    }
  };

  const entidades = Array.from(new Set(logs.map((l) => l.entidade)));

  if (loading && logs.length === 0) {
    return <div className="flex items-center justify-center h-96"><Spinner size="lg" /></div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900">📝 Auditoria</h1>
        <p className="text-slate-600 mt-1">Histórico de todas as ações no sistema</p>
      </div>

      {alertMessage && (
        <Alert variant="error" onClose={() => setAlertMessage('')}>{alertMessage}</Alert>
      )}

      <Card>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Select
            label="Filtrar por Entidade"
            options={[{ value: '', label: 'Todas' }, ...entidades.map((e) => ({ value: e, label: e }))]}
            onChange={(e) => setFilterEntidade(e.target.value)}
          />
          <Select
            label="Filtrar por Ação"
            options={[
              { value: '', label: 'Todas' },
              ...Object.values(TipoAcao).map((a) => ({ value: a, label: a })),
            ]}
            onChange={(e) => setFilterAcao(e.target.value)}
          />
        </div>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card><div className="text-center"><p className="text-sm text-slate-600 mb-2">Total de Ações</p>
          <p className="text-3xl font-bold text-slate-900">{logs.length}</p></div></Card>
        <Card><div className="text-center"><p className="text-sm text-slate-600 mb-2">Criações</p>
          <p className="text-3xl font-bold text-green-600">
            {logs.filter((l) => l.acao === TipoAcao.CRIADO).length}</p></div></Card>
        <Card><div className="text-center"><p className="text-sm text-slate-600 mb-2">Aprovações</p>
          <p className="text-3xl font-bold text-blue-600">
            {logs.filter((l) => l.acao === TipoAcao.APROVADO).length}</p></div></Card>
        <Card><div className="text-center"><p className="text-sm text-slate-600 mb-2">Cancelamentos</p>
          <p className="text-3xl font-bold text-red-600">
            {logs.filter((l) => l.acao === TipoAcao.CANCELADO).length}</p></div></Card>
      </div>

      <Card title="Histórico Detalhado">
        {filteredLogs.length > 0 ? (
          <div className="space-y-3 max-h-96 overflow-y-auto">
            {filteredLogs.map((log, index) => (
              <div key={log.id ?? index}
                className="flex items-start gap-4 p-3 border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-2">
                    <Badge variant={getAcaoVariant(log.acao)} className="font-mono">{log.acao}</Badge>
                    <span className="text-sm font-medium text-slate-900">{log.entidade}</span>
                  </div>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
                    <div>
                      <p className="text-slate-600">Status Anterior</p>
                      <p className="font-mono text-slate-900">{log.statusAnterior ?? '—'}</p>
                    </div>
                    <div>
                      <p className="text-slate-600">Status Novo</p>
                      <p className="font-mono text-slate-900">{log.statusNovo ?? '—'}</p>
                    </div>
                    <div>
                      <p className="text-slate-600">Data/Hora</p>
                      <p className="font-medium text-slate-900">
                        {format(new Date(log.dataHora), 'dd/MM/yyyy HH:mm:ss', { locale: ptBR })}
                      </p>
                    </div>
                  </div>
                  {log.realizadoPorId && (
                    <p className="mt-2 text-sm text-slate-600">👤 {log.realizadoPorId}</p>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-slate-600 text-lg">Nenhum registro encontrado</p>
          </div>
        )}
      </Card>
    </div>
  );
};
