package com.escala.ministerial.feature.escalas.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import javax.inject.Inject

class AprovarEscalaUseCase @Inject constructor(private val repository: EscalaRepository) {
    suspend operator fun invoke(id: Long): ApiResult<Escala> = repository.aprovar(id)
}
