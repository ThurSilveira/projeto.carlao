package com.escala.ministerial.feature.auditoria.domain.repository

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.auditoria.domain.model.LogAuditoria
import kotlinx.coroutines.flow.Flow

interface AuditoriaRepository {
    fun observeAll(): Flow<List<LogAuditoria>>
    suspend fun refresh(): ApiResult<Unit>
}
