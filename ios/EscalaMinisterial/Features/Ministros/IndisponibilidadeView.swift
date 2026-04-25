import SwiftUI

struct IndisponibilidadeView: View {
    let sheet: IndisponibilidadeSheet
    let onCreate: (Indisponibilidade) -> Void
    let onUpdate: (Indisponibilidade) -> Void
    let onDelete: (Int) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var showForm = false
    @State private var editingItem: Indisponibilidade? = nil

    var body: some View {
        NavigationStack {
            List {
                if showForm {
                    Section("Nova indisponibilidade") {
                        IndisponibilidadeFormSection(
                            existing: editingItem,
                            ministroId: sheet.ministroId,
                            onSave: { ind in
                                if editingItem != nil {
                                    onUpdate(ind)
                                } else {
                                    onCreate(ind)
                                }
                                showForm = false
                                editingItem = nil
                            },
                            onCancel: {
                                showForm = false
                                editingItem = nil
                            }
                        )
                    }
                }

                if sheet.isLoading {
                    Section {
                        HStack {
                            Spacer()
                            ProgressView()
                            Spacer()
                        }
                    }
                } else if sheet.items.isEmpty && !showForm {
                    Section {
                        Text("Nenhuma indisponibilidade registrada.")
                            .foregroundColor(.secondary)
                            .font(.appBody)
                    }
                } else {
                    Section("Registros") {
                        ForEach(sheet.items) { ind in
                            VStack(alignment: .leading, spacing: 4) {
                                Text(ind.dataFormatada)
                                    .font(.appBody)
                                if let inicio = ind.horarioInicio {
                                    let fim = ind.horarioFim ?? ""
                                    Text("\(inicio)–\(fim)")
                                        .font(.appCaption)
                                        .foregroundColor(.secondary)
                                }
                                if let motivo = ind.motivo, !motivo.isEmpty {
                                    Text(motivo)
                                        .font(.appCaption)
                                        .foregroundColor(.secondary)
                                }
                            }
                            .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                                Button(role: .destructive) {
                                    if let id = ind.id { onDelete(id) }
                                } label: {
                                    Label("Excluir", systemImage: "trash")
                                }
                                Button {
                                    editingItem = ind
                                    showForm = true
                                } label: {
                                    Label("Editar", systemImage: "pencil")
                                }
                                .tint(.appPrimary)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Indisponibilidades — \(sheet.ministroNome)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Fechar") { dismiss() }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    if !showForm {
                        Button { editingItem = nil; showForm = true } label: {
                            Image(systemName: "plus")
                        }
                    }
                }
            }
        }
    }
}

private struct IndisponibilidadeFormSection: View {
    let existing: Indisponibilidade?
    let ministroId: Int
    let onSave: (Indisponibilidade) -> Void
    let onCancel: () -> Void

    @State private var data: String = ""
    @State private var horarioInicio: String = ""
    @State private var horarioFim: String = ""
    @State private var motivo: String = ""

    var body: some View {
        Group {
            TextField("Data (AAAA-MM-DD) *", text: $data)
                .keyboardType(.numbersAndPunctuation)
            TextField("Horário início (HH:mm)", text: $horarioInicio)
                .keyboardType(.numbersAndPunctuation)
            TextField("Horário fim (HH:mm)", text: $horarioFim)
                .keyboardType(.numbersAndPunctuation)
            TextField("Motivo", text: $motivo)

            HStack {
                Button("Cancelar", role: .cancel) { onCancel() }
                    .foregroundColor(.statusError)
                Spacer()
                Button("Salvar") {
                    let ind = Indisponibilidade(
                        id: existing?.id,
                        ministroId: ministroId,
                        data: data.trimmingCharacters(in: .whitespaces),
                        horarioInicio: horarioInicio.isEmpty ? nil : horarioInicio,
                        horarioFim: horarioFim.isEmpty ? nil : horarioFim,
                        motivo: motivo.isEmpty ? nil : motivo
                    )
                    onSave(ind)
                }
                .disabled(data.trimmingCharacters(in: .whitespaces).isEmpty)
                .buttonStyle(.borderedProminent)
            }
        }
        .onAppear {
            if let e = existing {
                data = e.data
                horarioInicio = e.horarioInicio ?? ""
                horarioFim = e.horarioFim ?? ""
                motivo = e.motivo ?? ""
            } else {
                data = DateUtils.todayString()
            }
        }
    }
}
