package com.escala.ministerial.feature.escalas.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import javax.inject.Inject

class RefreshEscalasUseCase @Inject constructor(private val repository: EscalaRepository) {
    suspend operator fun invoke(): ApiResult<Unit> = repository.refresh()
}
