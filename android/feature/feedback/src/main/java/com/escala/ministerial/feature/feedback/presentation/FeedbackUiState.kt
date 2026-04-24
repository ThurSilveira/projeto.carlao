package com.escala.ministerial.feature.feedback.presentation

import com.escala.ministerial.feature.feedback.domain.model.Feedback

sealed interface FeedbackUiState {
    data object Loading : FeedbackUiState
    data class Success(
        val feedbacks: List<Feedback>,
        val mediaNota: Double = if (feedbacks.isEmpty()) 0.0 else feedbacks.map { it.nota }.average(),
        val pendentes: Int = feedbacks.count { it.status.name == "PENDENTE" },
    ) : FeedbackUiState
    data class Error(val message: String) : FeedbackUiState
}

sealed interface FeedbackEvent {
    data class ShowMessage(val message: String) : FeedbackEvent
    data object Responded : FeedbackEvent
}
