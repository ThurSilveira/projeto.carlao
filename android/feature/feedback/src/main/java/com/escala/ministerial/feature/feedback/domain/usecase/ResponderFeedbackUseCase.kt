package com.escala.ministerial.feature.feedback.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.feedback.domain.model.Feedback
import com.escala.ministerial.feature.feedback.domain.repository.FeedbackRepository
import javax.inject.Inject

class ResponderFeedbackUseCase @Inject constructor(private val repository: FeedbackRepository) {
    suspend operator fun invoke(id: Long, resposta: String): ApiResult<Feedback> =
        repository.responder(id, resposta)
}
