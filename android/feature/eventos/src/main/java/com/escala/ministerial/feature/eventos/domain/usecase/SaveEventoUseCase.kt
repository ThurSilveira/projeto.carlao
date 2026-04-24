package com.escala.ministerial.feature.eventos.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.eventos.domain.model.Evento
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import javax.inject.Inject

class SaveEventoUseCase @Inject constructor(private val repository: EventoRepository) {
    suspend operator fun invoke(evento: Evento): ApiResult<Evento> =
        if (evento.id == 0L) repository.create(evento)
        else repository.update(evento.id, evento)
}
