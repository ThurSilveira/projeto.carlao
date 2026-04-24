package com.escala.ministerial.feature.eventos.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import javax.inject.Inject

class DeleteEventoUseCase @Inject constructor(private val repository: EventoRepository) {
    suspend operator fun invoke(id: Long): ApiResult<Unit> = repository.delete(id)
}
