package com.escala.ministerial.feature.ministros.domain.repository

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.ministros.domain.model.Indisponibilidade
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import kotlinx.coroutines.flow.Flow

interface MinistroRepository {
    fun observeAll(): Flow<List<Ministro>>
    suspend fun refresh(): ApiResult<Unit>
    suspend fun getById(id: Long): ApiResult<Ministro>
    suspend fun create(ministro: Ministro): ApiResult<Ministro>
    suspend fun update(id: Long, ministro: Ministro): ApiResult<Ministro>
    suspend fun delete(id: Long): ApiResult<Unit>

    suspend fun getIndisponibilidades(ministroId: Long): ApiResult<List<Indisponibilidade>>
    suspend fun createIndisponibilidade(ministroId: Long, indisp: Indisponibilidade): ApiResult<Indisponibilidade>
    suspend fun updateIndisponibilidade(ministroId: Long, indisp: Indisponibilidade): ApiResult<Indisponibilidade>
    suspend fun deleteIndisponibilidade(ministroId: Long, id: Long): ApiResult<Unit>
}
