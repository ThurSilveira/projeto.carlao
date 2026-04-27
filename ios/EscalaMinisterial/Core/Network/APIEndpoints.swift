import Foundation

enum APIEndpoints {
    static let baseURL = "https://escala-ministerial-api.onrender.com/api"

    // Ministros
    static let ministros = "\(baseURL)/ministros"
    static func ministro(id: Int) -> String { "\(baseURL)/ministros/\(id)" }
    static func indisponibilidades(ministroId: Int) -> String { "\(baseURL)/ministros/\(ministroId)/indisponibilidades" }
    static func indisponibilidade(ministroId: Int, id: Int) -> String { "\(baseURL)/ministros/\(ministroId)/indisponibilidades/\(id)" }

    // Eventos
    static let eventos = "\(baseURL)/eventos"
    static func evento(id: Int) -> String { "\(baseURL)/eventos/\(id)" }
    static func cancelarEvento(id: Int) -> String { "\(baseURL)/eventos/\(id)/cancelar" }

    // Escalas
    static let escalas = "\(baseURL)/escalas"
    static func escala(id: Int) -> String { "\(baseURL)/escalas/\(id)" }
    static func gerarEscala(eventoId: Int) -> String { "\(baseURL)/escalas/gerar/\(eventoId)" }
    static func aprovarEscala(id: Int) -> String { "\(baseURL)/escalas/\(id)/aprovar" }
    static func cancelarEscala(id: Int) -> String { "\(baseURL)/escalas/\(id)/cancelar" }

    // Feedbacks
    static let feedbacks = "\(baseURL)/feedbacks"
    static func feedback(id: Int) -> String { "\(baseURL)/feedbacks/\(id)" }
    static func responderFeedback(id: Int) -> String { "\(baseURL)/feedbacks/\(id)/responder" }

    // Auditoria
    static let auditoria = "\(baseURL)/auditoria"

    // Seed
    static let seed = "\(baseURL)/seed"
}
