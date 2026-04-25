import Foundation

enum FeedbackViewState {
    case loading
    case success
    case error(String)
}

@MainActor
final class FeedbackViewModel: ObservableObject {
    @Published var state: FeedbackViewState = .loading
    @Published var feedbacks: [Feedback] = []
    @Published var toast: String? = nil

    private let service = FeedbackService()

    init() {
        Task { await load() }
    }

    var mediaNota: Double {
        guard !feedbacks.isEmpty else { return 0 }
        return Double(feedbacks.reduce(0) { $0 + $1.nota }) / Double(feedbacks.count)
    }

    var pendentes: Int {
        feedbacks.filter { $0.status == Feedback.StatusFeedback.pendente.rawValue }.count
    }

    func load() async {
        state = .loading
        do {
            feedbacks = try await service.fetchAll()
            state = .success
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func responder(id: Int, resposta: String) async {
        do {
            let updated = try await service.responder(id: id, resposta: resposta)
            if let idx = feedbacks.firstIndex(where: { $0.id == id }) {
                feedbacks[idx] = updated
            }
            toast = "Resposta enviada"
        } catch {
            toast = error.localizedDescription
        }
    }
}
