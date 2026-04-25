import SwiftUI

extension Color {
    // Primary palette — mirrors Android Purple40
    static let appPrimary = Color(red: 0.4, green: 0.314, blue: 0.643)

    // Status colors
    static let statusSuccess = Color(red: 0.298, green: 0.686, blue: 0.314)  // Green40
    static let statusError   = Color(red: 0.690, green: 0.000, blue: 0.125)  // Red40
    static let statusWarning = Color(red: 0.961, green: 0.620, blue: 0.043)  // Amber40
    static let statusInfo    = Color(red: 0.082, green: 0.396, blue: 0.753)  // Blue40
    static let statusNeutral = Color(red: 0.384, green: 0.357, blue: 0.443)  // PurpleGrey40

    // Background tints (8 % opacity variants for cards)
    static func tinted(_ base: Color) -> Color {
        base.opacity(0.10)
    }
}
