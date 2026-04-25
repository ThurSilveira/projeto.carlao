import Foundation

final class EventoService {
    private let client = APIClient.shared

    func fetchAll() async throws -> [Evento] {
        try await client.get(APIEndpoints.eventos)
    }

    func create(_ evento: Evento) async throws -> Evento {
        try await client.post(APIEndpoints.eventos, body: evento)
    }

    func update(id: Int, evento: Evento) async throws -> Evento {
        try await client.put(APIEndpoints.evento(id: id), body: evento)
    }

    func cancelar(id: Int) async throws -> Evento {
        // Backend: PUT /eventos/{id}/cancelar
        struct Empty: Encodable {}
        return try await client.put(APIEndpoints.cancelarEvento(id: id), body: Empty())
    }

    func delete(id: Int) async throws {
        try await client.delete(APIEndpoints.evento(id: id))
    }

    func seedTestData() async throws {
        try await client.postEmpty(APIEndpoints.seed)
    }
}
