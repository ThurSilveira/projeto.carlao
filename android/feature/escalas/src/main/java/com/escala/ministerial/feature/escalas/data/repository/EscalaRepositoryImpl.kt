package com.escala.ministerial.feature.escalas.data.repository

import com.escala.ministerial.core.data.database.dao.EscalaDao
import com.escala.ministerial.core.data.database.entity.EscalaEntity
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.core.network.model.safeApiCall
import com.escala.ministerial.feature.escalas.data.datasource.EscalaApiService
import com.escala.ministerial.feature.escalas.data.dto.EscalaDto
import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.model.EscalaMinistro
import com.escala.ministerial.feature.escalas.domain.model.StatusEscala
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class EscalaRepositoryImpl @Inject constructor(
    private val api: EscalaApiService,
    private val dao: EscalaDao,
) : EscalaRepository {

    override fun observeAll(): Flow<List<Escala>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh(): ApiResult<Unit> =
        safeApiCall {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.upsertAll(dtos.map { it.toEntity() })
        }

    override suspend fun create(eventoId: Long, observacao: String?): ApiResult<Escala> =
        safeApiCall {
            val dto = EscalaDto(eventoId = eventoId, observacao = observacao)
            val result = api.create(dto)
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun gerar(eventoId: Long): ApiResult<Escala> =
        safeApiCall {
            val result = api.gerar(eventoId)
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun aprovar(id: Long): ApiResult<Escala> =
        safeApiCall {
            val result = api.aprovar(id)
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun cancelar(id: Long): ApiResult<Escala> =
        safeApiCall {
            val result = api.cancelar(id)
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun delete(id: Long): ApiResult<Unit> =
        safeApiCall {
            api.delete(id)
            dao.deleteById(id)
        }
}

private fun EscalaDto.toDomain(): Escala = Escala(
    id = id ?: 0L,
    eventoId = eventoId,
    eventoNome = eventoNome ?: "—",
    eventoData = eventoData?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    eventoHorario = eventoHorario,
    dataAtribuicao = dataAtribuicao?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    observacao = observacao,
    status = runCatching { StatusEscala.valueOf(status) }.getOrDefault(StatusEscala.PROPOSTA),
    ministros = ministros.map { m ->
        EscalaMinistro(
            id = m.id ?: 0L,
            ministroId = m.ministroId,
            ministroNome = m.ministroNome ?: "—",
            confirmacaoMinistro = m.confirmacaoMinistro,
            substituido = m.substituido,
        )
    },
)

private fun EscalaDto.toEntity(): EscalaEntity = EscalaEntity(
    id = id ?: 0L,
    eventoId = eventoId,
    eventoNome = eventoNome ?: "—",
    eventoData = eventoData?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now(),
    eventoHorario = eventoHorario ?: "",
    dataAtribuicao = dataAtribuicao?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now(),
    observacao = observacao,
    status = status,
    totalMinistros = ministros.size,
)

private fun EscalaEntity.toDomain(): Escala = Escala(
    id = id,
    eventoId = eventoId,
    eventoNome = eventoNome,
    eventoData = eventoData,
    eventoHorario = eventoHorario,
    dataAtribuicao = dataAtribuicao,
    observacao = observacao,
    status = runCatching { StatusEscala.valueOf(status) }.getOrDefault(StatusEscala.PROPOSTA),
    ministros = emptyList(),
)
