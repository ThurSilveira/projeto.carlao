package com.escala.ministerial.feature.eventos.data.repository

import com.escala.ministerial.core.data.database.dao.EventoDao
import com.escala.ministerial.core.data.database.entity.EventoEntity
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.core.network.model.safeApiCall
import com.escala.ministerial.feature.eventos.data.datasource.EventoApiService
import com.escala.ministerial.feature.eventos.data.dto.EventoDto
import com.escala.ministerial.feature.eventos.domain.model.Evento
import com.escala.ministerial.feature.eventos.domain.model.TipoEvento
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class EventoRepositoryImpl @Inject constructor(
    private val api: EventoApiService,
    private val dao: EventoDao,
) : EventoRepository {

    override fun observeAll(): Flow<List<Evento>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh(): ApiResult<Unit> =
        safeApiCall {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.upsertAll(dtos.map { it.toEntity() })
        }

    override suspend fun create(evento: Evento): ApiResult<Evento> =
        safeApiCall {
            val result = api.create(evento.toDto())
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun update(id: Long, evento: Evento): ApiResult<Evento> =
        safeApiCall {
            val result = api.update(id, evento.toDto())
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun cancelar(id: Long): ApiResult<Evento> =
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

private fun EventoDto.toDomain(): Evento = Evento(
    id = id ?: 0L,
    nome = nome,
    data = runCatching { LocalDate.parse(data) }.getOrDefault(LocalDate.now()),
    horario = horario,
    tipoEvento = runCatching { TipoEvento.valueOf(tipoEvento) }.getOrDefault(TipoEvento.OUTRO),
    maxMinistros = maxMinistros,
    local = local,
    cancelado = cancelado,
)

private fun EventoDto.toEntity(): EventoEntity = EventoEntity(
    id = id ?: 0L,
    nome = nome,
    data = runCatching { LocalDate.parse(data) }.getOrDefault(LocalDate.now()),
    horario = horario,
    tipoEvento = tipoEvento,
    maxMinistros = maxMinistros,
    local = local,
    cancelado = cancelado,
)

private fun EventoEntity.toDomain(): Evento = Evento(
    id = id,
    nome = nome,
    data = data,
    horario = horario,
    tipoEvento = runCatching { TipoEvento.valueOf(tipoEvento) }.getOrDefault(TipoEvento.OUTRO),
    maxMinistros = maxMinistros,
    local = local,
    cancelado = cancelado,
)

private fun Evento.toDto(): EventoDto = EventoDto(
    id = if (id == 0L) null else id,
    nome = nome,
    data = data.toString(),
    horario = horario,
    tipoEvento = tipoEvento.name,
    maxMinistros = maxMinistros,
    local = local,
    cancelado = cancelado,
)
