import Foundation

enum EventosViewState {
    case loading
    case success
    case error(String)
}

@MainActor
final class EventosViewModel: ObservableObject {
    @Published var state: EventosViewState = .loading
    @Published var eventos: [Evento] = []
    @Published var query: String = ""
    @Published var tipoFilter: String? = nil
    @Published var toast: String? = nil

    private let service = EventoService()

    init() {
        Task { await load() }
    }

    var filtered: [Evento] {
        eventos.filter { e in
            let matchQuery = query.isEmpty
                || e.nome.localizedCaseInsensitiveContains(query)
                || (e.local ?? "").localizedCaseInsensitiveContains(query)
            let matchTipo = tipoFilter == nil || e.tipoEvento == tipoFilter
            return matchQuery && matchTipo
        }
    }

    func load() async {
        state = .loading
        do {
            eventos = try await service.fetchAll()
            state = .success
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func save(_ evento: Evento) async {
        do {
            if let id = evento.id {
                let updated = try await service.update(id: id, evento: evento)
                if let idx = eventos.firstIndex(where: { $0.id == id }) {
                    eventos[idx] = updated
                }
            } else {
                let created = try await service.create(evento)
                eventos.append(created)
            }
            toast = "Evento salvo"
        } catch {
            toast = error.localizedDescription
        }
    }

    func cancelar(id: Int) async {
        do {
            let updated = try await service.cancelar(id: id)
            if let idx = eventos.firstIndex(where: { $0.id == id }) {
                eventos[idx] = updated
            }
            toast = "Evento cancelado"
        } catch {
            toast = error.localizedDescription
        }
    }

    func delete(id: Int) async {
        do {
            try await service.delete(id: id)
            eventos.removeAll { $0.id == id }
            toast = "Evento removido"
        } catch {
            toast = error.localizedDescription
        }
    }

    func seedTestData() async {
        do {
            try await service.seedTestData()
            await load()
            toast = "Dados de teste carregados"
        } catch {
            toast = error.localizedDescription
        }
    }
}
