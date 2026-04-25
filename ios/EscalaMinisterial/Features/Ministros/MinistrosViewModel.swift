import Foundation

enum MinistrosViewState {
    case loading
    case success
    case error(String)
}

struct IndisponibilidadeSheet {
    var ministroId: Int
    var ministroNome: String
    var items: [Indisponibilidade]
    var isLoading: Bool = false
}

@MainActor
final class MinistrosViewModel: ObservableObject {
    @Published var state: MinistrosViewState = .loading
    @Published var ministros: [Ministro] = []
    @Published var query: String = ""
    @Published var soAtivos: Bool = false
    @Published var toast: String? = nil
    @Published var indisponibilidadeSheet: IndisponibilidadeSheet? = nil

    private let service = MinistroService()

    init() {
        Task { await load() }
    }

    var filtered: [Ministro] {
        ministros.filter { m in
            let matchQuery = query.isEmpty
                || m.nome.localizedCaseInsensitiveContains(query)
                || m.email.localizedCaseInsensitiveContains(query)
            let matchAtivo = !soAtivos || m.ativo
            return matchQuery && matchAtivo
        }
    }

    func load() async {
        state = .loading
        do {
            ministros = try await service.fetchAll()
            state = .success
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func save(_ ministro: Ministro) async {
        do {
            if let id = ministro.id {
                let updated = try await service.update(id: id, ministro: ministro)
                if let idx = ministros.firstIndex(where: { $0.id == id }) {
                    ministros[idx] = updated
                }
            } else {
                let created = try await service.create(ministro)
                ministros.append(created)
            }
            toast = "Ministro salvo com sucesso"
        } catch {
            toast = error.localizedDescription
        }
    }

    func delete(id: Int) async {
        do {
            try await service.delete(id: id)
            ministros.removeAll { $0.id == id }
            toast = "Ministro removido"
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

    // MARK: - Indisponibilidades

    func openIndisponibilidades(for ministro: Ministro) async {
        guard let id = ministro.id else { return }
        indisponibilidadeSheet = IndisponibilidadeSheet(ministroId: id, ministroNome: ministro.nome, items: [], isLoading: true)
        do {
            let items = try await service.fetchIndisponibilidades(ministroId: id)
            indisponibilidadeSheet?.items = items
            indisponibilidadeSheet?.isLoading = false
        } catch {
            indisponibilidadeSheet?.isLoading = false
            toast = error.localizedDescription
        }
    }

    func createIndisponibilidade(_ ind: Indisponibilidade) async {
        guard let sheet = indisponibilidadeSheet else { return }
        do {
            let created = try await service.createIndisponibilidade(ministroId: sheet.ministroId, indisponibilidade: ind)
            indisponibilidadeSheet?.items.append(created)
            toast = "Indisponibilidade criada"
        } catch {
            toast = error.localizedDescription
        }
    }

    func updateIndisponibilidade(_ ind: Indisponibilidade) async {
        guard let sheet = indisponibilidadeSheet, let id = ind.id else { return }
        do {
            let updated = try await service.updateIndisponibilidade(ministroId: sheet.ministroId, id: id, indisponibilidade: ind)
            if let idx = indisponibilidadeSheet?.items.firstIndex(where: { $0.id == id }) {
                indisponibilidadeSheet?.items[idx] = updated
            }
            toast = "Indisponibilidade atualizada"
        } catch {
            toast = error.localizedDescription
        }
    }

    func deleteIndisponibilidade(id: Int) async {
        guard let sheet = indisponibilidadeSheet else { return }
        do {
            try await service.deleteIndisponibilidade(ministroId: sheet.ministroId, id: id)
            indisponibilidadeSheet?.items.removeAll { $0.id == id }
            toast = "Indisponibilidade removida"
        } catch {
            toast = error.localizedDescription
        }
    }
}
