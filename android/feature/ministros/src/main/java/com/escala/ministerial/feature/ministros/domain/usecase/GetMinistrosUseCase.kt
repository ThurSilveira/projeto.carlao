package com.escala.ministerial.feature.ministros.domain.usecase

import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMinistrosUseCase @Inject constructor(
    private val repository: MinistroRepository,
) {
    operator fun invoke(): Flow<List<Ministro>> = repository.observeAll()
}
