import Foundation

struct Escala: Codable, Identifiable {
    var id: Int?
    var eventoId: Int?
    var eventoNome: String?
    var eventoData: String?
    var eventoHorario: String?
    var dataAtribuicao: String?
    var observacao: String?
    var status: String
    var escalaMinistros: [EscalaMinistro]

    enum StatusEscala: String, CaseIterable {
        case proposta   = "PROPOSTA"
        case aprovada   = "APROVADA"
        case confirmada = "CONFIRMADA"
        case cancelada  = "CANCELADA"

        var label: String {
            switch self {
            case .proposta:   return "Proposta"
            case .aprovada:   return "Aprovada"
            case .confirmada: return "Confirmada"
            case .cancelada:  return "Cancelada"
            }
        }
    }

    var statusEnum: StatusEscala? { StatusEscala(rawValue: status) }

    var eventoDataFormatada: String? {
        guard let d = eventoData, let date = DateUtils.parseDate(d) else { return eventoData }
        return DateUtils.formatDate(date)
    }
}

struct EscalaMinistro: Codable, Identifiable {
    var id: Int?
    var ministroId: Int
    var ministroNome: String?
    var ministroFuncao: String?
    var confirmacaoMinistro: Bool
    var substituido: Bool
}
