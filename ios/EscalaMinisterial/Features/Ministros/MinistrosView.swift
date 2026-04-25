import SwiftUI

struct MinistrosView: View {
    @StateObject private var viewModel = MinistrosViewModel()
    @State private var showForm = false
    @State private var editingMinistro: Ministro? = nil
    @State private var deletingId: Int? = nil
    @State private var showIndisponibilidades = false

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
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Image(systemName: "magnifyingglass")
                                    .foregroundColor(.secondary)
                                TextField("Buscar por nome ou e-mail", text: $viewModel.query)
                                    .autocorrectionDisabled()
                            }
                            .padding(10)
                            .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 10))
                            .padding(.horizontal)

                            Toggle(isOn: $viewModel.soAtivos) {
                                Text("Somente ativos")
                                    .font(.appBody)
                            }
                            .padding(.horizontal)
                        }
                        .padding(.vertical, 8)

                        if viewModel.filtered.isEmpty {
                            Spacer()
                            Text("Nenhum ministro encontrado")
                                .foregroundColor(.secondary)
                            Spacer()
                        } else {
                            List {
                                ForEach(viewModel.filtered) { ministro in
                                    MinistroCard(
                                        ministro: ministro,
                                        onEdit: {
                                            editingMinistro = ministro
                                            showForm = true
                                        },
                                        onDelete: {
                                            deletingId = ministro.id
                                        },
                                        onIndisponibilidades: {
                                            Task {
                                                await viewModel.openIndisponibilidades(for: ministro)
                                                showIndisponibilidades = true
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
            .navigationTitle("Ministros")
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Dados Teste") { Task { await viewModel.seedTestData() } }
                        .font(.appCaption)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button { editingMinistro = nil; showForm = true } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showForm) {
                MinistroFormView(ministro: editingMinistro) { ministro in
                    Task {
                        await viewModel.save(ministro)
                        showForm = false
                    }
                }
            }
            .sheet(isPresented: $showIndisponibilidades) {
                if let sheet = viewModel.indisponibilidadeSheet {
                    IndisponibilidadeView(
                        sheet: sheet,
                        onCreate: { ind in Task { await viewModel.createIndisponibilidade(ind) } },
                        onUpdate: { ind in Task { await viewModel.updateIndisponibilidade(ind) } },
                        onDelete: { id in Task { await viewModel.deleteIndisponibilidade(id: id) } }
                    )
                }
            }
            .alert("Confirmar exclusão", isPresented: Binding(
                get: { deletingId != nil },
                set: { if !$0 { deletingId = nil } }
            )) {
                Button("Cancelar", role: .cancel) { deletingId = nil }
                Button("Remover", role: .destructive) {
                    if let id = deletingId {
                        Task { await viewModel.delete(id: id) }
                        deletingId = nil
                    }
                }
            } message: {
                Text("Deseja remover este ministro?")
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

private struct MinistroCard: View {
    let ministro: Ministro
    let onEdit: () -> Void
    let onDelete: () -> Void
    let onIndisponibilidades: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(ministro.nome)
                        .font(.appHeadline)
                    Text(ministro.email)
                        .font(.appCaption)
                        .foregroundColor(.secondary)
                }
                Spacer()
                HStack(spacing: 0) {
                    Button(action: onIndisponibilidades) {
                        Image(systemName: "calendar.badge.exclamationmark")
                            .foregroundColor(.appPrimary)
                    }
                    .buttonStyle(.plain)
                    .padding(8)

                    Button(action: onEdit) {
                        Image(systemName: "pencil")
                            .foregroundColor(.primary)
                    }
                    .buttonStyle(.plain)
                    .padding(8)

                    Button(action: onDelete) {
                        Image(systemName: "trash")
                            .foregroundColor(.statusError)
                    }
                    .buttonStyle(.plain)
                    .padding(8)
                }
            }

            HStack(spacing: 6) {
                StatusBadge(
                    text: ministro.ativo ? "Ativo" : "Inativo",
                    variant: ministro.ativo ? .success : .neutral
                )
                StatusBadge(text: ministro.funcaoLabel, variant: .info)
            }

            if let telefone = ministro.telefone, !telefone.isEmpty {
                Text(telefone)
                    .font(.appCaption)
                    .foregroundColor(.secondary)
            }

            if let agendadas = ministro.escalasAgendadas, !agendadas.isEmpty {
                Divider()
                Text("Escalas agendadas:")
                    .font(.appLabel)
                    .foregroundColor(.secondary)
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 6) {
                        ForEach(agendadas, id: \.self) { dateStr in
                            let label = DateUtils.parseDate(dateStr).map { DateUtils.formatDate($0) } ?? dateStr
                            Text(label)
                                .font(.appLabel)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 3)
                                .background(Color(.tertiarySystemBackground), in: Capsule())
                        }
                    }
                }
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
    }
}

struct ToastView: View {
    let message: String
    var body: some View {
        Text(message)
            .font(.appBody)
            .foregroundColor(.white)
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color.black.opacity(0.75), in: Capsule())
    }
}

#Preview {
    MinistrosView()
}
