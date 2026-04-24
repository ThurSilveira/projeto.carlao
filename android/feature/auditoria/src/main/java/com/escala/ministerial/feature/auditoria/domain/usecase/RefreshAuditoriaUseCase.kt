package com.escala.ministerial.feature.auditoria.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.auditoria.domain.repository.AuditoriaRepository
import javax.inject.Inject

class RefreshAuditoriaUseCase @Inject constructor(private val repository: AuditoriaRepository) {
    suspend operator fun invoke(): ApiResult<Unit> = repository.refresh()
}
