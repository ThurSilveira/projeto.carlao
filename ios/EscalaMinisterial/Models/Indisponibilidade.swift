import Foundation

struct Indisponibilidade: Codable, Identifiable {
    var id: Int?
    var ministroId: Int?
    var data: String
    var horarioInicio: String?
    var horarioFim: String?
    var motivo: String?

    var dataFormatada: String {
        guard let date = DateUtils.parseDate(data) else { return data }
        return DateUtils.formatDate(date)
    }
}
