// new Date("2026-05-04") é UTC midnight → UTC-3 mostra dia anterior.
// Adicionar T12:00:00 neutraliza o offset sem alterar o dia.
export const parseLocalDate = (dateStr: string): Date =>
  new Date(`${dateStr}T12:00:00`);

export const formatDate = (dateStr: string): string =>
  parseLocalDate(dateStr).toLocaleDateString('pt-BR');

const BRASILIA_TIME_ZONE = 'America/Sao_Paulo';
const EXPLICIT_TIME_ZONE = /(?:Z|[+-]\d{2}:\d{2})$/i;

const brasiliaDateTimeFormatter = new Intl.DateTimeFormat('pt-BR', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit',
  hourCycle: 'h23',
  timeZone: BRASILIA_TIME_ZONE,
});

// O backend persiste os registros de auditoria em UTC, mas os datetimes antigos
// foram serializados sem o sufixo "Z". Sem ele, o navegador os interpreta como
// horário local e deixa a auditoria três horas adiantada em Brasília.
export const parseApiDateTime = (dateTime: string): Date =>
  new Date(EXPLICIT_TIME_ZONE.test(dateTime) ? dateTime : `${dateTime}Z`);

export const formatBrasiliaDateTime = (dateTime: string): string =>
  brasiliaDateTimeFormatter.format(parseApiDateTime(dateTime)).replace(',', '');
