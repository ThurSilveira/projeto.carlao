import SwiftUI

extension Color {
    // MARK: - Primary (warm brown)
    static let appPrimary          = Color(hex: "#583400")
    static let appPrimaryContainer = Color(hex: "#FFDDAE")
    static let appOnPrimary        = Color.white
    static let appOnPrimaryContainer = Color(hex: "#1C0A00")

    // MARK: - Secondary (teal)
    static let appSecondary          = Color(hex: "#0F766E")
    static let appSecondaryContainer = Color(hex: "#CCFBF1")
    static let appOnSecondary        = Color.white

    // MARK: - Dark-mode variants
    static let appPrimaryDark          = Color(hex: "#D4956A")
    static let appPrimaryContainerDark = Color(hex: "#412900")
    static let appSecondaryDark        = Color(hex: "#14B8A6")
    static let appSecondaryContainerDark = Color(hex: "#134E4A")

    // MARK: - Surface / Background
    static let appBackground      = Color(hex: "#F1F5F9")
    static let appSurface         = Color.white
    static let appBackgroundDark  = Color(hex: "#141927")
    static let appSurfaceDark     = Color(hex: "#1E2535")

    // MARK: - Semantic status colors
    static let statusSuccess          = Color(hex: "#16A34A")
    static let statusSuccessContainer = Color(hex: "#DCFCE7")
    static let statusError            = Color(hex: "#DC2626")
    static let statusErrorContainer   = Color(hex: "#FEE2E2")
    static let statusWarning          = Color(hex: "#D97706")
    static let statusWarningContainer = Color(hex: "#FEF3C7")
    static let statusInfo             = Color(hex: "#2563EB")
    static let statusInfoContainer    = Color(hex: "#DBEAFE")
    static let statusNeutral          = Color(hex: "#64748B")
    static let statusNeutralContainer = Color(hex: "#F1F5F9")

    // MARK: - Purple (escalas aprovadas KPI)
    static let appPurple          = Color(hex: "#7C3AED")
    static let appPurpleContainer = Color(hex: "#EDE9FE")

    // MARK: - Hex initializer
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8)  & 0xFF) / 255
        let b = Double(int         & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}

// MARK: - Dynamic (light/dark) helpers
extension Color {
    static func appPrimaryAdaptive(dark: Bool) -> Color {
        dark ? .appPrimaryDark : .appPrimary
    }
    static func appBackgroundAdaptive(dark: Bool) -> Color {
        dark ? .appBackgroundDark : .appBackground
    }
    static func appSurfaceAdaptive(dark: Bool) -> Color {
        dark ? .appSurfaceDark : .appSurface
    }
}
