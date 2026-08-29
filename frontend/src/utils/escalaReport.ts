import type { CellObject, Sheet, SheetData } from 'write-excel-file/browser';
import type { Escala, EscalaMinistro, Evento } from '@/types';

type ReportFileContent = File | Blob | ArrayBuffer;

export interface EscalaReportPeriod {
  startDate: string;
  endDate: string;
}

export interface EscalaReportTotals {
  escalas: number;
  eventos: number;
  alocacoesAtivas: number;
  confirmadas: number;
  pendentes: number;
  canceladas: number;
  vagasAbertas: number;
}

export interface EscalaWorkbook {
  fileName: string;
  sheets: Sheet<ReportFileContent>[];
  totals: EscalaReportTotals;
}

export interface EscalaComEvento {
  escala: Escala;
  evento: Evento;
}

const HEADER_COLOR = '#173F5F';
const BORDER_COLOR = '#CBD5E1';
const ALTERNATE_ROW_COLOR = '#F8FAFC';
const WHITE = '#FFFFFF';

const WEEKDAY_FORMATTER = new Intl.DateTimeFormat('pt-BR', { weekday: 'long' });

const parseReportDate = (date: string): Date => new Date(`${date}T12:00:00`);

const capitalize = (value: string): string =>
  value ? `${value.charAt(0).toUpperCase()}${value.slice(1)}` : value;

const resolveEvento = (escala: Escala, eventosById: Map<number, Evento>): Evento | undefined =>
  escala.evento ?? eventosById.get(escala.eventoId);

const getCurrentAssignments = (escala: Escala): EscalaMinistro[] =>
  escala.escalaMinistros.filter((item) => !item.substituido);

const formatDay = (date: string): string => {
  const parsed = parseReportDate(date);
  const weekday = capitalize(WEEKDAY_FORMATTER.format(parsed));
  return `${weekday}, ${parsed.toLocaleDateString('pt-BR')}`;
};

const splitEventTime = (time: string): { start: string; end: string } => {
  const parts = time.split(/\s*(?:-|–|—|às)\s*/i).filter(Boolean);
  return {
    start: parts[0] ?? '—',
    end: parts[1] ?? '—',
  };
};

const formatMinister = (assignment: EscalaMinistro): string => {
  return assignment.ministroNome ?? `Ministro #${assignment.ministroId}`;
};

const headerCell = (value: string): CellObject => ({
  value,
  backgroundColor: HEADER_COLOR,
  textColor: WHITE,
  fontWeight: 'bold',
  align: 'center',
  alignVertical: 'center',
  borderColor: HEADER_COLOR,
  borderStyle: 'thin',
  wrap: true,
  height: 28,
});

const dataCell = (
  value: CellObject['value'],
  rowIndex: number,
  extra: Partial<CellObject> = {},
): CellObject => ({
  value,
  backgroundColor: rowIndex % 2 === 0 ? WHITE : ALTERNATE_ROW_COLOR,
  borderColor: BORDER_COLOR,
  borderStyle: 'thin',
  alignVertical: 'center',
  wrap: true,
  height: 26,
  ...extra,
});

const buildSimpleSheet = (entries: EscalaComEvento[]): Sheet<ReportFileContent> => {
  const ministerColumnCount = Math.max(
    1,
    ...entries.map(({ escala }) => getCurrentAssignments(escala).length),
  );
  const headers = [
    'Dia',
    'Horário',
    'Término',
    'Evento',
    ...Array.from({ length: ministerColumnCount }, (_, index) => `Ministro ${index + 1}`),
  ];
  const rows: SheetData = [headers.map(headerCell)];

  entries.forEach(({ escala, evento }, rowIndex) => {
    const assignments = getCurrentAssignments(escala);
    const { start, end } = splitEventTime(evento.horario);
    const ministerCells = Array.from({ length: ministerColumnCount }, (_, index) =>
      dataCell(assignments[index] ? formatMinister(assignments[index]) : '', rowIndex),
    );

    rows.push([
      dataCell(formatDay(evento.data), rowIndex, { fontWeight: 'bold' }),
      dataCell(start, rowIndex, { align: 'center', format: '@' }),
      dataCell(end, rowIndex, { align: 'center', format: '@' }),
      dataCell(evento.nome, rowIndex, { fontWeight: 'bold' }),
      ...ministerCells,
    ]);
  });

  return {
    data: rows,
    sheet: 'Escalas',
    columns: [
      { width: 23 },
      { width: 11 },
      { width: 11 },
      { width: 31 },
      ...Array.from({ length: ministerColumnCount }, () => ({ width: 28 })),
    ],
    orientation: 'landscape',
    stickyRowsCount: 1,
    stickyColumnsCount: 4,
    showGridLines: false,
    zoomScale: 0.8,
  };
};

