import Foundation

struct Feedback: Codable, Identifiable {
    var id: Int?
    var ministroId: Int
    var ministroNome: String?
    var eventoId: Int
    var eventoNome: String?
    var nota: Int
    var comentario: String?
    var dataEnvio: String?
    var status: String
    var resposta: String?

    enum StatusFeedback: String, CaseIterable {
        case pendente   = "PENDENTE"
        case respondido = "RESPONDIDO"
        case arquivado  = "ARQUIVADO"

        var label: String {
            switch self {
            case .pendente:   return "Pendente"
            case .respondido: return "Respondido"
            case .arquivado:  return "Arquivado"
            }
        }
    }

    var statusEnum: StatusFeedback? { StatusFeedback(rawValue: status) }

    var dataEnvioFormatada: String? {
        guard let d = dataEnvio else { return nil }
        // Try datetime first (ISO 8601)
        if let date = DateUtils.parseDateTime(d) {
            return DateUtils.formatDateTime(date)
        }
        return d
    }
}
