import Foundation

final class EscalaService {
    private let client = APIClient.shared

    func fetchAll() async throws -> [Escala] {
        try await client.get(APIEndpoints.escalas)
    }

    func gerar(eventoId: Int) async throws -> Escala {
        // Backend: POST /escalas/gerar/{eventoId}
        struct Empty: Encodable {}
        return try await client.post(APIEndpoints.gerarEscala(eventoId: eventoId), body: Empty())
    }

    func aprovar(id: Int) async throws -> Escala {
        // Backend: PUT /escalas/{id}/aprovar
        struct Empty: Encodable {}
        return try await client.put(APIEndpoints.aprovarEscala(id: id), body: Empty())
    }

    func cancelar(id: Int) async throws -> Escala {
        // Backend: PUT /escalas/{id}/cancelar
        struct Empty: Encodable {}
        return try await client.put(APIEndpoints.cancelarEscala(id: id), body: Empty())
    }

    func delete(id: Int) async throws {
        try await client.delete(APIEndpoints.escala(id: id))
    }
}
