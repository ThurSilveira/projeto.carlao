package com.escala.ministerial.feature.feedback.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.feedback.domain.repository.FeedbackRepository
import javax.inject.Inject

class RefreshFeedbacksUseCase @Inject constructor(private val repository: FeedbackRepository) {
    suspend operator fun invoke(): ApiResult<Unit> = repository.refresh()
}
