import Foundation

final class AuditoriaService {
    private let client = APIClient.shared

    func fetchAll() async throws -> [LogAuditoria] {
        try await client.get(APIEndpoints.auditoria)
    }
}
