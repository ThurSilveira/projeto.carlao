import SwiftUI

struct ContentView: View {
    @State private var selectedTab: Tab = .dashboard
    @AppStorage("render_cold_start_notice_seen") private var noticeSeen = false
    @State private var showNotice = false

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
        .onAppear { if !noticeSeen { showNotice = true } }
        .alert("⏳ Aviso sobre o servidor", isPresented: $showNotice) {
            Button("Entendido") { noticeSeen = true }
        } message: {
            Text(
                "O servidor está hospedado no plano gratuito do Render, que hiberna após " +
                "15 minutos de inatividade. Na primeira requisição após o sono, pode levar " +
                "até 50 segundos para responder — depois fica rápido."
            )
        }
    }
}

#Preview {
    ContentView()
}
