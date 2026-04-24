package com.escala.ministerial.feature.auditoria.data.datasource

import com.escala.ministerial.feature.auditoria.data.dto.LogAuditoriaDto
import retrofit2.http.GET

interface AuditoriaApiService {
    @GET("auditoria")
    suspend fun getAll(): List<LogAuditoriaDto>
}
