import Foundation

enum EscalasViewState {
    case loading
    case success
    case error(String)
}

@MainActor
final class EscalasViewModel: ObservableObject {
    @Published var state: EscalasViewState = .loading
    @Published var escalas: [Escala] = []
    @Published var statusFilter: String? = nil
    @Published var toast: String? = nil

    private let service = EscalaService()

    init() {
        Task { await load() }
    }

    var filtered: [Escala] {
        guard let f = statusFilter else { return escalas }
        return escalas.filter { $0.status == f }
    }

    func load() async {
        state = .loading
        do {
            escalas = try await service.fetchAll()
            state = .success
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func gerar(eventoId: Int) async {
        do {
            let nova = try await service.gerar(eventoId: eventoId)
            escalas.insert(nova, at: 0)
            toast = "Escala gerada automaticamente"
        } catch {
            toast = error.localizedDescription
        }
    }

    func aprovar(id: Int) async {
        do {
            let updated = try await service.aprovar(id: id)
            if let idx = escalas.firstIndex(where: { $0.id == id }) {
                escalas[idx] = updated
            }
            toast = "Escala aprovada"
        } catch {
            toast = error.localizedDescription
        }
    }

    func cancelar(id: Int) async {
        do {
            let updated = try await service.cancelar(id: id)
            if let idx = escalas.firstIndex(where: { $0.id == id }) {
                escalas[idx] = updated
            }
            toast = "Escala cancelada"
        } catch {
            toast = error.localizedDescription
        }
    }

    func delete(id: Int) async {
        do {
            try await service.delete(id: id)
            escalas.removeAll { $0.id == id }
            toast = "Escala deletada"
        } catch {
            toast = error.localizedDescription
        }
    }
}
