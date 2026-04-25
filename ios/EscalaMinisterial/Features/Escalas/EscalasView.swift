import SwiftUI

struct EscalasView: View {
    @StateObject private var viewModel = EscalasViewModel()
    @State private var showGerarSheet = false
    @State private var confirmAction: EscalaConfirmAction? = nil

    struct EscalaConfirmAction: Identifiable {
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
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                FilterChipButton(
                                    title: "Todos",
                                    isSelected: viewModel.statusFilter == nil
                                ) { viewModel.statusFilter = nil }

                                ForEach(Escala.StatusEscala.allCases, id: \.rawValue) { s in
                                    FilterChipButton(
                                        title: s.label,
                                        isSelected: viewModel.statusFilter == s.rawValue
                                    ) { viewModel.statusFilter = s.rawValue }
                                }
                            }
                            .padding(.horizontal)
                            .padding(.vertical, 8)
                        }

                        if viewModel.filtered.isEmpty {
                            Spacer()
                            Text("Nenhuma escala encontrada")
                                .foregroundColor(.secondary)
                            Spacer()
                        } else {
                            List {
                                ForEach(viewModel.filtered) { escala in
                                    EscalaCard(
                                        escala: escala,
                                        onAprovar: {
                                            if let id = escala.id {
                                                confirmAction = EscalaConfirmAction(title: "Aprovar esta escala?") {
                                                    Task { await viewModel.aprovar(id: id) }
                                                }
                                            }
                                        },
                                        onCancelar: {
                                            if let id = escala.id {
                                                confirmAction = EscalaConfirmAction(title: "Cancelar esta escala?") {
                                                    Task { await viewModel.cancelar(id: id) }
                                                }
                                            }
                                        },
                                        onDeletar: {
                                            if let id = escala.id {
                                                confirmAction = EscalaConfirmAction(title: "Deletar permanentemente esta escala?") {
                                                    Task { await viewModel.delete(id: id) }
                                                }
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
            .navigationTitle("Escalas")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { showGerarSheet = true } label: {
                        Image(systemName: "arrow.clockwise.circle")
                    }
                }
            }
            .sheet(isPresented: $showGerarSheet) {
                GerarEscalaView { eventoId in
                    Task { await viewModel.gerar(eventoId: eventoId) }
                    showGerarSheet = false
                }
            }
            .alert(item: $confirmAction) { ca in
                Alert(
                    title: Text("Confirmar"),
                    message: Text(ca.title),
                    primaryButton: .default(Text("Confirmar")) { ca.action() },
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

private struct EscalaCard: View {
    let escala: Escala
    let onAprovar: () -> Void
    let onCancelar: () -> Void
    let onDeletar: () -> Void

    private var badgeVariant: BadgeVariant {
        switch escala.statusEnum {
        case .aprovada:   return .success
        case .cancelada:  return .error
        case .confirmada: return .info
        case .proposta:   return .warning
        case .none:       return .neutral
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(escala.eventoNome ?? "Evento desconhecido")
                        .font(.appHeadline)
                    if let dataFmt = escala.eventoDataFormatada {
                        Text("\(dataFmt) • \(escala.eventoHorario ?? "")")
                            .font(.appCaption)
                            .foregroundColor(.secondary)
                    }
                    Text("\(escala.escalaMinistros.count) ministros")
                        .font(.appCaption)
                        .foregroundColor(.secondary)
                }
                Spacer()
                HStack(spacing: 0) {
                    if escala.status == Escala.StatusEscala.proposta.rawValue {
                        Button(action: onAprovar) {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.statusSuccess)
                        }
                        .buttonStyle(.plain)
                        .padding(8)

                        Button(action: onCancelar) {
                            Image(systemName: "slash.circle")
                                .foregroundColor(.secondary)
                        }
                        .buttonStyle(.plain)
                        .padding(8)
                    }
                    Button(action: onDeletar) {
                        Image(systemName: "trash")
                            .foregroundColor(.statusError)
                    }
                    .buttonStyle(.plain)
                    .padding(8)
                }
            }

            StatusBadge(
                text: escala.statusEnum?.label ?? escala.status,
                variant: badgeVariant
            )

            if let obs = escala.observacao, !obs.isEmpty {
                Text(obs)
                    .font(.appCaption)
                    .foregroundColor(.secondary)
            }

            if !escala.escalaMinistros.isEmpty {
                Divider()
                Text(escala.escalaMinistros.compactMap(\.ministroNome).joined(separator: ", "))
                    .font(.appCaption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    EscalasView()
}
