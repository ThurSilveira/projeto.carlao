package com.escala.ministerial.feature.feedback.data.repository

import com.escala.ministerial.core.data.database.dao.FeedbackDao
import com.escala.ministerial.core.data.database.entity.FeedbackEntity
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.core.network.model.safeApiCall
import com.escala.ministerial.feature.feedback.data.datasource.FeedbackApiService
import com.escala.ministerial.feature.feedback.data.dto.FeedbackDto
import com.escala.ministerial.feature.feedback.data.dto.ResponderFeedbackRequest
import com.escala.ministerial.feature.feedback.domain.model.Feedback
import com.escala.ministerial.feature.feedback.domain.model.StatusFeedback
import com.escala.ministerial.feature.feedback.domain.repository.FeedbackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val api: FeedbackApiService,
    private val dao: FeedbackDao,
) : FeedbackRepository {

    override fun observeAll(): Flow<List<Feedback>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh(): ApiResult<Unit> =
        safeApiCall {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.upsertAll(dtos.map { it.toEntity() })
        }

    override suspend fun create(feedback: Feedback): ApiResult<Feedback> =
        safeApiCall {
            val dto = FeedbackDto(
                ministroId = feedback.ministroId,
                eventoId = feedback.eventoId,
                nota = feedback.nota,
                comentario = feedback.comentario,
            )
            val result = api.create(dto)
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun responder(id: Long, resposta: String): ApiResult<Feedback> =
        safeApiCall {
            val result = api.responder(id, ResponderFeedbackRequest(resposta))
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }
}

private fun FeedbackDto.toDomain(): Feedback = Feedback(
    id = id ?: 0L,
    ministroId = ministroId,
    ministroNome = ministroNome ?: "—",
    eventoId = eventoId,
    eventoNome = eventoNome ?: "—",
    nota = nota,
    comentario = comentario,
    dataEnvio = dataEnvio?.let { runCatching { LocalDateTime.parse(it) }.getOrNull() },
    status = runCatching { StatusFeedback.valueOf(status) }.getOrDefault(StatusFeedback.PENDENTE),
    resposta = resposta,
)

private fun FeedbackDto.toEntity(): FeedbackEntity = FeedbackEntity(
    id = id ?: 0L,
    ministroId = ministroId,
    ministroNome = ministroNome ?: "—",
    eventoId = eventoId,
    eventoNome = eventoNome ?: "—",
    nota = nota,
    comentario = comentario,
    dataEnvio = dataEnvio?.let { runCatching { LocalDateTime.parse(it) }.getOrNull() } ?: LocalDateTime.now(),
    status = status,
    resposta = resposta,
)

private fun FeedbackEntity.toDomain(): Feedback = Feedback(
    id = id,
    ministroId = ministroId,
    ministroNome = ministroNome,
    eventoId = eventoId,
    eventoNome = eventoNome,
    nota = nota,
    comentario = comentario,
    dataEnvio = dataEnvio,
    status = runCatching { StatusFeedback.valueOf(status) }.getOrDefault(StatusFeedback.PENDENTE),
    resposta = resposta,
)
