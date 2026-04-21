import React, { useState, useEffect } from 'react';
import { MinistroService } from '@/services/api';
import { Card, Button, Input, Badge, Spinner, Modal, Select, Alert } from '@/components/ui';
import { Ministro, FuncaoMinistro, TipoEvento } from '@/types';
import { Plus, Trash2, Edit2 } from 'lucide-react';

export const MinistrosPage: React.FC = () => {
  const [ministros, setMinistros] = useState<Ministro[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterAtivo, setFilterAtivo] = useState<boolean | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingMinistro, setEditingMinistro] = useState<Ministro | null>(null);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertVariant, setAlertVariant] = useState<'success' | 'error'>('success');
  const [formData, setFormData] = useState<Partial<Ministro>>({
    nome: '',
    telefone: '',
    email: '',
    dataNascimento: '',
    observacoes: '',
    ativo: true,
    funcao: FuncaoMinistro.EUCARISTIA,
    visitasAoInfermo: false,
    statusCurso: false,
    aptidoes: [],
    indisponibilidades: [],
    disponibilidadesRec: [],
    escalasMinistro: [],
  });

  useEffect(() => {
    loadMinistros();
  }, []);

  const loadMinistros = async () => {
    try {
      setLoading(true);
      const data = await MinistroService.getAllMinistros();
      setMinistros(data || []);
    } catch {
      showAlert('Erro ao carregar ministros', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showAlert = (msg: string, variant: 'success' | 'error') => {
    setAlertMessage(msg);
    setAlertVariant(variant);
  };

  const validarFormulario = (dados: Partial<Ministro>): string[] => {
    const erros: string[] = [];
    if (!dados.nome || dados.nome.trim().length < 3) erros.push('Nome deve ter ao menos 3 caracteres');
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!dados.email || !emailRegex.test(dados.email)) erros.push('Email inválido');
    if (!dados.telefone || dados.telefone.replace(/\D/g, '').length < 10) erros.push('Telefone inválido (mín. 10 dígitos)');
    if (!dados.dataNascimento) erros.push('Data de nascimento obrigatória');
    if (!dados.funcao) erros.push('Função ministerial obrigatória');
    return erros;
  };

  const handleSave = async () => {
    const erros = validarFormulario(formData);
    if (erros.length > 0) {
      showAlert(erros.join(' | '), 'error');
      return;
    }
    try {
      if (editingMinistro?.id != null) {
        await MinistroService.updateMinistro(editingMinistro.id, formData);
        showAlert('Ministro atualizado com sucesso!', 'success');
      } else {
        await MinistroService.createMinistro(formData);
        showAlert('Ministro criado com sucesso!', 'success');
      }
      await loadMinistros();
      setIsModalOpen(false);
      resetForm();
    } catch (error: any) {
      showAlert(error?.response?.data?.message || 'Erro ao salvar ministro', 'error');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Tem certeza que deseja deletar este ministro?')) return;
    try {
      await MinistroService.deleteMinistro(id);
      showAlert('Ministro deletado com sucesso!', 'success');
      await loadMinistros();
    } catch {
      showAlert('Erro ao deletar ministro', 'error');
    }
  };

  const handleEdit = (ministro: Ministro) => {
    setEditingMinistro(ministro);
    setFormData({ ...ministro });
    setIsModalOpen(true);
  };

  const resetForm = () => {
    setEditingMinistro(null);
    setFormData({
      nome: '', telefone: '', email: '', dataNascimento: '', observacoes: '',
      ativo: true, funcao: FuncaoMinistro.EUCARISTIA,
      visitasAoInfermo: false, statusCurso: false,
      aptidoes: [], indisponibilidades: [], disponibilidadesRec: [], escalasMinistro: [],
    });
  };

  const filteredMinistros = ministros.filter((m) => {
    const matchesSearch =
      m.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
      m.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesFilter = filterAtivo === null || m.ativo === filterAtivo;
    return matchesSearch && matchesFilter;
  });

  if (loading && ministros.length === 0) {
    return <div className="flex items-center justify-center h-96"><Spinner size="lg" /></div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">👥 Ministros</h1>
          <p className="text-slate-600 mt-1">Gerencie e acompanhe ministros da paróquia</p>
        </div>
        <Button onClick={() => { resetForm(); setIsModalOpen(true); }}>
          <Plus size={18} className="mr-2" />
          Novo Ministro
        </Button>
      </div>

      {alertMessage && (
        <Alert variant={alertVariant} onClose={() => setAlertMessage('')}>{alertMessage}</Alert>
      )}

      <Card>
        <div className="flex flex-col gap-4 md:flex-row md:items-end">
          <Input
            label="Buscar"
            placeholder="Por nome ou email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="flex-1"
          />
          <Select
            label="Status"
            options={[
              { value: '', label: 'Todos' },
              { value: 'ativo', label: 'Ativos' },
              { value: 'inativo', label: 'Inativos' },
            ]}
            onChange={(e) => {
              if (e.target.value === '') setFilterAtivo(null);
              else setFilterAtivo(e.target.value === 'ativo');
            }}
          />
        </div>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredMinistros.length > 0 ? (
          filteredMinistros.map((ministro) => (
            <Card key={ministro.id} className="relative">
              <div className="absolute top-4 right-4 flex gap-2">
                <button
                  onClick={() => handleEdit(ministro)}
                  className="p-2 rounded-lg hover:bg-slate-100 transition-colors"
                  aria-label={`Editar ${ministro.nome}`}
                >
                  <Edit2 size={16} className="text-slate-600" />
                </button>
                <button
                  onClick={() => ministro.id != null && handleDelete(ministro.id)}
                  className="p-2 rounded-lg hover:bg-red-50 transition-colors"
                  aria-label={`Deletar ${ministro.nome}`}
                >
                  <Trash2 size={16} className="text-red-600" />
                </button>
              </div>

              <div className="mb-4">
                <div className="flex items-center gap-2 mb-2">
                  <h3 className="text-lg font-semibold text-slate-900">{ministro.nome}</h3>
                  <Badge variant={ministro.ativo ? 'success' : 'danger'}>
                    {ministro.ativo ? 'Ativo' : 'Inativo'}
                  </Badge>
                </div>
                <Badge variant="primary">{ministro.funcao}</Badge>
              </div>

              <div className="space-y-1 text-sm">
                <p className="text-slate-600">📧 {ministro.email}</p>
                <p className="text-slate-600">📱 {ministro.telefone}</p>
                {ministro.dataNascimento && (
                  <p className="text-slate-600">🎂 {new Date(ministro.dataNascimento).toLocaleDateString('pt-BR')}</p>
                )}
                {ministro.observacoes && <p className="text-slate-600">📝 {ministro.observacoes}</p>}
              </div>

              {ministro.aptidoes && ministro.aptidoes.length > 0 && (
                <div className="mt-4 pt-4 border-t border-slate-200">
                  <p className="text-xs text-slate-600 font-medium mb-2">Aptidões:</p>
                  <div className="flex flex-wrap gap-1">
                    {ministro.aptidoes.map((apt) => (
                      <Badge key={apt} variant="primary">{apt.replace(/_/g, ' ')}</Badge>
                    ))}
                  </div>
                </div>
              )}
            </Card>
          ))
        ) : (
          <div className="col-span-full text-center py-12">
            <p className="text-slate-600 text-lg">Nenhum ministro encontrado</p>
          </div>
        )}
      </div>

      <Modal
        isOpen={isModalOpen}
        title={editingMinistro ? 'Editar Ministro' : 'Novo Ministro'}
        onClose={() => { setIsModalOpen(false); resetForm(); }}
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => { setIsModalOpen(false); resetForm(); }}>Cancelar</Button>
            <Button onClick={handleSave}>{editingMinistro ? 'Atualizar' : 'Criar'}</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Input label="Nome" value={formData.nome as string}
            onChange={(e) => setFormData({ ...formData, nome: e.target.value })} />
          <Input label="Email" type="email" value={formData.email as string}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })} />
          <Input label="Telefone" value={formData.telefone as string}
            onChange={(e) => setFormData({ ...formData, telefone: e.target.value })} />
          <Input label="Data de Nascimento" type="date" value={formData.dataNascimento as string}
            onChange={(e) => setFormData({ ...formData, dataNascimento: e.target.value })} />
          <Select
            label="Função"
            value={formData.funcao as string}
            options={Object.values(FuncaoMinistro).map((f) => ({ value: f, label: f }))}
            onChange={(e) => setFormData({ ...formData, funcao: e.target.value as FuncaoMinistro })}
          />
          <Input label="Observações" value={formData.observacoes as string}
            onChange={(e) => setFormData({ ...formData, observacoes: e.target.value })} />
          <div className="space-y-2">
            {[
              { label: 'Ativo', key: 'ativo' as const },
              { label: 'Realiza visitas ao infermo', key: 'visitasAoInfermo' as const },
              { label: 'Curso completo', key: 'statusCurso' as const },
            ].map(({ label, key }) => (
              <label key={key} className="flex items-center gap-2 text-sm font-medium text-slate-700">
                <input type="checkbox" checked={!!formData[key]}
                  onChange={(e) => setFormData({ ...formData, [key]: e.target.checked })} />
                {label}
              </label>
            ))}
          </div>
        </div>
      </Modal>
    </div>
  );
};
