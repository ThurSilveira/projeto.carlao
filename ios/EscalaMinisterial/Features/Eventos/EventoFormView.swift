import SwiftUI

struct EventoFormView: View {
    let evento: Evento?
    let onSave: (Evento) -> Void

    @Environment(\.dismiss) private var dismiss

    @State private var nome: String = ""
    @State private var data: String = ""
    @State private var horario: String = ""
    @State private var tipoEvento: String = Evento.TipoEvento.missaParoquial.rawValue
    @State private var tipoEspecificado: String = ""
    @State private var maxMinistros: String = "5"
    @State private var local: String = ""

    private var isEditing: Bool { evento != nil }

    var body: some View {
        NavigationStack {
            Form {
                Section("Dados do Evento") {
                    TextField("Nome *", text: $nome)
                    TextField("Data (AAAA-MM-DD) *", text: $data)
                        .keyboardType(.numbersAndPunctuation)
                    TextField("Horário (HH:mm) *", text: $horario)
                        .keyboardType(.numbersAndPunctuation)
                    TextField("Local", text: $local)
                }

                Section("Tipo") {
                    Picker("Tipo do evento", selection: $tipoEvento) {
                        ForEach(Evento.TipoEvento.allCases, id: \.rawValue) { t in
                            Text(t.label).tag(t.rawValue)
                        }
                    }
                    if tipoEvento == Evento.TipoEvento.outro.rawValue {
                        TextField("Especifique o tipo", text: $tipoEspecificado)
                    }
                }

                Section("Capacidade") {
                    TextField("Máximo de ministros *", text: $maxMinistros)
                        .keyboardType(.numberPad)
                }
            }
            .navigationTitle(isEditing ? "Editar Evento" : "Novo Evento")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Salvar") { save() }
                        .disabled(!formValid)
                }
            }
            .onAppear { populate() }
        }
    }

    private var formValid: Bool {
        !nome.trimmingCharacters(in: .whitespaces).isEmpty
            && !data.trimmingCharacters(in: .whitespaces).isEmpty
            && !horario.trimmingCharacters(in: .whitespaces).isEmpty
            && (Int(maxMinistros) ?? 0) > 0
    }

    private func populate() {
        guard let e = evento else { return }
        nome = e.nome
        data = e.data
        horario = e.horario
        tipoEvento = e.tipoEvento
        tipoEspecificado = e.tipoEspecificado ?? ""
        maxMinistros = "\(e.maxMinistros)"
        local = e.local ?? ""
    }

    private func save() {
        let e = Evento(
            id: evento?.id,
            nome: nome.trimmingCharacters(in: .whitespaces),
            data: data.trimmingCharacters(in: .whitespaces),
            horario: horario.trimmingCharacters(in: .whitespaces),
            tipoEvento: tipoEvento,
            tipoEspecificado: tipoEvento == Evento.TipoEvento.outro.rawValue && !tipoEspecificado.isEmpty ? tipoEspecificado : nil,
            maxMinistros: Int(maxMinistros) ?? 5,
            local: local.isEmpty ? nil : local,
            cancelado: evento?.cancelado ?? false
        )
        onSave(e)
        dismiss()
    }
}

#Preview {
    EventoFormView(evento: nil) { _ in }
}
