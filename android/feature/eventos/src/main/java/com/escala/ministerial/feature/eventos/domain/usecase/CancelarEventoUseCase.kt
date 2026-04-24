package com.escala.ministerial.feature.eventos.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.eventos.domain.model.Evento
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import javax.inject.Inject

class CancelarEventoUseCase @Inject constructor(private val repository: EventoRepository) {
    suspend operator fun invoke(id: Long): ApiResult<Evento> = repository.cancelar(id)
}
