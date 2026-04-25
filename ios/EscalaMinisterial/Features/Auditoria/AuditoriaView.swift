import SwiftUI

struct AuditoriaView: View {
    @StateObject private var viewModel = AuditoriaViewModel()

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
                                    isSelected: viewModel.entidadeFilter == nil
                                ) { viewModel.entidadeFilter = nil }

                                ForEach(AuditoriaViewModel.entidades, id: \.self) { entidade in
                                    FilterChipButton(
                                        title: entidade,
                                        isSelected: viewModel.entidadeFilter == entidade
                                    ) { viewModel.entidadeFilter = entidade }
                                }
                            }
                            .padding(.horizontal)
                            .padding(.vertical, 8)
                        }

                        if viewModel.filtered.isEmpty {
                            Spacer()
                            Text("Nenhum log encontrado")
                                .foregroundColor(.secondary)
                            Spacer()
                        } else {
                            List {
                                ForEach(viewModel.filtered) { log in
                                    LogCard(log: log)
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
            .navigationTitle("Auditoria")
        }
    }
}

private struct LogCard: View {
    let log: LogAuditoria

    private var acaoBadgeVariant: BadgeVariant {
        switch log.acaoEnum {
        case .criado:               return .success
        case .deletado:             return .error
        case .aprovado, .confirmado: return .info
        case .cancelado:            return .warning
        default:                    return .neutral
        }
    }

    private var acaoColor: Color {
        switch log.acaoEnum {
        case .criado:    return .statusSuccess
        case .deletado:  return .statusError
        case .aprovado:  return .statusInfo
        case .cancelado: return .statusWarning
        default:         return .secondary
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                HStack(spacing: 6) {
                    StatusBadge(text: log.entidade, variant: .neutral)
                    StatusBadge(
                        text: log.acaoEnum?.label ?? log.acao,
                        variant: acaoBadgeVariant
                    )
                }
                Spacer()
                Text(log.dataHoraFormatada)
                    .font(.appCaption)
                    .foregroundColor(.secondary)
            }

            if log.statusAnterior != nil || log.statusNovo != nil {
                HStack(spacing: 4) {
                    if let ant = log.statusAnterior {
                        Text("\(ant) →")
                            .font(.appCaption)
                            .foregroundColor(.secondary)
                    }
                    if let novo = log.statusNovo {
                        Text(novo)
                            .font(.appCaption)
                            .foregroundColor(acaoColor)
                    }
                }
            }

            if let por = log.realizadoPorId {
                Text("Por: \(por)")
                    .font(.appCaption)
                    .foregroundColor(.secondary)
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    AuditoriaView()
}
