-- ============================================================================
-- DADOS DE TESTE/SEED
-- Sistema de Gestão de Escalas - Paróquias
-- PostgreSQL
-- ============================================================================

-- Conectar ao database
\c escala_ministerial;

-- ============================================================================
-- 1. INSERIR PAROQUIAS
-- ============================================================================

INSERT INTO tb_paroquia (nome, cnpj, endereco, ativo) VALUES
('Paróquia São José', '99.999.999/0001-00', 'Rua das Flores, 100 - Araraquara, SP', TRUE),
('Paróquia Santa Maria', '99.999.999/0001-01', 'Avenida Principal, 200 - Araraquara, SP', TRUE);

-- ============================================================================
-- 2. INSERIR PESSOAS: COORDENADORES
-- ============================================================================

INSERT INTO tb_pessoa (nome, email, telefone, data_nascimento, observacoes, ativo, tipo_pessoa, id_paroquia) VALUES
('Pe. João Silva', 'joao@paroquia.com', '16999990000', '1970-03-15', 'Coordenador Geral', TRUE, 'COORDENADOR', 1),
('Irmã Rosa', 'rosa@paroquia.com', '16999990001', '1965-07-22', 'Coordenadora de Eventos', TRUE, 'COORDENADOR', 1),
('Pe. Paulo Santos', 'paulo@paroquia.com', '16999990002', '1975-05-10', 'Coordenador da Paróquia Santa Maria', TRUE, 'COORDENADOR', 2);

-- ============================================================================
-- 3. INSERIR COORDENADORES (especialização)
-- ============================================================================

INSERT INTO tb_coordenador (id_pessoa, ultimo_acesso, horas_atuacao, permissoes) VALUES
(1, NOW(), 150.50, 'ADMIN,ESCALAS,FEEDBACK,AUDITORIA'),
(2, NOW(), 80.00, 'EVENTOS,MINISTROS,FEEDBACK'),
(3, NOW(), 120.00, 'ADMIN,ESCALAS,FEEDBACK');

-- ============================================================================
-- 4. INSERIR PESSOAS: MINISTROS
-- ============================================================================

INSERT INTO tb_pessoa (nome, email, telefone, data_nascimento, observacoes, ativo, tipo_pessoa, id_paroquia) VALUES
-- Ministros da Paróquia 1
('Maria Silva', 'maria@email.com', '16988880001', '1985-06-20', 'Excelente ministra', TRUE, 'MINISTRO', 1),
('João Santos', 'joao.santos@email.com', '16988880002', '1980-12-05', 'Ativo há 5 anos', TRUE, 'MINISTRO', 1),
('Ana Oliveira', 'ana@email.com', '16988880003', '1990-02-14', 'Nova ministra - treinamento', TRUE, 'MINISTRO', 1),
('Carlos Ferreira', 'carlos@email.com', '16988880004', '1972-08-30', 'Veterano - pouca disponibilidade', FALSE, 'MINISTRO', 1),
('Beatriz Costa', 'beatriz@email.com', '16988880005', '1988-11-11', 'Muito responsável', TRUE, 'MINISTRO', 1),
('Lucas Almeida', 'lucas@email.com', '16988880006', '1995-04-18', 'Recém chegado', TRUE, 'MINISTRO', 1),

-- Ministros da Paróquia 2
('Felipe Oliveira', 'felipe@email.com', '16988880007', '1982-09-25', 'Coordenador local', TRUE, 'MINISTRO', 2),
('Mariana Costa', 'mariana@email.com', '16988880008', '1993-01-31', 'Docente na paróquia', TRUE, 'MINISTRO', 2);

-- ============================================================================
-- 5. INSERIR MINISTROS (especialização)
-- ============================================================================

INSERT INTO tb_ministro (id_pessoa, data_ultima_confissao, visitas_infermo, status_curso, funcao, escalas_mes) VALUES
(4, '2026-04-10', TRUE, TRUE, 'EUCARISTIA', 3),
(5, '2026-04-12', FALSE, TRUE, 'LEITURA', 2),
(6, '2026-03-20', TRUE, FALSE, 'ACOLHIMENTO', 1),
(7, NULL, FALSE, FALSE, 'MUSICA', 0),
(8, '2026-04-15', TRUE, TRUE, 'CATEQUESE', 4),
(9, '2026-04-01', FALSE, FALSE, 'ADORACAO', 2),
(10, '2026-04-10', TRUE, TRUE, 'EUCARISTIA', 3),
(11, '2026-04-14', TRUE, FALSE, 'LEITURA', 2);

-- ============================================================================
-- 6. INSERIR APTIDOES DOS MINISTROS
-- ============================================================================

INSERT INTO tb_aptidao_ministro (id_ministro, tipo_evento) VALUES
-- Maria Silva (id_ministro = 1)
(1, 'MISSA_PAROQUIAL'),
(1, 'MISSA_ESPECIAL'),
(1, 'BATIZADO'),

