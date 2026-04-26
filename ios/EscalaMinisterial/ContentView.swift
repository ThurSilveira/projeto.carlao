import SwiftUI

struct ContentView: View {
    @State private var selectedTab: Tab = .dashboard

    enum Tab {
        case dashboard, ministros, eventos, escalas, feedback, auditoria
    }

    var body: some View {
        TabView(selection: $selectedTab) {
            DashboardView()
                .tabItem { Label("Dashboard", systemImage: "chart.bar.fill") }
                .tag(Tab.dashboard)

            MinistrosView()
                .tabItem { Label("Ministros", systemImage: "person.3.fill") }
                .tag(Tab.ministros)

            EventosView()
                .tabItem { Label("Eventos", systemImage: "calendar") }
                .tag(Tab.eventos)

            EscalasView()
                .tabItem { Label("Escalas", systemImage: "clock.fill") }
                .tag(Tab.escalas)

            FeedbackView()
                .tabItem { Label("Feedback", systemImage: "bubble.left.fill") }
                .tag(Tab.feedback)

            AuditoriaView()
                .tabItem { Label("Auditoria", systemImage: "doc.text.magnifyingglass") }
                .tag(Tab.auditoria)
        }
        .tint(.appPrimary)
    }
}

#Preview {
    ContentView()
}
