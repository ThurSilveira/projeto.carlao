import Foundation

enum AuditoriaViewState {
    case loading
    case success
    case error(String)
}

@MainActor
final class AuditoriaViewModel: ObservableObject {
    @Published var state: AuditoriaViewState = .loading
    @Published var logs: [LogAuditoria] = []
    @Published var entidadeFilter: String? = nil

    private let service = AuditoriaService()

    static let entidades = ["Ministro", "Evento", "Escala", "Feedback"]

    init() {
        Task { await load() }
    }

    var filtered: [LogAuditoria] {
        guard let f = entidadeFilter else { return logs }
        return logs.filter { $0.entidade.localizedCaseInsensitiveContains(f) }
    }

    func load() async {
        state = .loading
        do {
            logs = try await service.fetchAll()
            state = .success
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}