-- João Santos (id_ministro = 2)
(2, 'MISSA_PAROQUIAL'),
(2, 'ADORACAO'),

-- Ana Oliveira (id_ministro = 3)
(3, 'MISSA_PAROQUIAL'),
(3, 'MISSA_ESPECIAL'),

-- Carlos Ferreira (id_ministro = 4) - inativo
(4, 'RETIRO'),

-- Beatriz Costa (id_ministro = 5)
(5, 'MISSA_PAROQUIAL'),
(5, 'MISSA_ESPECIAL'),
(5, 'CASAMENTO'),

-- Lucas Almeida (id_ministro = 6)
(6, 'MISSA_PAROQUIAL'),

-- Felipe Oliveira (id_ministro = 7)
(7, 'MISSA_PAROQUIAL'),
(7, 'MISSA_ESPECIAL'),
(7, 'RETIRO'),

-- Mariana Costa (id_ministro = 8)
(8, 'MISSA_PAROQUIAL'),
(8, 'ADORACAO'),
(8, 'BATIZADO');

-- ============================================================================
-- 7. INSERIR INDISPONIBILIDADES
-- ============================================================================

INSERT INTO tb_indisponibilidade (id_ministro, data_indisponibilidade, horario_inicio, horario_fim, motivo, tipo_indisponibilidade) VALUES
-- Maria Silva indisponível 20/04/2026
(1, '2026-04-20', '08:00:00', '10:00:00', 'Consulta médica', 'PROGRAMADA'),

-- João Santos indisponível 25/04/2026
(2, '2026-04-25', '09:00:00', NULL, 'Viagem inesperada', 'SURPRESA'),

-- Beatriz Costa indisponível
(5, '2026-04-22', '14:00:00', '18:00:00', 'Trabalho extra', 'PROGRAMADA');

-- ============================================================================
-- 8. INSERIR DISPONIBILIDADES RECORRENTES
-- ============================================================================

INSERT INTO tb_disponibilidade_recorrente (id_ministro, dia_semana, horario_inicio, horario_fim) VALUES
-- Maria Silva: Terças e Domingos
(1, 2, '19:00:00', '21:00:00'),  -- Terça
(1, 0, '08:00:00', '12:00:00'),  -- Domingo

-- João Santos: Domingos
(2, 0, '08:00:00', '12:00:00'),  -- Domingo

-- Beatriz Costa: Terças e Quintas
(5, 2, '19:00:00', '21:00:00'),  -- Terça
(5, 4, '19:30:00', '21:30:00'),  -- Quinta

-- Lucas Almeida: Domingos
(6, 0, '08:00:00', '12:00:00'),  -- Domingo

-- Felipe Oliveira: Terças, Quintas e Domingos
(7, 2, '18:30:00', '20:30:00'),  -- Terça
(7, 4, '18:30:00', '20:30:00'),  -- Quinta
(7, 0, '08:00:00', '12:00:00'),  -- Domingo

-- Mariana Costa: Domingos e Sábados
(8, 0, '08:00:00', '12:00:00'),  -- Domingo
(8, 6, '18:00:00', '20:00:00');  -- Sábado

-- ============================================================================
-- 9. INSERIR EVENTOS
-- ============================================================================

INSERT INTO tb_evento (id_paroquia, nome, data_evento, horario_evento, tipo_evento, local, max_ministros, cancelado) VALUES
-- Paróquia 1
(1, 'Missa Dominical - 19/04/2026', '2026-04-19', '09:00:00', 'MISSA_PAROQUIAL', 'Igreja Matriz', 6, FALSE),
(1, 'Missa Dominical - 26/04/2026', '2026-04-26', '09:00:00', 'MISSA_PAROQUIAL', 'Igreja Matriz', 6, FALSE),
(1, 'Retiro Pascoa', '2026-04-22', '10:00:00', 'RETIRO', 'Salão Paroquial', 12, FALSE),
(1, 'Batizado - João Silva', '2026-05-04', '10:30:00', 'BATIZADO', 'Igreja Matriz', 8, FALSE),
(1, 'Casamento - Maria e João', '2026-05-10', '14:00:00', 'CASAMENTO', 'Igreja Matriz', 10, FALSE),
(1, 'Adoração Noturna', '2026-04-19', '19:00:00', 'ADORACAO', 'Igreja Matriz', 4, FALSE),

-- Paróquia 2
(2, 'Missa Dominical - 19/04/2026', '2026-04-19', '10:00:00', 'MISSA_PAROQUIAL', 'Igreja Santa Maria', 5, FALSE),
(2, 'Missa Especial - Dia de Finados', '2026-11-02', '08:00:00', 'MISSA_ESPECIAL', 'Igreja Santa Maria', 8, FALSE);

-- ============================================================================
-- 10. INSERIR PERIODOS DE ESCALA
-- ============================================================================

