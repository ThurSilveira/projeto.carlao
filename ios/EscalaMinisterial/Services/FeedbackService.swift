import Foundation

struct RespostaRequest: Encodable {
    let resposta: String
}

final class FeedbackService {
    private let client = APIClient.shared

    func fetchAll() async throws -> [Feedback] {
        try await client.get(APIEndpoints.feedbacks)
    }

    func responder(id: Int, resposta: String) async throws -> Feedback {
        // Backend: PUT /feedbacks/{id}/responder
        try await client.put(APIEndpoints.responderFeedback(id: id), body: RespostaRequest(resposta: resposta))
    }
}
