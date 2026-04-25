import Foundation

struct Ministro: Codable, Identifiable {
    var id: Int?
    var nome: String
    var email: String
    var telefone: String?
    var dataNascimento: String?
    var observacoes: String?
    var ativo: Bool
    var funcao: String
    var funcaoEspecificada: String?
    var visitasAoInfermo: Bool
    var statusCurso: Bool
    var escalasMes: Int?
    var indisponibilidades: [Indisponibilidade]?
    var escalasAgendadas: [String]?

    enum FuncaoMinistro: String, CaseIterable, Codable {
        case eucaristia = "EUCARISTIA"
        case leitura = "LEITURA"
        case acolhimento = "ACOLHIMENTO"
        case musica = "MUSICA"
        case catequese = "CATEQUESE"
        case adoracao = "ADORACAO"
        case outro = "OUTRO"

        var label: String {
            switch self {
            case .eucaristia:  return "Eucaristia"
            case .leitura:     return "Leitura"
            case .acolhimento: return "Acolhimento"
            case .musica:      return "Música"
            case .catequese:   return "Catequese"
            case .adoracao:    return "Adoração"
            case .outro:       return "Outro"
            }
        }
    }

    var funcaoLabel: String {
        if funcao == FuncaoMinistro.outro.rawValue, let esp = funcaoEspecificada, !esp.isEmpty {
            return esp
        }
        return FuncaoMinistro(rawValue: funcao)?.label ?? funcao
    }
}
