import SwiftUI

struct DashboardView: View {
    @StateObject private var viewModel = DashboardViewModel()

    var body: some View {
        NavigationStack {
            Group {
                switch viewModel.state {
                case .loading:
                    LoadingView()
                case .error(let msg):
                    ErrorView(message: msg) { Task { await viewModel.load() } }
                case .success(let stats):
                    ScrollView {
                        LazyVStack(alignment: .leading, spacing: 16) {
                            Text("Resumo Geral")
                                .font(.appHeadline)
                                .padding(.horizontal)

                            HStack(spacing: 12) {
                                StatCard(
                                    label: "Ministros",
                                    value: "\(stats.ministrosAtivos)",
                                    subtitle: "de \(stats.totalMinistros) ativos",
                                    icon: "person.3.fill",
                                    color: Color(red: 0.082, green: 0.396, blue: 0.753)
                                )
                                StatCard(
                                    label: "Eventos",
                                    value: "\(stats.eventosAtivos)",
                                    subtitle: "de \(stats.totalEventos) ativos",
                                    icon: "calendar",
                                    color: Color(red: 0.298, green: 0.686, blue: 0.314)
                                )
                            }
                            .padding(.horizontal)

                            HStack(spacing: 12) {
                                StatCard(
                                    label: "Escalas",
                                    value: "\(stats.escalasAprovadas)",
                                    subtitle: "de \(stats.totalEscalas) aprovadas",
                                    icon: "clock.fill",
                                    color: Color(red: 0.612, green: 0.153, blue: 0.690)
                                )
                                StatCard(
                                    label: "Feedbacks",
                                    value: "\(stats.feedbacksPendentes)",
                                    subtitle: "pendentes",
                                    icon: "bubble.left.fill",
                                    color: Color(red: 0.961, green: 0.620, blue: 0.043)
                                )
                            }
                            .padding(.horizontal)

                            Text("Indicadores")
                                .font(.appHeadline)
                                .padding(.horizontal)

                            HStack(spacing: 12) {
                                StatCard(
                                    label: "Nota Média",
                                    value: stats.mediaNota > 0 ? String(format: "%.1f", stats.mediaNota) : "—",
                                    subtitle: "feedback dos ministros",
                                    icon: "star.fill",
                                    color: Color(red: 1.0, green: 0.596, blue: 0.0)
                                )
                                StatCard(
                                    label: "Auditoria",
                                    value: "\(stats.totalEscalas + stats.totalMinistros)",
                                    subtitle: "registros totais",
                                    icon: "chart.bar.fill",
                                    color: Color(red: 0.376, green: 0.490, blue: 0.545)
                                )
                            }
                            .padding(.horizontal)
                        }
                        .padding(.vertical)
                    }
                }
            }
            .navigationTitle("Dashboard")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { Task { await viewModel.load() } } label: {
                        Image(systemName: "arrow.clockwise")
                    }
                }
            }
        }
    }
}

private struct StatCard: View {
    let label: String
    let value: String
    let subtitle: String
    let icon: String
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 6) {
                Image(systemName: icon)
                    .foregroundColor(color)
                Text(label)
                    .font(.appLabel)
                    .foregroundColor(.secondary)
            }
            Text(value)
                .font(.system(size: 28, weight: .bold))
                .foregroundColor(color)
            Text(subtitle)
                .font(.appCaption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(color.opacity(0.08), in: RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    DashboardView()
}
