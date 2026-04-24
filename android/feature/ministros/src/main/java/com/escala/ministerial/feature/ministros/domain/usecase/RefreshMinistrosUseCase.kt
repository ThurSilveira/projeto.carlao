package com.escala.ministerial.feature.ministros.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import javax.inject.Inject

class RefreshMinistrosUseCase @Inject constructor(
    private val repository: MinistroRepository,
) {
    suspend operator fun invoke(): ApiResult<Unit> = repository.refresh()
}
