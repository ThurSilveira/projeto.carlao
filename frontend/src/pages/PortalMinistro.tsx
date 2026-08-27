import React, { FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import {
  addMonths,
  eachDayOfInterval,
  endOfMonth,
  format,
  parseISO,
  startOfMonth,
  subDays,
  subMonths,
} from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { CalendarDays, ChevronLeft, ChevronRight, Clock, MapPin, Pencil, Trash2 } from 'lucide-react';

import { Alert, Badge, Button, Card, Modal, Spinner } from '@/components/ui';
import { PortalMinistroService } from '@/services/api';
import type { CalendarioMinistroEvento, FeedbackMinistro, Indisponibilidade } from '@/types';
import { getErrorMessage } from '@/utils/error';


const dateKey = (value: Date): string => format(value, 'yyyy-MM-dd');

export const CalendarioMinistroPage: React.FC = () => {
  const [currentMonth, setCurrentMonth] = useState(() => startOfMonth(new Date()));
  const [events, setEvents] = useState<CalendarioMinistroEvento[]>([]);
  const [selectedEvent, setSelectedEvent] = useState<CalendarioMinistroEvento | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const { monthStart, monthEnd } = useMemo(() => ({
    monthStart: startOfMonth(currentMonth),
    monthEnd: endOfMonth(currentMonth),
  }), [currentMonth]);
  const days = eachDayOfInterval({ start: monthStart, end: monthEnd });

  useEffect(() => {
    let active = true;
    PortalMinistroService.calendario(dateKey(monthStart), dateKey(monthEnd))
      .then((data) => { if (active) setEvents(data); })
      .catch((requestError) => {
        if (active) setError(getErrorMessage(requestError, 'Não foi possível carregar o calendário.'));
      })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [monthEnd, monthStart]);

  const changeMonth = (nextMonth: Date) => {
    setLoading(true);
    setError('');
    setCurrentMonth(nextMonth);
  };

  const eventsByDate = useMemo(() => {
    const grouped = new Map<string, CalendarioMinistroEvento[]>();
    events.forEach((event) => grouped.set(event.data, [...(grouped.get(event.data) ?? []), event]));
    return grouped;
  }, [events]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Meu calendário</h1>
        <p className="mt-1 text-slate-600 dark:text-slate-400">
          Consulte os eventos da igreja. Os itens destacados fazem parte da sua escala.
        </p>
      </div>

      {error && <Alert variant="error" onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <CalendarDays className="text-primary-600" aria-hidden="true" />
            <h2 className="text-xl font-bold capitalize text-slate-900 dark:text-white">
              {format(currentMonth, 'MMMM yyyy', { locale: ptBR })}
            </h2>
          </div>
          <div className="flex gap-2">
            <Button variant="secondary" size="sm" onClick={() => changeMonth(subMonths(currentMonth, 1))} aria-label="Mês anterior">
              <ChevronLeft size={18} />
            </Button>
            <Button variant="secondary" size="sm" onClick={() => changeMonth(startOfMonth(new Date()))}>Hoje</Button>
            <Button variant="secondary" size="sm" onClick={() => changeMonth(addMonths(currentMonth, 1))} aria-label="Próximo mês">
              <ChevronRight size={18} />
            </Button>
          </div>
        </div>

        <div className="mb-4 flex flex-wrap gap-4 text-xs text-slate-600 dark:text-slate-300">
          <span className="flex items-center gap-2"><span className="size-3 rounded bg-primary-600" /> Minha escala</span>
          <span className="flex items-center gap-2"><span className="size-3 rounded bg-slate-300 dark:bg-neutral-600" /> Evento da igreja</span>
        </div>

        {loading ? (
          <div className="flex min-h-80 items-center justify-center"><Spinner size="lg" /></div>
        ) : (
          <div className="overflow-x-auto">
            <div className="min-w-[760px]">
              <div className="grid grid-cols-7 border-b border-slate-200 dark:border-neutral-700">
                {['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'].map((weekday) => (
                  <div key={weekday} className="px-2 py-3 text-center text-xs font-bold uppercase text-slate-500">{weekday}</div>
                ))}
              </div>
              <div className="grid grid-cols-7">
                {Array.from({ length: monthStart.getDay() }).map((_, index) => (
                  <div key={`empty-${index}`} className="min-h-32 border-b border-r border-slate-200 bg-slate-50 dark:border-neutral-700 dark:bg-neutral-900/40" />
                ))}
                {days.map((day) => {
                  const dayEvents = eventsByDate.get(dateKey(day)) ?? [];
                  return (
                    <div key={day.toISOString()} className="min-h-32 border-b border-r border-slate-200 p-2 dark:border-neutral-700">
                      <span className="text-xs font-semibold text-slate-500">{format(day, 'd')}</span>
                      <div className="mt-2 space-y-1.5">
                        {dayEvents.map((event) => (
                          <button
                            key={event.eventoId}
                            type="button"
                            onClick={() => setSelectedEvent(event)}
                            className={[
                              'w-full rounded-md px-2 py-1.5 text-left text-xs transition-colors focus-visible:outline-2 focus-visible:outline-primary-500',
                              event.escalado
                                ? 'bg-primary-600 text-white hover:bg-primary-700'
                                : 'bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-neutral-700 dark:text-slate-100',
                              event.cancelado ? 'line-through opacity-60' : '',
                            ].join(' ')}
                          >
                            <span className="block font-bold truncate">{event.nome}</span>
                            <span className="block opacity-80">{event.horario}</span>
                          </button>
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}
      </Card>

      <Modal isOpen={selectedEvent !== null} title={selectedEvent?.nome ?? 'Evento'} onClose={() => setSelectedEvent(null)}>
        {selectedEvent && (
          <div className="space-y-3 text-sm text-slate-700 dark:text-slate-200">
            <p className="flex items-center gap-2"><CalendarDays size={17} /> {format(parseISO(selectedEvent.data), 'dd/MM/yyyy')}</p>
            <p className="flex items-center gap-2"><Clock size={17} /> {selectedEvent.horario}</p>
            {selectedEvent.local && <p className="flex items-center gap-2"><MapPin size={17} /> {selectedEvent.local}</p>}
            <Badge variant={selectedEvent.escalado ? 'primary' : 'neutral'}>
              {selectedEvent.escalado ? 'Você está escalado' : 'Evento da igreja'}
            </Badge>
            {selectedEvent.cancelado && <Badge variant="danger">Cancelado</Badge>}
          </div>
        )}
      </Modal>
    </div>
  );
};

type UnavailabilityForm = {
  id?: number;
  data: string;
  horarioInicio: string;
  horarioFim: string;
  motivo: string;
};

const emptyUnavailability = (): UnavailabilityForm => ({
  data: dateKey(new Date()),
  horarioInicio: '',
  horarioFim: '',
  motivo: '',
});

export const IndisponibilidadesMinistroPage: React.FC = () => {
  const [items, setItems] = useState<Indisponibilidade[]>([]);
  const [form, setForm] = useState<UnavailabilityForm>(emptyUnavailability);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const load = useCallback(async () => {
    try {
      setItems(await PortalMinistroService.listarIndisponibilidades());
    } catch (error) {
      setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível carregar as indisponibilidades.') });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;
    PortalMinistroService.listarIndisponibilidades()
      .then((data) => { if (active) setItems(data); })
      .catch((error) => {
        if (active) setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível carregar as indisponibilidades.') });
      })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setMessage(null);
    try {
      const payload = {
        data: form.data,
        horarioInicio: form.horarioInicio || undefined,
        horarioFim: form.horarioFim || undefined,
        motivo: form.motivo || undefined,
      };
      if (form.id) await PortalMinistroService.atualizarIndisponibilidade(form.id, payload);
      else await PortalMinistroService.criarIndisponibilidade(payload);
      setForm(emptyUnavailability());
      setMessage({ type: 'success', text: form.id ? 'Indisponibilidade atualizada.' : 'Indisponibilidade cadastrada.' });
      await load();
    } catch (error) {
      setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível salvar a indisponibilidade.') });
    } finally {
      setSaving(false);
    }
  };

  const edit = (item: Indisponibilidade) => setForm({
    id: item.id,
    data: item.data,
    horarioInicio: item.horarioInicio ?? '',
    horarioFim: item.horarioFim ?? '',
    motivo: item.motivo ?? '',
  });

  const remove = async (item: Indisponibilidade) => {
    if (!item.id || !window.confirm(`Excluir a indisponibilidade de ${format(parseISO(item.data), 'dd/MM/yyyy')}?`)) return;
    try {
      await PortalMinistroService.excluirIndisponibilidade(item.id);
      setMessage({ type: 'success', text: 'Indisponibilidade excluída.' });
      await load();
    } catch (error) {
      setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível excluir a indisponibilidade.') });
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Minhas indisponibilidades</h1>
        <p className="mt-1 text-slate-600 dark:text-slate-400">Informe antecipadamente os dias e horários em que você não poderá servir.</p>
      </div>
      {message && <Alert variant={message.type} onClose={() => setMessage(null)}>{message.text}</Alert>}

      <Card title={form.id ? 'Editar indisponibilidade' : 'Nova indisponibilidade'}>
        <form onSubmit={submit} className="grid gap-4 md:grid-cols-2">
          <label className="text-sm font-semibold text-slate-700 dark:text-slate-200">Data
            <input type="date" required min={dateKey(new Date())} value={form.data} onChange={(event) => setForm({ ...form, data: event.target.value })} className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 dark:border-neutral-600 dark:bg-neutral-800" />
          </label>
          <label className="text-sm font-semibold text-slate-700 dark:text-slate-200">Motivo (opcional)
            <input value={form.motivo} maxLength={255} onChange={(event) => setForm({ ...form, motivo: event.target.value })} className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 dark:border-neutral-600 dark:bg-neutral-800" />
          </label>
          <label className="text-sm font-semibold text-slate-700 dark:text-slate-200">Horário inicial (opcional)
            <input type="time" value={form.horarioInicio} onChange={(event) => setForm({ ...form, horarioInicio: event.target.value })} className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 dark:border-neutral-600 dark:bg-neutral-800" />
          </label>
          <label className="text-sm font-semibold text-slate-700 dark:text-slate-200">Horário final (opcional)
            <input type="time" value={form.horarioFim} onChange={(event) => setForm({ ...form, horarioFim: event.target.value })} className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 dark:border-neutral-600 dark:bg-neutral-800" />
          </label>
          <div className="flex gap-2 md:col-span-2">
            <Button type="submit" isLoading={saving}>{form.id ? 'Salvar alterações' : 'Cadastrar'}</Button>
            {form.id && <Button type="button" variant="secondary" onClick={() => setForm(emptyUnavailability())}>Cancelar</Button>}
          </div>
        </form>
      </Card>

      <Card title="Registros cadastrados">
        {loading ? <div className="flex justify-center py-10"><Spinner /></div> : items.length === 0 ? (
          <p className="py-8 text-center text-slate-500">Nenhuma indisponibilidade cadastrada.</p>
        ) : (
          <div className="divide-y divide-slate-200 dark:divide-neutral-700">
            {items.map((item) => (
              <div key={item.id} className="flex flex-wrap items-center justify-between gap-4 py-4">
                <div>
                  <p className="font-bold text-slate-900 dark:text-white">{format(parseISO(item.data), 'dd/MM/yyyy')}</p>
                  <p className="text-sm text-slate-500">{item.horarioInicio ? `${item.horarioInicio}${item.horarioFim ? `–${item.horarioFim}` : ''}` : 'Dia inteiro'}{item.motivo ? ` · ${item.motivo}` : ''}</p>
                </div>
                <div className="flex gap-2">
                  <Button size="sm" variant="secondary" onClick={() => edit(item)}><Pencil size={16} /> Editar</Button>
                  <Button size="sm" variant="danger" onClick={() => void remove(item)}><Trash2 size={16} /> Excluir</Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
};

export const FeedbackMinistroPage: React.FC = () => {
  const [events, setEvents] = useState<CalendarioMinistroEvento[]>([]);
  const [feedbacks, setFeedbacks] = useState<FeedbackMinistro[]>([]);
  const [selectedEventId, setSelectedEventId] = useState('');
  const [rating, setRating] = useState(10);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const load = useCallback(async () => {
    try {
      const today = new Date();
      const [calendarData, feedbackData] = await Promise.all([
        PortalMinistroService.calendario(dateKey(subDays(today, 365)), dateKey(today)),
        PortalMinistroService.listarFeedbacks(),
      ]);
      setEvents(calendarData);
      setFeedbacks(feedbackData);
    } catch (error) {
      setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível carregar os feedbacks.') });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;
    const today = new Date();
    Promise.all([
      PortalMinistroService.calendario(dateKey(subDays(today, 365)), dateKey(today)),
      PortalMinistroService.listarFeedbacks(),
    ])
      .then(([calendarData, feedbackData]) => {
        if (active) {
          setEvents(calendarData);
          setFeedbacks(feedbackData);
        }
      })
      .catch((error) => {
        if (active) setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível carregar os feedbacks.') });
      })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, []);

  const eligibleEvents = events.filter((event) => event.feedbackDisponivel);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (!selectedEventId) return;
    setSaving(true);
    setMessage(null);
    try {
      await PortalMinistroService.criarFeedback(Number(selectedEventId), rating, comment);
      setSelectedEventId('');
      setRating(10);
      setComment('');
      setMessage({ type: 'success', text: 'Feedback enviado com sucesso.' });
      await load();
    } catch (error) {
      setMessage({ type: 'error', text: getErrorMessage(error, 'Não foi possível enviar o feedback.') });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Meus feedbacks</h1>
        <p className="mt-1 text-slate-600 dark:text-slate-400">Avalie eventos concluídos em que você participou da escala.</p>
      </div>
      {message && <Alert variant={message.type} onClose={() => setMessage(null)}>{message.text}</Alert>}

      <Card title="Enviar feedback">
        {loading ? <div className="flex justify-center py-10"><Spinner /></div> : eligibleEvents.length === 0 ? (
          <p className="text-slate-500">Não há eventos aguardando seu feedback.</p>
        ) : (
          <form onSubmit={submit} className="space-y-5">
            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-200">Evento
              <select required value={selectedEventId} onChange={(event) => setSelectedEventId(event.target.value)} className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 dark:border-neutral-600 dark:bg-neutral-800">
                <option value="">Selecione...</option>
                {eligibleEvents.map((event) => <option key={event.eventoId} value={event.eventoId}>{format(parseISO(event.data), 'dd/MM/yyyy')} — {event.nome}</option>)}
              </select>
            </label>
            <fieldset>
              <legend className="mb-2 text-sm font-semibold text-slate-700 dark:text-slate-200">Nota</legend>
              <div className="flex flex-wrap gap-2">
                {Array.from({ length: 10 }, (_, index) => index + 1).map((value) => (
                  <button key={value} type="button" onClick={() => setRating(value)} className={[
                    'size-10 rounded-lg border text-sm font-bold transition-colors',
                    rating === value ? 'border-primary-600 bg-primary-600 text-white' : 'border-slate-300 text-slate-700 hover:border-primary-500 dark:border-neutral-600 dark:text-slate-200',
                  ].join(' ')} aria-pressed={rating === value}>{value}</button>
                ))}
              </div>
            </fieldset>
            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-200">Comentário (opcional)
              <textarea value={comment} onChange={(event) => setComment(event.target.value)} maxLength={2000} rows={4} className="mt-2 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 dark:border-neutral-600 dark:bg-neutral-800" />
            </label>
            <Button type="submit" isLoading={saving}>Enviar feedback</Button>
          </form>
        )}
      </Card>

      <Card title="Histórico">
        {feedbacks.length === 0 ? <p className="py-8 text-center text-slate-500">Nenhum feedback enviado.</p> : (
          <div className="space-y-4">
            {feedbacks.map((feedback) => (
              <div key={feedback.id} className="rounded-lg border border-slate-200 p-4 dark:border-neutral-700">
                <div className="flex flex-wrap items-start justify-between gap-3">
                  <div><p className="font-bold text-slate-900 dark:text-white">{feedback.eventoNome}</p><p className="text-sm text-slate-500">{format(parseISO(feedback.eventoData), 'dd/MM/yyyy')} · {feedback.eventoHorario}</p></div>
                  <div className="flex gap-2"><Badge variant="warning">Nota {feedback.nota}/10</Badge><Badge variant={feedback.status === 'RESPONDIDO' ? 'success' : 'neutral'}>{feedback.status}</Badge></div>
                </div>
                {feedback.comentario && <p className="mt-3 text-slate-700 dark:text-slate-300">{feedback.comentario}</p>}
                {feedback.resposta && <div className="mt-3 rounded-lg bg-slate-100 p-3 text-sm dark:bg-neutral-800"><strong>Resposta da coordenação:</strong> {feedback.resposta}</div>}
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
};
