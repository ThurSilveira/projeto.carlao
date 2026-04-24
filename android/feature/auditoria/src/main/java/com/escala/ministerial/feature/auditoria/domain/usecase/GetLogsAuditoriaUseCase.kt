package com.escala.ministerial.feature.auditoria.domain.usecase

import com.escala.ministerial.feature.auditoria.domain.model.LogAuditoria
import com.escala.ministerial.feature.auditoria.domain.repository.AuditoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLogsAuditoriaUseCase @Inject constructor(private val repository: AuditoriaRepository) {
    operator fun invoke(): Flow<List<LogAuditoria>> = repository.observeAll()
}