INSERT INTO tb_periodo_escala (id_paroquia, data_inicio, data_fim, descricao, aprovado, id_coordenador_aprovou, data_aprovacao) VALUES
(1, '2026-04-19', '2026-04-26', 'Semana de 19-26/04/2026', TRUE, 1, '2026-04-15 14:30:00'),
(1, '2026-04-26', '2026-05-10', 'Período Pascoa 26/04 a 10/05', FALSE, NULL, NULL),
(2, '2026-04-19', '2026-04-26', 'Semana de 19-26/04/2026', TRUE, 3, '2026-04-16 10:00:00');

-- ============================================================================
-- 11. INSERIR ESCALAS
-- ============================================================================

INSERT INTO tb_escala (id_evento, id_periodo, data_atribuicao, status_escala, observacao) VALUES
-- Escala para Missa Dominical 19/04 (Paróquia 1)
(1, 1, '2026-04-15 10:00:00', 'APROVADA', 'Escala confirmada'),

-- Escala para Adoração 19/04
(6, 1, '2026-04-15 11:00:00', 'PROPOSTA', 'Aguardando confirmação'),

-- Escala para Retiro
(3, 2, '2026-04-16 09:00:00', 'PROPOSTA', 'Evento importante - aguardando confirmações'),

-- Escala para Missa Dominical 26/04
(2, 1, '2026-04-15 10:30:00', 'APROVADA', 'Escala confirmada'),

-- Escala para Batizado
(4, 2, '2026-04-17 14:00:00', 'PROPOSTA', 'Aguardando confirmações dos padrinhos'),

-- Escala para Casamento
(5, 2, '2026-04-19 09:00:00', 'PROPOSTA', 'Em negociação com a família'),

-- Escala para Paróquia 2 - Missa 19/04
(7, 3, '2026-04-16 14:00:00', 'APROVADA', 'Confirmado com os ministros'),

-- Escala para Missa de Finados (preparação com antecedência)
(8, NULL, '2026-04-19 15:00:00', 'PROPOSTA', 'Planejamento antecipado para novembro');

-- ============================================================================
-- 12. INSERIR ESCALAS_MINISTRO (Confirmações por ministro)
-- ============================================================================

INSERT INTO tb_escala_ministro (id_escala, id_ministro, status_confirmacao, data_confirmacao) VALUES
-- Escala 1 (Missa 19/04 - Paróquia 1) - 3 ministros
(1, 1, 'CONFIRMADO', '2026-04-15 10:15:00'),
(1, 2, 'CONFIRMADO', '2026-04-15 10:20:00'),
(1, 3, 'PENDENTE', NULL),

-- Escala 2 (Adoração) - 1 ministro
(2, 6, 'CONFIRMADO', '2026-04-15 11:30:00'),

-- Escala 3 (Retiro) - 4 ministros
(3, 1, 'PENDENTE', NULL),
(3, 5, 'PENDENTE', NULL),
(3, 7, 'CONFIRMADO', '2026-04-16 09:30:00'),
(3, 8, 'PENDENTE', NULL),

-- Escala 4 (Missa 26/04) - 2 ministros
(4, 5, 'CONFIRMADO', '2026-04-15 10:45:00'),
(4, 8, 'PENDENTE', NULL),

-- Escala 7 (Missa Paróquia 2) - 2 ministros
(7, 7, 'CONFIRMADO', '2026-04-16 14:15:00'),
(7, 8, 'CONFIRMADO', '2026-04-16 14:30:00');

-- ============================================================================
-- 13. INSERIR FEEDBACK
-- ============================================================================

INSERT INTO tb_feedback (id_ministro, id_evento, nota, comentario, status_feedback) VALUES
-- Feedback da Missa 19/04
(1, 1, 5, 'Excelente participação, muito engajado!', 'RESPONDIDO'),
(2, 1, 4, 'Boa leitura, poderia melhorar o tom', 'PENDENTE'),

-- Feedback do Retiro (sem feedback ainda)
-- Feedback de outro evento
(5, 2, 4, 'Participação ativa e construtiva', 'RESPONDIDO');

-- ============================================================================
-- 14. INSERIR LOG_AUDITORIA
-- ============================================================================

INSERT INTO tb_log_auditoria (id_paroquia, entidade, id_registro, tipo_acao, status_anterior, status_novo, id_usuario, descricao) VALUES
(1, 'ESCALA', 1, 'CREATE', NULL, 'PROPOSTA', 1, 'Escala criada para Missa 19/04'),
(1, 'ESCALA', 1, 'UPDATE', 'PROPOSTA', 'APROVADA', 1, 'Escala aprovada após confirmarões'),
(1, 'ESCALA_MINISTRO', 1, 'CONFIRMACAO', 'PENDENTE', 'CONFIRMADO', NULL, 'Ministro confirmou presença'),
(1, 'FEEDBACK', 1, 'CREATE', NULL, 'PENDENTE', NULL, 'Feedback recebido após evento');

-- ============================================================================
-- FIM DO SCRIPT
-- ============================================================================
