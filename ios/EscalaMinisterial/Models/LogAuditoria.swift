import Foundation

struct LogAuditoria: Codable, Identifiable {
    var id: Int?
    var entidade: String
    var acao: String
    var statusAnterior: String?
    var statusNovo: String?
    var realizadoPorId: String?
    var dataHora: String

    enum TipoAcao: String, CaseIterable {
        case criado      = "CRIADO"
        case atualizado  = "ATUALIZADO"
        case deletado    = "DELETADO"
        case aprovado    = "APROVADO"
        case cancelado   = "CANCELADO"
        case substituido = "SUBSTITUIDO"
        case confirmado  = "CONFIRMADO"
        case notificado  = "NOTIFICADO"

        var label: String {
            switch self {
            case .criado:      return "Criado"
            case .atualizado:  return "Atualizado"
            case .deletado:    return "Deletado"
            case .aprovado:    return "Aprovado"
            case .cancelado:   return "Cancelado"
            case .substituido: return "Substituído"
            case .confirmado:  return "Confirmado"
            case .notificado:  return "Notificado"
            }
        }
    }

    var acaoEnum: TipoAcao? { TipoAcao(rawValue: acao) }

    var dataHoraFormatada: String {
        if let date = DateUtils.parseDateTime(dataHora) {
            return DateUtils.formatDateTime(date)
        }
        return dataHora
    }
}
