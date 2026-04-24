package com.escala.ministerial.feature.feedback.domain.repository

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.feedback.domain.model.Feedback
import kotlinx.coroutines.flow.Flow

interface FeedbackRepository {
    fun observeAll(): Flow<List<Feedback>>
    suspend fun refresh(): ApiResult<Unit>
    suspend fun create(feedback: Feedback): ApiResult<Feedback>
    suspend fun responder(id: Long, resposta: String): ApiResult<Feedback>
}
