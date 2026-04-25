import Foundation

struct DashboardStats {
    var totalMinistros: Int = 0
    var ministrosAtivos: Int = 0
    var totalEventos: Int = 0
    var eventosAtivos: Int = 0
    var totalEscalas: Int = 0
    var escalasAprovadas: Int = 0
    var feedbacksPendentes: Int = 0
    var mediaNota: Double = 0
}

enum DashboardViewState {
    case loading
    case success(DashboardStats)
    case error(String)
}

@MainActor
final class DashboardViewModel: ObservableObject {
    @Published var state: DashboardViewState = .loading

    private let ministroService = MinistroService()
    private let eventoService = EventoService()
    private let escalaService = EscalaService()
    private let feedbackService = FeedbackService()

    init() {
        Task { await load() }
    }

    func load() async {
        state = .loading
        do {
            async let ministros = ministroService.fetchAll()
            async let eventos = eventoService.fetchAll()
            async let escalas = escalaService.fetchAll()
            async let feedbacks = feedbackService.fetchAll()

            let (ms, evs, ess, fbs) = try await (ministros, eventos, escalas, feedbacks)

            let totalNota = fbs.reduce(0) { $0 + $1.nota }
            let media = fbs.isEmpty ? 0.0 : Double(totalNota) / Double(fbs.count)

            let stats = DashboardStats(
                totalMinistros: ms.count,
                ministrosAtivos: ms.filter(\.ativo).count,
                totalEventos: evs.count,
                eventosAtivos: evs.filter { !$0.cancelado }.count,
                totalEscalas: ess.count,
                escalasAprovadas: ess.filter { $0.status == "APROVADA" }.count,
                feedbacksPendentes: fbs.filter { $0.status == "PENDENTE" }.count,
                mediaNota: media
            )
            state = .success(stats)
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}
