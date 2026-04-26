// Escala Ministerial — Mock Data
window.AppData = (function () {
  const ministros = [
    { id: 1, nome: 'João Silva', funcao: 'Leitor', telefone: '(11) 99876-5432', email: 'joao.silva@paroquia.org', ativo: true },
    { id: 2, nome: 'Maria Santos', funcao: 'Música', telefone: '(11) 98765-4321', email: 'maria.santos@paroquia.org', ativo: true },
    { id: 3, nome: 'Pedro Oliveira', funcao: 'Acólito', telefone: '(11) 97654-3210', email: 'pedro.oliveira@paroquia.org', ativo: true },
    { id: 4, nome: 'Ana Costa', funcao: 'Leitor', telefone: '(11) 96543-2109', email: 'ana.costa@paroquia.org', ativo: true },
    { id: 5, nome: 'Carlos Ferreira', funcao: 'Ministro da Eucaristia', telefone: '(11) 95432-1098', email: 'carlos.f@paroquia.org', ativo: true },
    { id: 6, nome: 'Lucia Mendes', funcao: 'Coordenadora', telefone: '(11) 94321-0987', email: 'lucia.m@paroquia.org', ativo: true },
    { id: 7, nome: 'Roberto Nunes', funcao: 'Acólito', telefone: '(11) 93210-9876', email: 'roberto.n@paroquia.org', ativo: true },
    { id: 8, nome: 'Fernanda Lima', funcao: 'Música', telefone: '(11) 92109-8765', email: 'fernanda.l@paroquia.org', ativo: false },
    { id: 9, nome: 'Antônio Rocha', funcao: 'Ministro da Eucaristia', telefone: '(11) 91098-7654', email: 'antonio.r@paroquia.org', ativo: true },
    { id: 10, nome: 'Beatriz Alves', funcao: 'Leitor', telefone: '(11) 90987-6543', email: 'beatriz.a@paroquia.org', ativo: true },
  ];

  const indisponibilidades = [
    { ministroId: 3, data: '2026-04-27', motivo: 'Viagem' },
    { ministroId: 8, data: '2026-04-27', motivo: 'Doença' },
    { ministroId: 8, data: '2026-04-28', motivo: 'Doença' },
  ];

  const eventos = [
    { id: 1, nome: 'Missa Dominical', data: '2026-04-27', horario: '09:00', local: 'Igreja Matriz', tipo: 'Missa', capacidade: 8, status: 'ATIVO' },
    { id: 2, nome: 'Batizado Coletivo', data: '2026-04-28', horario: '11:00', local: 'Igreja Matriz', tipo: 'Batizado', capacidade: 6, status: 'ATIVO' },
    { id: 3, nome: 'Retiro de Jovens', data: '2026-05-03', horario: '08:00', local: 'Centro Paroquial', tipo: 'Retiro', capacidade: 15, status: 'ATIVO' },
    { id: 4, nome: 'Missa de Primeira Comunhão', data: '2026-05-10', horario: '10:00', local: 'Igreja Matriz', tipo: 'Missa', capacidade: 10, status: 'ATIVO' },
    { id: 5, nome: 'Via Sacra', data: '2026-04-03', horario: '19:00', local: 'Adro da Igreja', tipo: 'Celebração', capacidade: 12, status: 'CONCLUIDO' },
    { id: 6, nome: 'Missa de Corpus Christi', data: '2026-06-04', horario: '09:00', local: 'Igreja Matriz', tipo: 'Missa', capacidade: 10, status: 'ATIVO' },
  ];

  const escalas = [
    { id: 1, eventoId: 1, eventoNome: 'Missa Dominical', data: '2026-04-27', status: 'APROVADA', ministros: [
      { ministroId: 1, nome: 'João Silva', funcao: 'Leitor', confirmado: true },
      { ministroId: 2, nome: 'Maria Santos', funcao: 'Música', confirmado: true },
      { ministroId: 4, nome: 'Ana Costa', funcao: 'Leitor', confirmado: false },
      { ministroId: 5, nome: 'Carlos Ferreira', funcao: 'Ministro da Eucaristia', confirmado: true },
    ]},
    { id: 2, eventoId: 2, eventoNome: 'Batizado Coletivo', data: '2026-04-28', status: 'PROPOSTA', ministros: [
      { ministroId: 2, nome: 'Maria Santos', funcao: 'Música', confirmado: false },
      { ministroId: 3, nome: 'Pedro Oliveira', funcao: 'Acólito', confirmado: false },
      { ministroId: 6, nome: 'Lucia Mendes', funcao: 'Coordenadora', confirmado: false },
    ]},
    { id: 3, eventoId: 3, eventoNome: 'Retiro de Jovens', data: '2026-05-03', status: 'PROPOSTA', ministros: [
      { ministroId: 1, nome: 'João Silva', funcao: 'Leitor', confirmado: false },
      { ministroId: 4, nome: 'Ana Costa', funcao: 'Leitor', confirmado: false },
      { ministroId: 7, nome: 'Roberto Nunes', funcao: 'Acólito', confirmado: false },
    ]},
    { id: 4, eventoId: 5, eventoNome: 'Via Sacra', data: '2026-04-03', status: 'CONFIRMADA', ministros: [
      { ministroId: 1, nome: 'João Silva', funcao: 'Leitor', confirmado: true },
      { ministroId: 2, nome: 'Maria Santos', funcao: 'Música', confirmado: true },
      { ministroId: 5, nome: 'Carlos Ferreira', funcao: 'Ministro da Eucaristia', confirmado: true },
      { ministroId: 6, nome: 'Lucia Mendes', funcao: 'Coordenadora', confirmado: true },
    ]},
    { id: 5, eventoId: 4, eventoNome: 'Missa de Primeira Comunhão', data: '2026-05-10', status: 'CANCELADA', ministros: [] },
  ];

  const feedbacks = [
    { id: 1, ministroId: 1, ministroNome: 'João Silva', eventoNome: 'Via Sacra', nota: 9, comentario: 'Excelente leitura, muito bem preparado. Conduziu com serenidade e clareza.', resposta: null, data: '2026-04-04', respondido: false },
    { id: 2, ministroId: 2, ministroNome: 'Maria Santos', eventoNome: 'Via Sacra', nota: 8, comentario: 'Boa condução musical. O coro respondeu muito bem.', resposta: 'Obrigado pela dedicação, Maria! Continue assim.', data: '2026-04-04', respondido: true },
    { id: 3, ministroId: 5, ministroNome: 'Carlos Ferreira', eventoNome: 'Via Sacra', nota: 10, comentario: 'Impecável em todos os momentos. Referência para o grupo.', resposta: null, data: '2026-04-05', respondido: false },
    { id: 4, ministroId: 3, ministroNome: 'Pedro Oliveira', eventoNome: 'Via Sacra', nota: 7, comentario: 'Chegou um pouco atrasado. No geral cumpriu bem o papel.', resposta: null, data: '2026-04-05', respondido: false },
    { id: 5, ministroId: 6, ministroNome: 'Lucia Mendes', eventoNome: 'Via Sacra', nota: 9, comentario: 'Organização exemplar antes e durante o evento.', resposta: 'Fico feliz, Lucia! O resultado foi fruto do seu empenho.', data: '2026-04-06', respondido: true },
  ];

  const auditoria = [
    { id: 1, acao: 'APROVAÇÃO', entidade: 'Escala', descricao: 'Escala aprovada para Missa Dominical', usuario: 'Coordenador', data: '2026-04-24 14:45' },
    { id: 2, acao: 'CRIAÇÃO', entidade: 'Escala', descricao: 'Escala gerada para Missa Dominical', usuario: 'Sistema', data: '2026-04-24 14:32' },
    { id: 3, acao: 'CRIAÇÃO', entidade: 'Ministro', descricao: 'Ministro adicionado: Roberto Nunes', usuario: 'Coordenador', data: '2026-04-23 09:10' },
    { id: 4, acao: 'CANCELAMENTO', entidade: 'Escala', descricao: 'Escala cancelada: Missa de Primeira Comunhão', usuario: 'Coordenador', data: '2026-04-22 16:00' },
    { id: 5, acao: 'EDIÇÃO', entidade: 'Evento', descricao: 'Evento atualizado: Batizado Coletivo — capacidade alterada para 6', usuario: 'Coordenador', data: '2026-04-21 11:30' },
    { id: 6, acao: 'CONFIRMAÇÃO', entidade: 'Escala', descricao: 'Escala confirmada: Via Sacra', usuario: 'Sistema', data: '2026-04-03 20:15' },
    { id: 7, acao: 'CRIAÇÃO', entidade: 'Evento', descricao: 'Evento criado: Retiro de Jovens', usuario: 'Coordenador', data: '2026-04-20 08:45' },
    { id: 8, acao: 'EXCLUSÃO', entidade: 'Ministro', descricao: 'Indisponibilidade registrada: Pedro Oliveira em 27/04', usuario: 'Coordenador', data: '2026-04-19 15:20' },
    { id: 9, acao: 'CRIAÇÃO', entidade: 'Feedback', descricao: 'Feedback registrado para João Silva — Via Sacra (nota 9)', usuario: 'Coordenador', data: '2026-04-04 10:00' },
  ];

  return { ministros, indisponibilidades, eventos, escalas, feedbacks, auditoria };
})();
