import React, { useState, useEffect } from 'react';
import { MinistroService, EventoService, EscalaService, FeedbackService } from '@/services/api';
import { Card, Badge, Spinner } from '@/components/ui';
import { Ministro, Evento, Escala, Feedback } from '@/types';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Users, Calendar, Clipboard, MessageSquare, TrendingUp } from 'lucide-react';

export const Dashboard: React.FC = () => {
  const [ministros, setMinistros] = useState<Ministro[]>([]);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [escalas, setEscalas] = useState<Escala[]>([]);
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);

  useEffect(() => {
    let isMounted = true;

    const loadData = async () => {
      try {
        const [ministrosData, eventosData, escalasData, feedbacksData] = await Promise.all([
          MinistroService.getAllMinistros(),
          EventoService.getAllEventos(),
          EscalaService.getAllEscalas(),
          FeedbackService.getAllFeedbacks(),
        ]);

        if (isMounted) {
          setMinistros(ministrosData || []);
          setEventos(eventosData || []);
          setEscalas(escalasData || []);
          setFeedbacks(feedbacksData || []);
        }
      } catch (error) {
        console.error('Erro ao carregar dados:', error);
      }
    };

    loadData();

    return () => {
      isMounted = false;
    };
  }, []);

  const stats = [
    {
      label: 'Total de Ministros',
      value: ministros.length,
      icon: Users,
      color: 'bg-blue-100 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400',
    },
    {
      label: 'Eventos Ativos',
      value: eventos.filter((e) => !e.cancelado).length,
      icon: Calendar,
      color: 'bg-green-100 text-green-600 dark:bg-green-900/30 dark:text-green-400',
    },
    {
      label: 'Escalas Ativas',
      value: escalas.length,
      icon: Clipboard,
      color: 'bg-purple-100 text-purple-600 dark:bg-purple-900/30 dark:text-purple-400',
    },
    {
      label: 'Feedbacks Pendentes',
      value: feedbacks.filter((f) => f.status === 'PENDENTE').length,
      icon: MessageSquare,
      color: 'bg-orange-100 text-orange-600 dark:bg-orange-900/30 dark:text-orange-400',
    },
  ];

  const nextEvents = eventos.filter((e) => !e.cancelado).slice(0, 3);
  const recentFeedbacks = feedbacks.slice(-3);
  const activeMinistros = ministros.filter((m) => m.ativo);

  return (
    <div className="space-y-8">
      {/* Page Title */}
      <div>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white mb-2">Dashboard</h1>
        <p className="text-slate-600 dark:text-slate-400">Bem-vindo ao sistema de gestão de escalas de ministros</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.label} className="flex items-center gap-4">
              <div className={`${stat.color} p-3 rounded-lg flex-shrink-0`}>
                <Icon size={24} />
              </div>
              <div>
                <p className="text-sm text-slate-600 dark:text-slate-400">{stat.label}</p>
                <p className="text-2xl font-bold text-slate-900 dark:text-white">{stat.value}</p>
              </div>
            </Card>
          );
        })}
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Próximos Eventos */}
        <Card className="lg:col-span-2" title="📅 Próximos Eventos">
          {nextEvents.length > 0 ? (
            <div className="space-y-3">
              {nextEvents.map((evento) => (
                <div
                  key={evento.id}
                  className="flex items-start justify-between p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
                >
                  <div className="flex-1">
                    <h3 className="font-semibold text-slate-900 dark:text-white">{evento.nome}</h3>
                    <p className="text-sm text-slate-600 dark:text-slate-400">
                      📍 {evento.local} • 🕐 {evento.horario}
                    </p>
                    <p className="text-xs text-slate-500 dark:text-slate-500 mt-1">
                      {format(new Date(evento.data), 'dd MMMM yyyy', { locale: ptBR })}
                    </p>
                  </div>
                  <Badge variant="primary">{evento.tipoEvento.replace(/_/g, ' ')}</Badge>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-slate-600 dark:text-slate-400 text-center py-4">Nenhum evento agendado</p>
          )}
        </Card>

        {/* Ministros Ativos */}
        <Card title="👥 Ministros Ativos">
          <div className="space-y-2">
            <p className="text-3xl font-bold text-primary-600">{activeMinistros.length}</p>
            <p className="text-sm text-slate-600 dark:text-slate-400">de {ministros.length} ministros</p>
            <div className="mt-4 pt-4 border-t border-slate-200 dark:border-slate-700">
              <ul className="space-y-2">
                {activeMinistros.slice(0, 5).map((m) => (
                  <li key={m.id} className="text-sm text-slate-700 dark:text-slate-300">
                    ✓ {m.nome}
                  </li>
                ))}
              </ul>
              {activeMinistros.length > 5 && (
                <p className="text-xs text-slate-500 dark:text-slate-500 mt-2">
                  +{activeMinistros.length - 5} ministros
                </p>
              )}
            </div>
          </div>
        </Card>
      </div>

      {/* Feedbacks Recentes */}
      <Card title="💬 Feedbacks Recentes">
        {recentFeedbacks.length > 0 ? (
          <div className="space-y-3">
            {recentFeedbacks.map((feedback) => (
              <div
                key={feedback.id}
                className="flex items-start justify-between p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg"
              >
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-slate-900 dark:text-white">⭐ {feedback.nota}/10</span>
                    <Badge
                      variant={
                        feedback.status === 'RESPONDIDO' ? 'success' : feedback.status === 'ARQUIVADO' ? 'danger' : 'warning'
                      }
                    >
                      {feedback.status}
                    </Badge>
                  </div>
                  <p className="text-sm text-slate-600 dark:text-slate-400">{feedback.comentario}</p>
                  <p className="text-xs text-slate-500 dark:text-slate-500 mt-1">
                    {format(new Date(feedback.dataEnvio), 'dd/MM/yyyy HH:mm')}
                  </p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-slate-600 dark:text-slate-400 text-center py-4">Nenhum feedback recebido</p>
        )}
      </Card>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <div className="flex items-center gap-3">
            <div className="bg-green-100 text-green-600 dark:bg-green-900/30 dark:text-green-400 p-3 rounded-lg">
              <TrendingUp size={20} />
            </div>
            <div>
              <p className="text-sm text-slate-600 dark:text-slate-400">Taxa de Confirmação</p>
              <p className="text-2xl font-bold text-slate-900 dark:text-white">85%</p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="text-center">
            <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">Satisfação Média</p>
            <p className="text-3xl font-bold text-slate-900 dark:text-white">8.5/10</p>
          </div>
        </Card>

        <Card>
          <div className="text-center">
            <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">Escalas Aprovadas</p>
            <p className="text-3xl font-bold text-slate-900 dark:text-white">{escalas.length}</p>
          </div>
        </Card>
      </div>
    </div>
  );
};
