import SwiftUI

enum BadgeVariant {
    case success, error, warning, info, neutral

    var foreground: Color {
        switch self {
        case .success: return .statusSuccess
        case .error:   return .statusError
        case .warning: return .statusWarning
        case .info:    return .statusInfo
        case .neutral: return .statusNeutral
        }
    }

    var background: Color {
        foreground.opacity(0.12)
    }
}

struct StatusBadge: View {
    let text: String
    let variant: BadgeVariant

    var body: some View {
        Text(text)
            .font(.appLabel)
            .foregroundColor(variant.foreground)
            .padding(.horizontal, 8)
            .padding(.vertical, 3)
            .background(variant.background, in: Capsule())
    }
}

#Preview {
    HStack {
        StatusBadge(text: "Ativo", variant: .success)
        StatusBadge(text: "Cancelado", variant: .error)
        StatusBadge(text: "Proposta", variant: .warning)
        StatusBadge(text: "Info", variant: .info)
        StatusBadge(text: "Neutro", variant: .neutral)
    }
    .padding()
}
