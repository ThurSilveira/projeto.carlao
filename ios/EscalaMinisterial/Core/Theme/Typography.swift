import SwiftUI

// MARK: - Font families
// Fonts must be added to the app target + Info.plist under "Fonts provided by application".
// File names: Nunito-Regular.ttf, Nunito-Medium.ttf, Nunito-SemiBold.ttf, Nunito-Bold.ttf,
//             Nunito-ExtraBold.ttf, PlayfairDisplay-SemiBold.ttf, PlayfairDisplay-Bold.ttf

private func nunito(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
    let psName: String
    switch weight {
    case .bold:       psName = "Nunito-Bold"
    case .semibold:   psName = "Nunito-SemiBold"
    case .medium:     psName = "Nunito-Medium"
    case .heavy:      psName = "Nunito-ExtraBold"
    default:          psName = "Nunito-Regular"
    }
    return Font.custom(psName, size: size)
}

private func playfair(_ size: CGFloat, weight: Font.Weight = .semibold) -> Font {
    let psName = weight == .bold ? "PlayfairDisplay-Bold" : "PlayfairDisplay-SemiBold"
    return Font.custom(psName, size: size)
}

// MARK: - App type scale
extension Font {
    // Headlines — Playfair Display
    static let appDisplayLarge  = playfair(32, weight: .bold)
    static let appDisplayMedium = playfair(28, weight: .bold)
    static let appHeadlineLarge = playfair(24, weight: .semibold)
    static let appHeadline      = playfair(20, weight: .semibold)

    // Body / UI — Nunito
    static let appTitle         = nunito(18, weight: .semibold)
    static let appTitleMedium   = nunito(16, weight: .semibold)
    static let appBody          = nunito(15, weight: .regular)
    static let appBodyMedium    = nunito(15, weight: .medium)
    static let appBodySmall     = nunito(13, weight: .regular)
    static let appLabel         = nunito(11, weight: .semibold)
    static let appLabelMedium   = nunito(12, weight: .semibold)
    static let appCaption       = nunito(11, weight: .regular)
}
