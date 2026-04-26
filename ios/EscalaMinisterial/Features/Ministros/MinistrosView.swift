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

    private var avatarColor: Color {
        ministro.ativo ? .appPrimary : .statusNeutral
    }

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            AvatarView(nome: ministro.nome, size: 44, color: avatarColor)

            VStack(alignment: .leading, spacing: 3) {
                HStack(spacing: 8) {
                    Text(ministro.nome)
                        .font(.appTitleMedium)
                    if !ministro.ativo {
                        StatusBadge(text: "Inativo", variant: .neutral)
                    }
                }
                Text(ministro.funcaoLabel)
                    .font(.appBodySmall)
                    .foregroundStyle(Color.secondary)
                if let telefone = ministro.telefone, !telefone.isEmpty {
                    Label(telefone, systemImage: "phone")
                        .font(.appCaption)
                        .foregroundStyle(Color.secondary)
                        .labelStyle(.titleAndIcon)
                }
            }

            Spacer()

            HStack(spacing: 0) {
                SmallIconButton(systemName: "calendar.badge.exclamationmark", color: .appSecondary, action: onIndisponibilidades)
                SmallIconButton(systemName: "pencil", color: Color(.secondaryLabel), action: onEdit)
                SmallIconButton(systemName: "trash", color: .statusError, action: onDelete)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 14)
        .background(Color(.secondarySystemBackground), in: RoundedCornerShape(radius: 14))
    }
}

private struct SmallIconButton: View {
    let systemName: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: systemName)
                .font(.system(size: 14))
                .foregroundStyle(color)
                .frame(width: 34, height: 34)
        }
        .buttonStyle(.plain)
    }
}

private struct RoundedCornerShape: Shape {
    let radius: CGFloat
    func path(in rect: CGRect) -> Path {
        Path(UIBezierPath(roundedRect: rect, cornerRadius: radius).cgPath)
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