export const getEscalasInPeriod = (
  escalas: Escala[],
  eventos: Evento[],
  { startDate, endDate }: EscalaReportPeriod,
): EscalaComEvento[] => {
  const eventosById = new Map(eventos.flatMap((evento) =>
    evento.id == null ? [] : [[evento.id, evento] as const],
  ));

  return escalas
    .map((escala) => ({ escala, evento: resolveEvento(escala, eventosById) }))
    .filter((entry): entry is EscalaComEvento => Boolean(entry.evento))
    .filter(({ evento }) => evento.data >= startDate && evento.data <= endDate)
    .sort((a, b) =>
      a.evento.data.localeCompare(b.evento.data)
      || a.evento.horario.localeCompare(b.evento.horario)
      || a.evento.nome.localeCompare(b.evento.nome, 'pt-BR'),
    );
};

export const getDefaultEscalaReportPeriod = (
  escalas: Escala[],
  eventos: Evento[],
): EscalaReportPeriod => {
  const eventosById = new Map(eventos.flatMap((evento) =>
    evento.id == null ? [] : [[evento.id, evento] as const],
  ));
  const scaleDates = escalas
    .map((escala) => resolveEvento(escala, eventosById)?.data)
    .filter((date): date is string => Boolean(date))
    .sort();
  const fallbackDates = eventos.map((evento) => evento.data).filter(Boolean).sort();
  const dates = scaleDates.length > 0 ? scaleDates : fallbackDates;
  const today = new Date().toLocaleDateString('sv-SE', { timeZone: 'America/Sao_Paulo' });

  return {
    startDate: dates[0] ?? today,
    endDate: dates[dates.length - 1] ?? today,
  };
};

export const buildEscalaWorkbook = (
  escalas: Escala[],
  eventos: Evento[],
  period: EscalaReportPeriod,
): EscalaWorkbook => {
  if (!period.startDate || !period.endDate) {
    throw new Error('Informe as datas inicial e final do relatório.');
  }
  if (period.startDate > period.endDate) {
    throw new Error('A data inicial não pode ser posterior à data final.');
  }

  const entries = getEscalasInPeriod(escalas, eventos, period);
  if (entries.length === 0) {
    throw new Error('Nenhuma escala foi encontrada no período informado.');
  }

  const assignments = entries.flatMap(({ escala }) => getCurrentAssignments(escala));
  const activeEntries = entries.filter(({ escala }) => escala.status !== 'CANCELADA');
  const activeAssignments = activeEntries.flatMap(({ escala }) => getCurrentAssignments(escala));
  const capacity = activeEntries.reduce((sum, { evento }) => sum + evento.maxMinistros, 0);
  const totals: EscalaReportTotals = {
    escalas: entries.length,
    eventos: new Set(entries.map(({ evento }) =>
      evento.id ?? `${evento.data}-${evento.horario}-${evento.nome}`,
    )).size,
    alocacoesAtivas: assignments.length,
    confirmadas: assignments.filter((item) => item.confirmacaoMinistro).length,
    pendentes: assignments.filter((item) => !item.confirmacaoMinistro).length,
    canceladas: entries.length - activeEntries.length,
    vagasAbertas: Math.max(capacity - activeAssignments.length, 0),
  };

  return {
    fileName: `relatorio-escalas_${period.startDate}_a_${period.endDate}.xlsx`,
    sheets: [buildSimpleSheet(entries)],
    totals,
  };
};

export const downloadEscalaReport = async (
  escalas: Escala[],
  eventos: Evento[],
  period: EscalaReportPeriod,
): Promise<EscalaWorkbook> => {
  const workbook = buildEscalaWorkbook(escalas, eventos, period);
  const { default: writeXlsxFile } = await import('write-excel-file/browser');
  await writeXlsxFile(workbook.sheets, { fontFamily: 'Aptos', fontSize: 10 }).toFile(workbook.fileName);
  return workbook;
};
