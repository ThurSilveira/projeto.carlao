import SwiftUI

struct FeedbackView: View {
    @StateObject private var viewModel = FeedbackViewModel()
    @State private var respondingFeedback: Feedback? = nil
    @State private var respostaText: String = ""

    var body: some View {
        NavigationStack {
            Group {
                switch viewModel.state {
                case .loading:
                    LoadingView()
                case .error(let msg):
                    ErrorView(message: msg) { Task { await viewModel.load() } }
                case .success:
                    VStack(spacing: 0) {
                        HStack(spacing: 0) {
                            Spacer()
                            VStack(spacing: 2) {
                                Text("\(viewModel.feedbacks.count)")
                                    .font(.system(size: 24, weight: .bold))
                                Text("Total")
                                    .font(.appCaption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            VStack(spacing: 2) {
                                Text(viewModel.mediaNota > 0 ? String(format: "%.1f", viewModel.mediaNota) : "—")
                                    .font(.system(size: 24, weight: .bold))
                                    .foregroundColor(Color(red: 1.0, green: 0.596, blue: 0.0))
                                Text("Nota média")
                                    .font(.appCaption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            VStack(spacing: 2) {
                                Text("\(viewModel.pendentes)")
                                    .font(.system(size: 24, weight: .bold))
                                    .foregroundColor(.statusWarning)
                                Text("Pendentes")
                                    .font(.appCaption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                        }
                        .padding(.vertical, 12)
                        .background(Color(.secondarySystemBackground))

                        Divider()

                        if viewModel.feedbacks.isEmpty {
                            Spacer()
                            Text("Nenhum feedback encontrado")
                                .foregroundColor(.secondary)
                            Spacer()
                        } else {
                            List {
                                ForEach(viewModel.feedbacks) { feedback in
                                    FeedbackCard(feedback: feedback) {
                                        respondingFeedback = feedback
                                        respostaText = ""
                                    }
                                    .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
                                    .listRowBackground(Color.clear)
                                    .listRowSeparator(.hidden)
                                }
                            }
                            .listStyle(.plain)
                        }
                    }
                }
            }
            .navigationTitle("Feedbacks")
            .sheet(item: $respondingFeedback) { feedback in
                ResponderFeedbackSheet(
                    feedback: feedback,
                    respostaText: $respostaText
                ) { resposta in
                    if let id = feedback.id {
                        Task { await viewModel.responder(id: id, resposta: resposta) }
                    }
                    respondingFeedback = nil
                }
            }
            .overlay(alignment: .bottom) {
                if let toast = viewModel.toast {
                    ToastView(message: toast)
                        .padding(.bottom, 24)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                                viewModel.toast = nil
                            }
                        }
                }
            }
            .animation(.easeInOut, value: viewModel.toast)
        }
    }
}

private struct FeedbackCard: View {
    let feedback: Feedback
    let onResponder: () -> Void

    private var badgeVariant: BadgeVariant {
        switch feedback.statusEnum {
        case .pendente:   return .warning
        case .respondido: return .success
        case .arquivado:  return .neutral
        case .none:       return .neutral
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(feedback.ministroNome ?? "Ministro")
                        .font(.appHeadline)
                    Text(feedback.eventoNome ?? "Evento")
                        .font(.appCaption)
                        .foregroundColor(.secondary)
                    if let dataFmt = feedback.dataEnvioFormatada {
                        Text(dataFmt)
                            .font(.appCaption)
                            .foregroundColor(.secondary)
                    }
                }
                Spacer()
                VStack(alignment: .trailing, spacing: 4) {
                    HStack(spacing: 2) {
                        Image(systemName: "star.fill")
                            .foregroundColor(Color(red: 1.0, green: 0.596, blue: 0.0))
                            .font(.appCaption)
                        Text("\(feedback.nota)/10")
                            .font(.appHeadline)
                    }
                    if feedback.status == Feedback.StatusFeedback.pendente.rawValue {
                        Button(action: onResponder) {
                            Image(systemName: "arrowshape.turn.up.left.fill")
                                .foregroundColor(.appPrimary)
                        }
                        .buttonStyle(.plain)
                    }
                }
            }

            if let comentario = feedback.comentario, !comentario.isEmpty {
                Text("\"\(comentario)\"")
                    .font(.appBody)
                    .italic()
            }

            StatusBadge(
                text: feedback.statusEnum?.label ?? feedback.status,
                variant: badgeVariant
            )

            if let resposta = feedback.resposta, !resposta.isEmpty {
                Text("Resposta: \(resposta)")
                    .font(.appCaption)
                    .foregroundColor(.statusSuccess)
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
    }
}

private struct ResponderFeedbackSheet: View {
    let feedback: Feedback
    @Binding var respostaText: String
    let onEnviar: (String) -> Void

    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            Form {
                Section("Feedback de \(feedback.ministroNome ?? "")") {
                    Text("Evento: \(feedback.eventoNome ?? "")")
                        .font(.appCaption)
                        .foregroundColor(.secondary)
                    if let comentario = feedback.comentario, !comentario.isEmpty {
                        Text("\"\(comentario)\"")
                            .font(.appBody)
                            .italic()
                    }
                }
                Section("Sua resposta") {
                    TextField("Digite sua resposta...", text: $respostaText, axis: .vertical)
                        .lineLimit(4...8)
                }
            }
            .navigationTitle("Responder Feedback")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Enviar") {
                        if !respostaText.trimmingCharacters(in: .whitespaces).isEmpty {
                            onEnviar(respostaText.trimmingCharacters(in: .whitespaces))
                        }
                        dismiss()
                    }
                    .disabled(respostaText.trimmingCharacters(in: .whitespaces).isEmpty)
                }
            }
        }
    }
}

#Preview {
    FeedbackView()
}
