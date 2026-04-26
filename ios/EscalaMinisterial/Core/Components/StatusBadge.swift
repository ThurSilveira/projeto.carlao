import SwiftUI

// MARK: - BadgeVariant
enum BadgeVariant {
    case primary, secondary, success, warning, error, info, neutral

    var foreground: Color {
        switch self {
        case .primary:   return .appPrimary
        case .secondary: return .appSecondary
        case .success:   return .statusSuccess
        case .warning:   return .statusWarning
        case .error:     return .statusError
        case .info:      return .statusInfo
        case .neutral:   return .statusNeutral
        }
    }

    var background: Color {
        switch self {
        case .primary:   return .appPrimaryContainer
        case .secondary: return .appSecondaryContainer
        case .success:   return .statusSuccessContainer
        case .warning:   return .statusWarningContainer
        case .error:     return .statusErrorContainer
        case .info:      return .statusInfoContainer
        case .neutral:   return .statusNeutralContainer
        }
    }
}

// MARK: - StatusBadge — pill + dot
struct StatusBadge: View {
    let text: String
    let variant: BadgeVariant

    var body: some View {
        HStack(spacing: 5) {
            Circle()
                .fill(variant.foreground)
                .frame(width: 6, height: 6)
            Text(text)
                .font(.appLabel)
                .foregroundStyle(variant.foreground)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 3)
        .background(variant.background, in: Capsule())
    }
}

// MARK: - Helper
func escalaBadgeVariant(_ status: String) -> BadgeVariant {
    switch status.uppercased() {
    case "APROVADA":   return .primary
    case "CONFIRMADA": return .success
    case "CANCELADA":  return .error
    case "PROPOSTA":   return .warning
    case "ATIVO":      return .success
    case "CONCLUIDO":  return .neutral
    default:           return .neutral
    }
}

// MARK: - Preview
#Preview {
    VStack(alignment: .leading, spacing: 8) {
        StatusBadge(text: "Proposta",   variant: .warning)
        StatusBadge(text: "Aprovada",   variant: .primary)
        StatusBadge(text: "Confirmada", variant: .success)
        StatusBadge(text: "Cancelada",  variant: .error)
        StatusBadge(text: "Info",       variant: .info)
        StatusBadge(text: "Neutro",     variant: .neutral)
    }
    .padding()
}
