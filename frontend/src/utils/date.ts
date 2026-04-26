// new Date("2026-05-04") é UTC midnight → UTC-3 mostra dia anterior.
// Adicionar T12:00:00 neutraliza o offset sem alterar o dia.
export const parseLocalDate = (dateStr: string): Date =>
  new Date(`${dateStr}T12:00:00`);

export const formatDate = (dateStr: string): string =>
  parseLocalDate(dateStr).toLocaleDateString('pt-BR');
