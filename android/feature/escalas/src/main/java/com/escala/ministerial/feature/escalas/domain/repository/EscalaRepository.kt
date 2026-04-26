package com.escala.ministerial.feature.escalas.domain.repository

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.escalas.domain.model.Escala
import kotlinx.coroutines.flow.Flow

interface EscalaRepository {
    fun observeAll(): Flow<List<Escala>>
    suspend fun refresh(): ApiResult<Unit>
    suspend fun create(eventoId: Long, observacao: String?): ApiResult<Escala>
    suspend fun gerar(eventoId: Long): ApiResult<Escala>
    suspend fun aprovar(id: Long): ApiResult<Escala>
    suspend fun confirmar(id: Long): ApiResult<Escala>
    suspend fun cancelar(id: Long): ApiResult<Escala>
    suspend fun delete(id: Long): ApiResult<Unit>
}
