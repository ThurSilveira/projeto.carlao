package com.escala.ministerial.feature.eventos.domain.usecase

import com.escala.ministerial.feature.eventos.domain.model.Evento
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventosUseCase @Inject constructor(
    private val repository: EventoRepository,
) {
    operator fun invoke(): Flow<List<Evento>> = repository.observeAll()
}
