import SwiftUI

struct GerarEscalaView: View {
    let onGerar: (Int) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var eventoIdText: String = ""
    @State private var errorMessage: String? = nil

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("ID do Evento", text: $eventoIdText)
                        .keyboardType(.numberPad)

                    if let err = errorMessage {
                        Text(err)
                            .font(.appCaption)
                            .foregroundColor(.statusError)
                    }
                } header: {
                    Text("Gerar Escala Automática")
                } footer: {
                    Text("Informe o ID do evento para gerar a escala automaticamente com base na disponibilidade dos ministros.")
                }
            }
            .navigationTitle("Gerar Escala")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Gerar") {
                        guard let id = Int(eventoIdText.trimmingCharacters(in: .whitespaces)) else {
                            errorMessage = "ID inválido. Digite um número inteiro."
                            return
                        }
                        errorMessage = nil
                        onGerar(id)
                        dismiss()
                    }
                    .disabled(eventoIdText.trimmingCharacters(in: .whitespaces).isEmpty)
                }
            }
        }
    }
}

#Preview {
    GerarEscalaView { _ in }
}
