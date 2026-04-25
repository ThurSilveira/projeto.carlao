import Foundation

final class MinistroService {
    private let client = APIClient.shared

    func fetchAll() async throws -> [Ministro] {
        try await client.get(APIEndpoints.ministros)
    }

    func create(_ ministro: Ministro) async throws -> Ministro {
        try await client.post(APIEndpoints.ministros, body: ministro)
    }

    func update(id: Int, ministro: Ministro) async throws -> Ministro {
        try await client.put(APIEndpoints.ministro(id: id), body: ministro)
    }

    func delete(id: Int) async throws {
        try await client.delete(APIEndpoints.ministro(id: id))
    }

    // MARK: - Indisponibilidades

    func fetchIndisponibilidades(ministroId: Int) async throws -> [Indisponibilidade] {
        try await client.get(APIEndpoints.indisponibilidades(ministroId: ministroId))
    }

    func createIndisponibilidade(ministroId: Int, indisponibilidade: Indisponibilidade) async throws -> Indisponibilidade {
        try await client.post(APIEndpoints.indisponibilidades(ministroId: ministroId), body: indisponibilidade)
    }

    func updateIndisponibilidade(ministroId: Int, id: Int, indisponibilidade: Indisponibilidade) async throws -> Indisponibilidade {
        try await client.put(APIEndpoints.indisponibilidade(ministroId: ministroId, id: id), body: indisponibilidade)
    }

    func deleteIndisponibilidade(ministroId: Int, id: Int) async throws {
        try await client.delete(APIEndpoints.indisponibilidade(ministroId: ministroId, id: id))
    }

    // MARK: - Seed

    func seedTestData() async throws {
        try await client.postEmpty(APIEndpoints.seed)
    }
}
