package com.escala.ministerial.feature.escalas.domain.usecase

import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEscalasUseCase @Inject constructor(private val repository: EscalaRepository) {
    operator fun invoke(): Flow<List<Escala>> = repository.observeAll()
}
