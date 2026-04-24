package com.escala.ministerial.feature.feedback.domain.usecase

import com.escala.ministerial.feature.feedback.domain.model.Feedback
import com.escala.ministerial.feature.feedback.domain.repository.FeedbackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedbacksUseCase @Inject constructor(private val repository: FeedbackRepository) {
    operator fun invoke(): Flow<List<Feedback>> = repository.observeAll()
}
