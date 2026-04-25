import SwiftUI

struct EventosView: View {
    @StateObject private var viewModel = EventosViewModel()
    @State private var showForm = false
    @State private var editingEvento: Evento? = nil
    @State private var confirmAction: ConfirmAction? = nil

    struct ConfirmAction: Identifiable {
        let id = UUID()
        let title: String
        let action: () -> Void
    }

    var body: some View {
        NavigationStack {
            Group {
                switch viewModel.state {
                case .loading:
                    LoadingView()
                case .error(let msg):
                    ErrorView(message: msg) { Task { await viewModel.load() } }
                case .success:
                    VStack(spacing: 0) {
                        VStack(spacing: 8) {
                            HStack {
                                Image(systemName: "magnifyingglass")
                                    .foregroundColor(.secondary)
                                TextField("Buscar evento", text: $viewModel.query)
                                    .autocorrectionDisabled()
                            }
                            .padding(10)
                            .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 10))
                            .padding(.horizontal)

                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 8) {
                                    FilterChipButton(
                                        title: "Todos",
                                        isSelected: viewModel.tipoFilter == nil
                                    ) { viewModel.tipoFilter = nil }

                                    ForEach(Evento.TipoEvento.allCases, id: \.rawValue) { tipo in
                                        FilterChipButton(
                                            title: tipo.label,
                                            isSelected: viewModel.tipoFilter == tipo.rawValue
                                        ) { viewModel.tipoFilter = tipo.rawValue }
                                    }
                                }
                                .padding(.horizontal)
                            }
                        }
                        .padding(.vertical, 8)

                        if viewModel.filtered.isEmpty {
                            Spacer()
                            Text("Nenhum evento encontrado")
                                .foregroundColor(.secondary)
                            Spacer()
                        } else {
                            List {
                                ForEach(viewModel.filtered) { evento in
                                    EventoCard(
                                        evento: evento,
                                        onEdit: {
                                            editingEvento = evento
                                            showForm = true
                                        },
                                        onCancelar: {
                                            confirmAction = ConfirmAction(title: "Cancelar evento?") {
                                                if let id = evento.id { Task { await viewModel.cancelar(id: id) } }
                                            }
                                        },
                                        onDelete: {
                                            confirmAction = ConfirmAction(title: "Excluir evento?") {
                                                if let id = evento.id { Task { await viewModel.delete(id: id) } }
                                            }
                                        }
                                    )
                                    .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
                                    .listRowBackground(Color.clear)
                                    .listRowSeparator(.hidden)
                                }
                            }
                            .listStyle(.plain)
                        }
                    }
                }
            }
            .navigationTitle("Eventos")
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Dados Teste") { Task { await viewModel.seedTestData() } }
                        .font(.appCaption)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button { editingEvento = nil; showForm = true } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showForm) {
                EventoFormView(evento: editingEvento) { evento in
                    Task {
                        await viewModel.save(evento)
                        showForm = false
                    }
                }
            }
            .alert(item: $confirmAction) { action in
                Alert(
                    title: Text("Confirmar"),
                    message: Text(action.title),
                    primaryButton: .destructive(Text("Confirmar")) { action.action() },
                    secondaryButton: .cancel(Text("Cancelar"))
                )
            }
            .overlay(alignment: .bottom) {
                if let toast = viewModel.toast {
                    ToastView(message: toast)
                        .padding(.bottom, 24)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                                viewModel.toast = nil
                            }
                        }
                }
            }
            .animation(.easeInOut, value: viewModel.toast)
        }
    }
}

private struct EventoCard: View {
    let evento: Evento
    let onEdit: () -> Void
    let onCancelar: () -> Void
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(evento.nome)
                        .font(.appHeadline)
                    Text("\(evento.dataFormatada) • \(evento.horario)")
                        .font(.appCaption)
                        .foregroundColor(.secondary)
                    if let local = evento.local, !local.isEmpty {
                        Text(local)
                            .font(.appCaption)
                            .foregroundColor(.secondary)
                    }
                }
                Spacer()
                HStack(spacing: 0) {
                    if !evento.cancelado {
                        Button(action: onEdit) {
                            Image(systemName: "pencil")
                        }
                        .buttonStyle(.plain)
                        .padding(8)

                        Button(action: onCancelar) {
                            Image(systemName: "slash.circle")
                                .foregroundColor(.statusWarning)
                        }
                        .buttonStyle(.plain)
                        .padding(8)
                    }
                    Button(action: onDelete) {
                        Image(systemName: "trash")
                            .foregroundColor(.statusError)
                    }
                    .buttonStyle(.plain)
                    .padding(8)
                }
            }

            HStack(spacing: 6) {
                StatusBadge(text: evento.tipoLabel, variant: .info)
                if evento.cancelado {
                    StatusBadge(text: "Cancelado", variant: .error)
                }
                Text("Máx: \(evento.maxMinistros)")
                    .font(.appCaption)
                    .foregroundColor(.secondary)
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
    }
}

struct FilterChipButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.appCaption)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(isSelected ? Color.appPrimary : Color(.tertiarySystemBackground), in: Capsule())
                .foregroundColor(isSelected ? .white : .primary)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    EventosView()
}
