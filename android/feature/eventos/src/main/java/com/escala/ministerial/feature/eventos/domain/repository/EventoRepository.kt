package com.escala.ministerial.feature.eventos.domain.repository

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.eventos.domain.model.Evento
import kotlinx.coroutines.flow.Flow

interface EventoRepository {
    fun observeAll(): Flow<List<Evento>>
    suspend fun refresh(): ApiResult<Unit>
    suspend fun create(evento: Evento): ApiResult<Evento>
    suspend fun update(id: Long, evento: Evento): ApiResult<Evento>
    suspend fun cancelar(id: Long): ApiResult<Evento>
    suspend fun delete(id: Long): ApiResult<Unit>
}
