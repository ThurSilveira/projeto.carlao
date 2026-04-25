import SwiftUI

struct MinistroFormView: View {
    let ministro: Ministro?
    let onSave: (Ministro) -> Void

    @Environment(\.dismiss) private var dismiss

    @State private var nome: String = ""
    @State private var email: String = ""
    @State private var telefone: String = ""
    @State private var dataNascimento: String = ""
    @State private var observacoes: String = ""
    @State private var ativo: Bool = true
    @State private var funcao: String = Ministro.FuncaoMinistro.eucaristia.rawValue
    @State private var funcaoEspecificada: String = ""
    @State private var visitasAoInfermo: Bool = false
    @State private var statusCurso: Bool = false

    private var isEditing: Bool { ministro != nil }

    var body: some View {
        NavigationStack {
            Form {
                Section("Dados principais") {
                    TextField("Nome *", text: $nome)
                    TextField("E-mail *", text: $email)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                    TextField("Telefone", text: $telefone)
                        .keyboardType(.phonePad)
                    TextField("Data de nascimento (AAAA-MM-DD)", text: $dataNascimento)
                }

                Section("Função") {
                    Picker("Função", selection: $funcao) {
                        ForEach(Ministro.FuncaoMinistro.allCases, id: \.rawValue) { f in
                            Text(f.label).tag(f.rawValue)
                        }
                    }
                    if funcao == Ministro.FuncaoMinistro.outro.rawValue {
                        TextField("Especifique a função", text: $funcaoEspecificada)
                    }
                }

                Section("Opções") {
                    Toggle("Ativo", isOn: $ativo)
                    Toggle("Visitas ao Infermo", isOn: $visitasAoInfermo)
                    Toggle("Status do Curso", isOn: $statusCurso)
                }

                Section("Observações") {
                    TextField("Observações", text: $observacoes, axis: .vertical)
                        .lineLimit(3...6)
                }
            }
            .navigationTitle(isEditing ? "Editar Ministro" : "Novo Ministro")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Salvar") { save() }
                        .disabled(nome.trimmingCharacters(in: .whitespaces).isEmpty || email.trimmingCharacters(in: .whitespaces).isEmpty)
                }
            }
            .onAppear { populate() }
        }
    }

    private func populate() {
        guard let m = ministro else { return }
        nome = m.nome
        email = m.email
        telefone = m.telefone ?? ""
        dataNascimento = m.dataNascimento ?? ""
        observacoes = m.observacoes ?? ""
        ativo = m.ativo
        funcao = m.funcao
        funcaoEspecificada = m.funcaoEspecificada ?? ""
        visitasAoInfermo = m.visitasAoInfermo
        statusCurso = m.statusCurso
    }

    private func save() {
        let m = Ministro(
            id: ministro?.id,
            nome: nome.trimmingCharacters(in: .whitespaces),
            email: email.trimmingCharacters(in: .whitespaces),
            telefone: telefone.isEmpty ? nil : telefone,
            dataNascimento: dataNascimento.isEmpty ? nil : dataNascimento,
            observacoes: observacoes.isEmpty ? nil : observacoes,
            ativo: ativo,
            funcao: funcao,
            funcaoEspecificada: funcao == Ministro.FuncaoMinistro.outro.rawValue && !funcaoEspecificada.isEmpty ? funcaoEspecificada : nil,
            visitasAoInfermo: visitasAoInfermo,
            statusCurso: statusCurso
        )
        onSave(m)
        dismiss()
    }
}

#Preview {
    MinistroFormView(ministro: nil) { _ in }
}
