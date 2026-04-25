import Foundation

enum DateUtils {
    private static let dateFormatter: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd"
        f.locale = Locale(identifier: "pt_BR")
        return f
    }()

    private static let displayDateFormatter: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "dd/MM/yyyy"
        f.locale = Locale(identifier: "pt_BR")
        return f
    }()

    private static let dateTimeFormatter: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()

    private static let dateTimeFormatterNoFrac: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime]
        return f
    }()

    private static let displayDateTimeFormatter: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "dd/MM/yyyy HH:mm"
        f.locale = Locale(identifier: "pt_BR")
        return f
    }()

    static func parseDate(_ string: String) -> Date? {
        dateFormatter.date(from: string)
    }

    static func formatDate(_ date: Date) -> String {
        displayDateFormatter.string(from: date)
    }

    static func parseDateTime(_ string: String) -> Date? {
        dateTimeFormatter.date(from: string)
            ?? dateTimeFormatterNoFrac.date(from: string)
    }

    static func formatDateTime(_ date: Date) -> String {
        displayDateTimeFormatter.string(from: date)
    }

    static func todayString() -> String {
        dateFormatter.string(from: Date())
    }
}
