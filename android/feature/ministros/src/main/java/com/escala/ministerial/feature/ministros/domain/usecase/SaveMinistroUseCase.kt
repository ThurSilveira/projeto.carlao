package com.escala.ministerial.feature.ministros.domain.usecase

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import javax.inject.Inject

class SaveMinistroUseCase @Inject constructor(
    private val repository: MinistroRepository,
) {
    suspend operator fun invoke(ministro: Ministro): ApiResult<Ministro> =
        if (ministro.id == 0L) repository.create(ministro)
        else repository.update(ministro.id, ministro)
}
