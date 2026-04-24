package com.escala.ministerial.feature.eventos.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import javax.inject.Inject

class RefreshEventosUseCase @Inject constructor(private val repository: EventoRepository) {
    suspend operator fun invoke(): ApiResult<Unit> = repository.refresh()
}
