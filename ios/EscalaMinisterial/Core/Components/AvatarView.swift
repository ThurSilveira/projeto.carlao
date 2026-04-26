import SwiftUI

// MARK: - AvatarView
// Displays a circle with the first 2 initials of the given name.
struct AvatarView: View {
    let nome: String
    var size: CGFloat = 40
    var color: Color = .appPrimary

    private var initials: String {
        let words = nome.split(separator: " ").prefix(2)
        return words.compactMap { $0.first.map { String($0).uppercased() } }.joined()
    }

    var body: some View {
        ZStack {
            Circle()
                .fill(color.opacity(0.13))
                .overlay(Circle().stroke(color.opacity(0.20), lineWidth: 1.5))
                .frame(width: size, height: size)
            Text(initials)
                .font(.system(size: size * 0.38, weight: .semibold))
                .foregroundStyle(color)
        }
    }
}

#Preview {
    HStack(spacing: 12) {
        AvatarView(nome: "João Silva", size: 44, color: .appPrimary)
        AvatarView(nome: "Maria", size: 36, color: .appSecondary)
        AvatarView(nome: "Carlos Alberto", size: 28, color: .statusNeutral)
    }
    .padding()
}
