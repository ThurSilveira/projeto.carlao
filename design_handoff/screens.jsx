// Escala Ministerial — All Screens (theme-aware)

// ── Dashboard ─────────────────────────────────────────────────────────────────
function DashboardScreen({ data, onNavigate }) {
  const C = useColors();
  const ativos = data.ministros.filter(m => m.ativo).length;
  const eventosAtivos = data.eventos.filter(e => e.status === 'ATIVO').length;
  const aprovadas = data.escalas.filter(e => e.status === 'APROVADA' || e.status === 'CONFIRMADA').length;
  const notas = data.feedbacks.map(f => f.nota);
  const notaMedia = notas.length ? (notas.reduce((a, b) => a + b, 0) / notas.length).toFixed(1) : '—';
  const pendentes = data.feedbacks.filter(f => !f.respondido).length;

  const kpis = [
    { label: 'Ministros Ativos',       value: ativos,            icon: 'ministers', color: C.primary,   bg: C.primaryLight,   screen: 'ministros' },
    { label: 'Eventos este Mês',        value: eventosAtivos,     icon: 'events',    color: C.secondary, bg: C.secondaryLight, screen: 'eventos'   },
    { label: 'Escalas Aprovadas',       value: aprovadas,         icon: 'scales',    color: '#7C3AED',   bg: '#EDE9FE',        screen: 'escalas'   },
    { label: 'Nota Média',              value: notaMedia,         icon: 'feedback',  color: C.amber,     bg: C.amberLight,     screen: 'feedback'  },
    { label: 'Feedbacks Pendentes',     value: pendentes,         icon: 'reply',     color: C.error,     bg: C.errorLight,     screen: 'feedback'  },
    { label: 'Registros de Auditoria',  value: data.auditoria.length, icon: 'audit', color: C.info,     bg: C.infoLight,      screen: 'auditoria' },
  ];

  const proximosEventos = data.eventos
    .filter(e => e.status === 'ATIVO')
    .sort((a, b) => a.data.localeCompare(b.data))
    .slice(0, 3);

  const escalasRecentes = data.escalas.slice(0, 4);

  return (
    <div style={{ padding: '0 0 24px' }}>
      {/* Header */}
      <div style={{
        padding: '28px 20px 20px',
        background: `linear-gradient(135deg, ${C.primary} 0%, ${C.primary}cc 100%)`,
        borderBottomLeftRadius: 24, borderBottomRightRadius: 24, marginBottom: 24,
      }}>
        <p style={{ margin: '0 0 4px', fontSize: 13, color: 'rgba(255,255,255,0.65)', fontFamily: "'Nunito', sans-serif", fontWeight: 500 }}>Bem-vindo de volta</p>
        <h1 style={{ margin: '0 0 2px', fontSize: 22, fontWeight: 800, color: '#fff', fontFamily: "'Playfair Display', serif" }}>Painel de Controle</h1>
        <p style={{ margin: 0, fontSize: 13, color: 'rgba(255,255,255,0.6)', fontFamily: "'Nunito', sans-serif" }}>
          Escala Ministerial • {new Date().toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long' })}
        </p>
      </div>

      <div style={{ padding: '0 16px' }}>
        {/* KPI Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 12, marginBottom: 28 }}>
          {kpis.map((k, i) => (
            <Card key={i} onClick={() => onNavigate(k.screen)} style={{ padding: '16px' }}>
              <div style={{ marginBottom: 10 }}>
                <div style={{ width: 38, height: 38, borderRadius: 10, background: k.bg, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name={k.icon} size={20} color={k.color} />
                </div>
              </div>
              <div style={{ fontSize: 26, fontWeight: 800, color: C.onSurface, fontFamily: "'Nunito', sans-serif", lineHeight: 1 }}>{k.value}</div>
              <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 4, fontWeight: 500 }}>{k.label}</div>
            </Card>
          ))}
        </div>

        {/* Próximos Eventos */}
        <div style={{ marginBottom: 28 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
            <h2 style={{ margin: 0, fontSize: 15, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>Próximos Eventos</h2>
            <button onClick={() => onNavigate('eventos')} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 13, color: C.primary, fontFamily: "'Nunito', sans-serif", fontWeight: 600 }}>Ver todos</button>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {proximosEventos.map(ev => (
              <Card key={ev.id} style={{ padding: '14px 16px' }} onClick={() => onNavigate('eventos')}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                  <div style={{ width: 44, height: 44, borderRadius: 10, background: C.primaryLight, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                    <span style={{ fontSize: 16, fontWeight: 800, color: C.primary, lineHeight: 1, fontFamily: "'Nunito', sans-serif" }}>{ev.data.split('-')[2]}</span>
                    <span style={{ fontSize: 9, fontWeight: 600, color: C.primary, fontFamily: "'Nunito', sans-serif", textTransform: 'uppercase' }}>
                      {new Date(ev.data + 'T12:00:00').toLocaleDateString('pt-BR', { month: 'short' })}
                    </span>
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 14, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif", whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{ev.nome}</div>
                    <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 2 }}>{ev.horario} • {ev.local}</div>
                  </div>
                  <span style={{ fontSize: 12, fontWeight: 600, color: C.primary, background: C.primaryLight, padding: '3px 8px', borderRadius: 6, fontFamily: "'Nunito', sans-serif" }}>{ev.tipo}</span>
                </div>
              </Card>
            ))}
          </div>
        </div>

        {/* Escalas Recentes */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
            <h2 style={{ margin: 0, fontSize: 15, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>Escalas Recentes</h2>
            <button onClick={() => onNavigate('escalas')} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 13, color: C.primary, fontFamily: "'Nunito', sans-serif", fontWeight: 600 }}>Ver todas</button>
          </div>
          <Card hover={false}>
            {escalasRecentes.map((esc, i) => (
              <React.Fragment key={esc.id}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px', gap: 12 }}>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif", whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{esc.eventoNome}</div>
                    <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 1 }}>{fmtDate(esc.data)} • {esc.ministros.length} ministro(s)</div>
                  </div>
                  <StatusBadge status={esc.status} />
                </div>
                {i < escalasRecentes.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </Card>
        </div>
      </div>
    </div>
  );
}
window.DashboardScreen = DashboardScreen;

// ── Ministros ─────────────────────────────────────────────────────────────────
function MinistrosScreen({ data, setData, isDesktop, showToast }) {
  const C = useColors();
  const [search, setSearch] = React.useState('');
  const [showModal, setShowModal] = React.useState(false);
  const [editItem, setEditItem] = React.useState(null);
  const [confirmDelete, setConfirmDelete] = React.useState(null);
  const [showIndisponib, setShowIndisponib] = React.useState(null);
  const [form, setForm] = React.useState({ nome: '', funcao: 'Leitor', telefone: '', email: '', ativo: true });
  const funcoes = ['Leitor', 'Música', 'Acólito', 'Ministro da Eucaristia', 'Coordenadora', 'Coordenador', 'Outro'];

  const filtered = data.ministros.filter(m =>
    m.nome.toLowerCase().includes(search.toLowerCase()) ||
    m.funcao.toLowerCase().includes(search.toLowerCase())
  );

  function openAdd() { setForm({ nome: '', funcao: 'Leitor', telefone: '', email: '', ativo: true }); setEditItem(null); setShowModal(true); }
  function openEdit(m) { setForm({ ...m }); setEditItem(m); setShowModal(true); }
  function save() {
    if (!form.nome.trim()) return;
    if (editItem) {
      setData(d => ({ ...d, ministros: d.ministros.map(m => m.id === editItem.id ? { ...m, ...form } : m) }));
      showToast('Ministro atualizado!');
    } else {
      const newId = Math.max(...data.ministros.map(m => m.id)) + 1;
      setData(d => ({ ...d, ministros: [...d.ministros, { ...form, id: newId }] }));
      showToast('Ministro adicionado!');
    }
    setShowModal(false);
  }
  function del() {
    setData(d => ({ ...d, ministros: d.ministros.filter(m => m.id !== confirmDelete) }));
    setConfirmDelete(null); showToast('Ministro removido.');
  }

  return (
    <div style={{ padding: '0 0 24px' }}>
      <ScreenHeader title="Ministros" subtitle={`${filtered.length} ministro(s)`} onAdd={openAdd} addLabel="Novo Ministro" isDesktop={isDesktop} />
      <div style={{ padding: '0 16px 16px' }}>
        <SearchBar value={search} onChange={setSearch} placeholder="Buscar por nome ou função…" />
      </div>
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {filtered.length === 0 && <EmptyState message="Nenhum ministro encontrado" sub="Tente outro termo ou adicione um novo." />}
        {filtered.map(m => (
          <Card key={m.id} style={{ padding: '14px 16px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <Avatar nome={m.nome} size={44} color={m.ativo ? C.primary : C.neutral} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
                  <span style={{ fontSize: 15, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>{m.nome}</span>
                  {!m.ativo && <StatusBadge status="NEUTRAL" label="Inativo" />}
                </div>
                <div style={{ fontSize: 13, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 2 }}>{m.funcao}</div>
                {m.telefone && <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 4, display: 'flex', alignItems: 'center', gap: 4 }}><Icon name="phone" size={12} />{m.telefone}</div>}
              </div>
              <div style={{ display: 'flex', gap: 4, flexShrink: 0 }}>
                <ActionBtn icon="calendar" onClick={() => setShowIndisponib(m)} title="Indisponibilidades" color={C.secondary} />
                <ActionBtn icon="edit" onClick={() => openEdit(m)} title="Editar" />
                <ActionBtn icon="trash" onClick={() => setConfirmDelete(m.id)} title="Remover" color={C.error} />
              </div>
            </div>
          </Card>
        ))}
      </div>
      {!isDesktop && <FAB onClick={openAdd} label="Novo Ministro" />}

      <Modal open={showModal} onClose={() => setShowModal(false)} title={editItem ? 'Editar Ministro' : 'Novo Ministro'}>
        <FormField label="Nome completo" required><Input value={form.nome} onChange={v => setForm(f => ({ ...f, nome: v }))} placeholder="Ex.: João Silva" /></FormField>
        <FormField label="Função" required><Select value={form.funcao} onChange={v => setForm(f => ({ ...f, funcao: v }))}>{funcoes.map(f => <option key={f} value={f}>{f}</option>)}</Select></FormField>
        <FormField label="Telefone"><Input value={form.telefone} onChange={v => setForm(f => ({ ...f, telefone: v }))} placeholder="(11) 99999-9999" /></FormField>
        <FormField label="E-mail"><Input value={form.email} onChange={v => setForm(f => ({ ...f, email: v }))} placeholder="email@paroquia.org" type="email" /></FormField>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 24 }}>
          <input type="checkbox" id="ativo" checked={form.ativo} onChange={e => setForm(f => ({ ...f, ativo: e.target.checked }))} style={{ width: 16, height: 16, accentColor: C.primary }} />
          <label htmlFor="ativo" style={{ fontSize: 14, color: C.onSurface, fontFamily: "'Nunito', sans-serif", fontWeight: 500 }}>Ministro ativo</label>
        </div>
        <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
          <Button variant="ghost" onClick={() => setShowModal(false)}>Cancelar</Button>
          <Button onClick={save}>{editItem ? 'Salvar' : 'Adicionar'}</Button>
        </div>
      </Modal>

      <ConfirmDialog open={!!confirmDelete} onClose={() => setConfirmDelete(null)} onConfirm={del}
        title="Remover Ministro" message="Tem certeza que deseja remover este ministro? Esta ação não pode ser desfeita." confirmLabel="Remover" danger />

      <IndisponibModal open={!!showIndisponib} ministro={showIndisponib} data={data} setData={setData} onClose={() => setShowIndisponib(null)} showToast={showToast} />
    </div>
  );
}
window.MinistrosScreen = MinistrosScreen;

function IndisponibModal({ open, ministro, data, setData, onClose, showToast }) {
  const C = useColors();
  const [novaData, setNovaData] = React.useState('');
  const [novoMotivo, setNovoMotivo] = React.useState('');
  if (!ministro) return null;
  const indisps = data.indisponibilidades.filter(i => i.ministroId === ministro.id);
  function add() {
    if (!novaData) return;
    setData(d => ({ ...d, indisponibilidades: [...d.indisponibilidades, { ministroId: ministro.id, data: novaData, motivo: novoMotivo }] }));
    setNovaData(''); setNovoMotivo(''); showToast('Indisponibilidade registrada.');
  }
  function remove(idx) {
    const item = indisps[idx];
    setData(d => ({ ...d, indisponibilidades: d.indisponibilidades.filter(i => !(i.ministroId === item.ministroId && i.data === item.data)) }));
  }
  return (
    <Modal open={open} onClose={onClose} title={`Indisponibilidades — ${ministro.nome}`}>
      <div style={{ marginBottom: 20 }}>
        {indisps.length === 0 && <p style={{ fontSize: 13, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", margin: '0 0 16px' }}>Nenhuma indisponibilidade registrada.</p>}
        {indisps.map((ind, i) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 12px', borderRadius: 8, background: C.errorLight, marginBottom: 8 }}>
            <div>
              <span style={{ fontSize: 13, fontWeight: 700, color: C.error, fontFamily: "'Nunito', sans-serif" }}>{fmtDate(ind.data)}</span>
              {ind.motivo && <span style={{ fontSize: 12, color: C.error, fontFamily: "'Nunito', sans-serif", marginLeft: 8 }}>— {ind.motivo}</span>}
            </div>
            <button onClick={() => remove(i)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: C.error, display: 'flex' }}><Icon name="x" size={16} /></button>
          </div>
        ))}
      </div>
      <div style={{ padding: 14, borderRadius: 10, background: C.surfaceVariant, border: `1px solid ${C.border}` }}>
        <p style={{ margin: '0 0 10px', fontSize: 13, fontWeight: 600, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>Adicionar indisponibilidade</p>
        <div style={{ marginBottom: 8 }}><Input type="date" value={novaData} onChange={setNovaData} /></div>
        <Input value={novoMotivo} onChange={setNovoMotivo} placeholder="Motivo (opcional)" />
        <div style={{ marginTop: 10 }}><Button onClick={add} size="sm">Registrar</Button></div>
      </div>
    </Modal>
  );
}

// ── Eventos ───────────────────────────────────────────────────────────────────
function EventosScreen({ data, setData, isDesktop, showToast }) {
  const C = useColors();
  const [search, setSearch] = React.useState('');
  const [showModal, setShowModal] = React.useState(false);
  const [editItem, setEditItem] = React.useState(null);
  const [confirmCancel, setConfirmCancel] = React.useState(null);
  const [form, setForm] = React.useState({ nome: '', data: '', horario: '09:00', local: 'Igreja Matriz', tipo: 'Missa', capacidade: 8 });
  const tipos = ['Missa', 'Batizado', 'Retiro', 'Celebração', 'Casamento', 'Funeral', 'Outro'];

  const filtered = data.eventos.filter(e =>
    e.nome.toLowerCase().includes(search.toLowerCase()) ||
    e.local.toLowerCase().includes(search.toLowerCase()) ||
    e.tipo.toLowerCase().includes(search.toLowerCase())
  );

  function openAdd() { setForm({ nome: '', data: '', horario: '09:00', local: 'Igreja Matriz', tipo: 'Missa', capacidade: 8 }); setEditItem(null); setShowModal(true); }
  function openEdit(e) { setForm({ ...e }); setEditItem(e); setShowModal(true); }
  function save() {
    if (!form.nome.trim() || !form.data) return;
    if (editItem) {
      setData(d => ({ ...d, eventos: d.eventos.map(e => e.id === editItem.id ? { ...e, ...form } : e) }));
      showToast('Evento atualizado!');
    } else {
      const newId = Math.max(...data.eventos.map(e => e.id)) + 1;
      setData(d => ({ ...d, eventos: [...d.eventos, { ...form, id: newId, status: 'ATIVO', capacidade: Number(form.capacidade) }] }));
      showToast('Evento criado!');
    }
    setShowModal(false);
  }
  function cancelEvento() {
    setData(d => ({ ...d, eventos: d.eventos.map(e => e.id === confirmCancel ? { ...e, status: 'CONCLUIDO' } : e) }));
    setConfirmCancel(null); showToast('Evento cancelado.');
  }

  return (
    <div style={{ padding: '0 0 24px' }}>
      <ScreenHeader title="Eventos" subtitle={`${filtered.length} evento(s)`} onAdd={openAdd} addLabel="Novo Evento" isDesktop={isDesktop} />
      <div style={{ padding: '0 16px 16px' }}>
        <SearchBar value={search} onChange={setSearch} placeholder="Buscar evento, local ou tipo…" />
      </div>
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {filtered.length === 0 && <EmptyState message="Nenhum evento encontrado" />}
        {filtered.map(ev => (
          <Card key={ev.id} style={{ padding: '16px' }}>
            <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 10 }}>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap', marginBottom: 4 }}>
                  <span style={{ fontSize: 15, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>{ev.nome}</span>
                  <StatusBadge status={ev.status} label={ev.status === 'ATIVO' ? 'Ativo' : 'Concluído'} />
                </div>
                <div style={{ display: 'flex', gap: 14, flexWrap: 'wrap' }}>
                  <span style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", display: 'flex', alignItems: 'center', gap: 4 }}><Icon name="calendar" size={12} />{fmtDate(ev.data)}</span>
                  <span style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", display: 'flex', alignItems: 'center', gap: 4 }}><Icon name="clock" size={12} />{ev.horario}</span>
                  <span style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", display: 'flex', alignItems: 'center', gap: 4 }}><Icon name="mapPin" size={12} />{ev.local}</span>
                </div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: 10, borderTop: `1px solid ${C.border}` }}>
              <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                <span style={{ fontSize: 12, fontWeight: 600, color: C.primary, background: C.primaryLight, padding: '3px 10px', borderRadius: 6, fontFamily: "'Nunito', sans-serif" }}>{ev.tipo}</span>
                <span style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", display: 'flex', alignItems: 'center', gap: 3 }}><Icon name="ministers" size={12} />Cap. {ev.capacidade}</span>
              </div>
              <div style={{ display: 'flex', gap: 4 }}>
                <ActionBtn icon="edit" onClick={() => openEdit(ev)} title="Editar" />
                {ev.status === 'ATIVO' && <ActionBtn icon="ban" onClick={() => setConfirmCancel(ev.id)} title="Cancelar" color={C.error} />}
              </div>
            </div>
          </Card>
        ))}
      </div>
      {!isDesktop && <FAB onClick={openAdd} label="Novo Evento" />}

      <Modal open={showModal} onClose={() => setShowModal(false)} title={editItem ? 'Editar Evento' : 'Novo Evento'}>
        <FormField label="Nome do evento" required><Input value={form.nome} onChange={v => setForm(f => ({ ...f, nome: v }))} placeholder="Ex.: Missa Dominical" /></FormField>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
          <FormField label="Data" required><Input type="date" value={form.data} onChange={v => setForm(f => ({ ...f, data: v }))} /></FormField>
          <FormField label="Horário"><Input type="time" value={form.horario} onChange={v => setForm(f => ({ ...f, horario: v }))} /></FormField>
        </div>
        <FormField label="Local"><Input value={form.local} onChange={v => setForm(f => ({ ...f, local: v }))} placeholder="Igreja Matriz" /></FormField>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
          <FormField label="Tipo"><Select value={form.tipo} onChange={v => setForm(f => ({ ...f, tipo: v }))}>{tipos.map(t => <option key={t} value={t}>{t}</option>)}</Select></FormField>
          <FormField label="Capacidade"><Input type="number" value={form.capacidade} onChange={v => setForm(f => ({ ...f, capacidade: v }))} placeholder="8" /></FormField>
        </div>
        <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end', marginTop: 8 }}>
          <Button variant="ghost" onClick={() => setShowModal(false)}>Cancelar</Button>
          <Button onClick={save}>{editItem ? 'Salvar' : 'Criar Evento'}</Button>
        </div>
      </Modal>

      <ConfirmDialog open={!!confirmCancel} onClose={() => setConfirmCancel(null)} onConfirm={cancelEvento}
        title="Cancelar Evento" message="Deseja cancelar este evento? As escalas vinculadas serão impactadas." confirmLabel="Cancelar Evento" danger />
    </div>
  );
}
window.EventosScreen = EventosScreen;

// ── Escalas ───────────────────────────────────────────────────────────────────
function EscalasScreen({ data, setData, isDesktop, showToast }) {
  const C = useColors();
  const [filter, setFilter] = React.useState('TODOS');
  const [showGenerar, setShowGenerar] = React.useState(false);
  const [eventoSel, setEventoSel] = React.useState('');
  const [confirmAction, setConfirmAction] = React.useState(null);

  const filtros = ['TODOS', 'PROPOSTA', 'APROVADA', 'CONFIRMADA', 'CANCELADA'];
  const filtered = filter === 'TODOS' ? data.escalas : data.escalas.filter(e => e.status === filter);

  function gerarEscala() {
    if (!eventoSel) return;
    const evento = data.eventos.find(e => e.id === Number(eventoSel));
    if (!evento) return;
    const disponiveis = data.ministros.filter(m => m.ativo).slice(0, 4);
    const novaEscala = {
      id: Math.max(...data.escalas.map(e => e.id)) + 1,
      eventoId: evento.id, eventoNome: evento.nome,
      data: evento.data, status: 'PROPOSTA',
      ministros: disponiveis.map(m => ({ ministroId: m.id, nome: m.nome, funcao: m.funcao, confirmado: false })),
    };
    setData(d => ({ ...d, escalas: [novaEscala, ...d.escalas] }));
    setShowGenerar(false); setEventoSel('');
    showToast('Escala gerada com sucesso!');
  }

  function doAction(esc, acao) {
    const statusMap = { aprovar: 'APROVADA', confirmar: 'CONFIRMADA', cancelar: 'CANCELADA' };
    setData(d => ({ ...d, escalas: d.escalas.map(e => e.id === esc.id ? { ...e, status: statusMap[acao] } : e) }));
    setConfirmAction(null);
    showToast(`Escala ${acao === 'aprovar' ? 'aprovada' : acao === 'confirmar' ? 'confirmada' : 'cancelada'}!`);
  }

  const smMap = getStatusMap(C);

  return (
    <div style={{ padding: '0 0 24px' }}>
      <ScreenHeader title="Escalas" subtitle={`${filtered.length} escala(s)`} onAdd={() => setShowGenerar(true)} addLabel="Gerar Escala" isDesktop={isDesktop} />
      <div style={{ padding: '0 16px 16px', display: 'flex', gap: 8, overflowX: 'auto', scrollbarWidth: 'none' }}>
        {filtros.map(f => (
          <FilterChip key={f} label={f === 'TODOS' ? 'Todos' : smMap[f]?.label || f} active={filter === f} onClick={() => setFilter(f)} />
        ))}
      </div>
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {filtered.length === 0 && <EmptyState message="Nenhuma escala encontrada" sub="Gere uma nova escala a partir de um evento." />}
        {filtered.map(esc => (
          <Card key={esc.id} style={{ padding: '16px' }}>
            <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 12 }}>
              <div>
                <div style={{ fontSize: 15, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif", marginBottom: 3 }}>{esc.eventoNome}</div>
                <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", display: 'flex', alignItems: 'center', gap: 4 }}><Icon name="calendar" size={12} />{fmtDate(esc.data)}</div>
              </div>
              <StatusBadge status={esc.status} />
            </div>
            {esc.ministros.length > 0 && (
              <div style={{ marginBottom: 12 }}>
                <div style={{ fontSize: 11, fontWeight: 700, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginBottom: 8, textTransform: 'uppercase', letterSpacing: '0.06em' }}>Ministros escalados</div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                  {esc.ministros.map((m, i) => (
                    <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <Avatar nome={m.nome} size={28} color={m.confirmado ? C.success : C.neutral} />
                      <div style={{ flex: 1 }}>
                        <span style={{ fontSize: 13, fontWeight: 600, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>{m.nome}</span>
                        <span style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginLeft: 6 }}>{m.funcao}</span>
                      </div>
                      {m.confirmado
                        ? <span style={{ fontSize: 11, fontWeight: 700, color: C.success, display: 'flex', alignItems: 'center', gap: 3 }}><Icon name="check" size={12} color={C.success} />Confirmado</span>
                        : <span style={{ fontSize: 11, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif" }}>Pendente</span>}
                    </div>
                  ))}
                </div>
              </div>
            )}
            {esc.ministros.length === 0 && <p style={{ fontSize: 13, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", margin: '0 0 12px' }}>Nenhum ministro nesta escala.</p>}
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', paddingTop: 10, borderTop: `1px solid ${C.border}` }}>
              {esc.status === 'PROPOSTA' && <Button size="sm" onClick={() => setConfirmAction({ esc, acao: 'aprovar' })}><Icon name="check" size={14} />Aprovar</Button>}
              {esc.status === 'APROVADA' && <Button size="sm" onClick={() => setConfirmAction({ esc, acao: 'confirmar' })} color={C.success}><Icon name="check" size={14} />Confirmar</Button>}
              {(esc.status === 'PROPOSTA' || esc.status === 'APROVADA') && <Button size="sm" variant="secondary" onClick={() => setConfirmAction({ esc, acao: 'cancelar' })} color={C.error}><Icon name="ban" size={14} />Cancelar</Button>}
            </div>
          </Card>
        ))}
      </div>
      {!isDesktop && <FAB onClick={() => setShowGenerar(true)} label="Gerar Escala" />}

      <Modal open={showGenerar} onClose={() => setShowGenerar(false)} title="Gerar Nova Escala" maxWidth={400}>
        <FormField label="Selecionar Evento" required>
          <Select value={eventoSel} onChange={setEventoSel}>
            <option value="">— selecione um evento —</option>
            {data.eventos.filter(e => e.status === 'ATIVO').map(e => <option key={e.id} value={e.id}>{e.nome} ({fmtDate(e.data)})</option>)}
          </Select>
        </FormField>
        <p style={{ fontSize: 13, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", margin: '0 0 20px', lineHeight: 1.6 }}>
          A escala será gerada automaticamente com base na disponibilidade dos ministros ativos.
        </p>
        <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
          <Button variant="ghost" onClick={() => setShowGenerar(false)}>Cancelar</Button>
          <Button onClick={gerarEscala} disabled={!eventoSel}><Icon name="zap" size={16} />Gerar Escala</Button>
        </div>
      </Modal>

      {confirmAction && (
        <ConfirmDialog open={true} onClose={() => setConfirmAction(null)}
          onConfirm={() => doAction(confirmAction.esc, confirmAction.acao)}
          title={confirmAction.acao === 'aprovar' ? 'Aprovar Escala' : confirmAction.acao === 'confirmar' ? 'Confirmar Escala' : 'Cancelar Escala'}
          message={`Deseja ${confirmAction.acao} a escala de "${confirmAction.esc.eventoNome}"?`}
          confirmLabel={confirmAction.acao === 'aprovar' ? 'Aprovar' : confirmAction.acao === 'confirmar' ? 'Confirmar' : 'Cancelar Escala'}
          danger={confirmAction.acao === 'cancelar'} />
      )}
    </div>
  );
}
window.EscalasScreen = EscalasScreen;

// ── Feedback ──────────────────────────────────────────────────────────────────
function FeedbackScreen({ data, setData, isDesktop, showToast }) {
  const C = useColors();
  const [filter, setFilter] = React.useState('TODOS');
  const [showModal, setShowModal] = React.useState(false);
  const [resposta, setResposta] = React.useState('');
  const [selectedFb, setSelectedFb] = React.useState(null);

  const filtros = [{ key: 'TODOS', label: 'Todos' }, { key: 'PENDENTE', label: 'Pendentes' }, { key: 'RESPONDIDO', label: 'Respondidos' }];
  const filtered = filter === 'TODOS' ? data.feedbacks : filter === 'PENDENTE' ? data.feedbacks.filter(f => !f.respondido) : data.feedbacks.filter(f => f.respondido);
  const notas = data.feedbacks.map(f => f.nota);
  const notaMedia = notas.length ? (notas.reduce((a, b) => a + b, 0) / notas.length).toFixed(1) : '—';
  const pendentes = data.feedbacks.filter(f => !f.respondido).length;

  function openResposta(fb) { setSelectedFb(fb); setResposta(fb.resposta || ''); setShowModal(true); }
  function saveResposta() {
    setData(d => ({ ...d, feedbacks: d.feedbacks.map(f => f.id === selectedFb.id ? { ...f, resposta, respondido: true } : f) }));
    setShowModal(false); showToast('Resposta enviada!');
  }

  return (
    <div style={{ padding: '0 0 24px' }}>
      <ScreenHeader title="Feedback" isDesktop={isDesktop} />
      <div style={{ padding: '0 16px 16px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <Card style={{ padding: '14px 16px', background: C.amberLight }} hover={false}>
          <div style={{ fontSize: 28, fontWeight: 800, color: C.amber, fontFamily: "'Nunito', sans-serif", lineHeight: 1 }}>{notaMedia}</div>
          <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 4 }}>Nota Média</div>
          <div style={{ display: 'flex', gap: 2, marginTop: 6 }}>
            {[1,2,3,4,5].map(i => <Icon key={i} name={Number(notaMedia) >= i * 2 ? 'starFilled' : 'star'} size={12} color={C.amber} />)}
          </div>
        </Card>
        <Card style={{ padding: '14px 16px', background: pendentes > 0 ? C.errorLight : C.successLight }} hover={false}>
          <div style={{ fontSize: 28, fontWeight: 800, color: pendentes > 0 ? C.error : C.success, fontFamily: "'Nunito', sans-serif", lineHeight: 1 }}>{pendentes}</div>
          <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", marginTop: 4 }}>Pendentes</div>
          <div style={{ marginTop: 6 }}><Icon name={pendentes > 0 ? 'alertCircle' : 'check'} size={16} color={pendentes > 0 ? C.error : C.success} /></div>
        </Card>
      </div>
      <div style={{ padding: '0 16px 16px', display: 'flex', gap: 8, overflowX: 'auto', scrollbarWidth: 'none' }}>
        {filtros.map(f => <FilterChip key={f.key} label={f.label} active={filter === f.key} onClick={() => setFilter(f.key)} />)}
      </div>
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {filtered.length === 0 && <EmptyState message="Nenhum feedback encontrado" />}
        {filtered.map(fb => (
          <Card key={fb.id} style={{ padding: '16px' }}>
            <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 10 }}>
              <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                <Avatar nome={fb.ministroNome} size={38} />
                <div>
                  <div style={{ fontSize: 14, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif" }}>{fb.ministroNome}</div>
                  <div style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif" }}>{fb.eventoNome} • {fmtDate(fb.data)}</div>
                </div>
              </div>
              <span style={{ fontSize: 20, fontWeight: 800, color: fb.nota >= 8 ? C.success : fb.nota >= 6 ? C.amber : C.error, fontFamily: "'Nunito', sans-serif" }}>
                {fb.nota}<span style={{ fontSize: 11, fontWeight: 500, color: C.onSurfaceVariant }}>/10</span>
              </span>
            </div>
            <p style={{ margin: '0 0 10px', fontSize: 13, color: C.onSurface, fontFamily: "'Nunito', sans-serif", lineHeight: 1.6, fontStyle: 'italic' }}>"{fb.comentario}"</p>
            {fb.resposta && (
              <div style={{ padding: '10px 12px', borderRadius: 8, background: C.secondaryLight, borderLeft: `3px solid ${C.secondary}`, marginBottom: 10 }}>
                <div style={{ fontSize: 11, fontWeight: 700, color: C.secondary, fontFamily: "'Nunito', sans-serif", marginBottom: 3, textTransform: 'uppercase', letterSpacing: '0.05em' }}>Resposta da Coordenação</div>
                <p style={{ margin: 0, fontSize: 13, color: C.onSurface, fontFamily: "'Nunito', sans-serif", lineHeight: 1.5 }}>{fb.resposta}</p>
              </div>
            )}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <StatusBadge status={fb.respondido ? 'CONFIRMADA' : 'PROPOSTA'} label={fb.respondido ? 'Respondido' : 'Pendente'} />
              {!fb.respondido && <Button size="sm" variant="secondary" onClick={() => openResposta(fb)}><Icon name="reply" size={14} />Responder</Button>}
            </div>
          </Card>
        ))}
      </div>

      <Modal open={showModal} onClose={() => setShowModal(false)} title="Responder Feedback" maxWidth={440}>
        {selectedFb && (
          <>
            <div style={{ padding: 12, borderRadius: 10, background: C.surfaceVariant, marginBottom: 16, border: `1px solid ${C.border}` }}>
              <div style={{ fontSize: 13, fontWeight: 700, color: C.onSurface, fontFamily: "'Nunito', sans-serif", marginBottom: 4 }}>{selectedFb.ministroNome} — nota {selectedFb.nota}/10</div>
              <p style={{ margin: 0, fontSize: 13, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", fontStyle: 'italic' }}>"{selectedFb.comentario}"</p>
            </div>
            <FormField label="Sua resposta"><Textarea value={resposta} onChange={setResposta} placeholder="Escreva uma resposta ao ministro…" rows={4} /></FormField>
            <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
              <Button variant="ghost" onClick={() => setShowModal(false)}>Cancelar</Button>
              <Button onClick={saveResposta}><Icon name="send" size={15} />Enviar Resposta</Button>
            </div>
          </>
        )}
      </Modal>
    </div>
  );
}
window.FeedbackScreen = FeedbackScreen;

// ── Auditoria ─────────────────────────────────────────────────────────────────
function AuditoriaScreen({ data, isDesktop }) {
  const C = useColors();
  const [filter, setFilter] = React.useState('TODOS');
  const entidades = ['TODOS', 'Escala', 'Ministro', 'Evento', 'Feedback'];
  const filtered = filter === 'TODOS' ? data.auditoria : data.auditoria.filter(a => a.entidade === filter);
  const smMap = getStatusMap(C);

  const actionIcon = { 'CRIAÇÃO': 'plus', 'APROVAÇÃO': 'check', 'CANCELAMENTO': 'ban', 'CONFIRMAÇÃO': 'check', 'EDIÇÃO': 'edit', 'EXCLUSÃO': 'trash' };

  return (
    <div style={{ padding: '0 0 24px' }}>
      <ScreenHeader title="Auditoria" subtitle={`${filtered.length} registro(s)`} isDesktop={isDesktop} />
      <div style={{ padding: '0 16px 16px', display: 'flex', gap: 8, overflowX: 'auto', scrollbarWidth: 'none' }}>
        {entidades.map(e => <FilterChip key={e} label={e === 'TODOS' ? 'Todas' : e} active={filter === e} onClick={() => setFilter(e)} />)}
      </div>
      <div style={{ padding: '0 16px' }}>
        {filtered.length === 0 && <EmptyState message="Nenhum registro encontrado" />}
        <Card hover={false}>
          {filtered.map((log, i) => {
            const sm = smMap[log.acao] || smMap.NEUTRAL;
            return (
              <React.Fragment key={log.id}>
                <div style={{ display: 'flex', gap: 12, padding: '14px 16px', alignItems: 'flex-start' }}>
                  <div style={{ width: 36, height: 36, borderRadius: 10, background: sm.bg, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                    <Icon name={actionIcon[log.acao] || 'clock'} size={16} color={sm.color} />
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap', marginBottom: 3 }}>
                      <StatusBadge status={log.acao} label={log.acao} />
                      <span style={{ fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif", background: C.surfaceVariant, padding: '2px 8px', borderRadius: 6, border: `1px solid ${C.border}` }}>{log.entidade}</span>
                    </div>
                    <p style={{ margin: '4px 0 3px', fontSize: 13, color: C.onSurface, fontFamily: "'Nunito', sans-serif", lineHeight: 1.5 }}>{log.descricao}</p>
                    <div style={{ display: 'flex', gap: 12, fontSize: 12, color: C.onSurfaceVariant, fontFamily: "'Nunito', sans-serif" }}>
                      <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}><Icon name="clock" size={11} />{log.data}</span>
                      <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}><Icon name="user" size={11} />{log.usuario}</span>
                    </div>
                  </div>
                </div>
                {i < filtered.length - 1 && <Divider />}
              </React.Fragment>
            );
          })}
        </Card>
      </div>
    </div>
  );
}
window.AuditoriaScreen = AuditoriaScreen;
