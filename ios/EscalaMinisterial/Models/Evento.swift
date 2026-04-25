import Foundation

struct Evento: Codable, Identifiable {
    var id: Int?
    var nome: String
    var data: String
    var horario: String
    var tipoEvento: String
    var tipoEspecificado: String?
    var maxMinistros: Int
    var local: String?
    var cancelado: Bool

    enum TipoEvento: String, CaseIterable, Codable {
        case missaParoquial = "MISSA_PAROQUIAL"
        case missaEspecial  = "MISSA_ESPECIAL"
        case retiro         = "RETIRO"
        case batizado       = "BATIZADO"
        case casamento      = "CASAMENTO"
        case adoracao       = "ADORACAO"
        case outro          = "OUTRO"

        var label: String {
            switch self {
            case .missaParoquial: return "Missa Paroquial"
            case .missaEspecial:  return "Missa Especial"
            case .retiro:         return "Retiro"
            case .batizado:       return "Batizado"
            case .casamento:      return "Casamento"
            case .adoracao:       return "Adoração"
            case .outro:          return "Outro"
            }
        }
    }

    var tipoLabel: String {
        if tipoEvento == TipoEvento.outro.rawValue, let esp = tipoEspecificado, !esp.isEmpty {
            return esp
        }
        return TipoEvento(rawValue: tipoEvento)?.label ?? tipoEvento
    }

    var dataFormatada: String {
        guard let date = DateUtils.parseDate(data) else { return data }
        return DateUtils.formatDate(date)
    }
}
