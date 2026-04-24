package com.escala.ministerial.feature.escalas.data.datasource

import com.escala.ministerial.feature.escalas.data.dto.EscalaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EscalaApiService {
    @GET("escalas")
    suspend fun getAll(): List<EscalaDto>

    @GET("escalas/{id}")
    suspend fun getById(@Path("id") id: Long): EscalaDto

    @POST("escalas")
    suspend fun create(@Body dto: EscalaDto): EscalaDto

    @POST("escalas/gerar/{eventoId}")
    suspend fun gerar(@Path("eventoId") eventoId: Long): EscalaDto

    @PUT("escalas/{id}/aprovar")
    suspend fun aprovar(@Path("id") id: Long): EscalaDto

    @PUT("escalas/{id}/cancelar")
    suspend fun cancelar(@Path("id") id: Long): EscalaDto

    @DELETE("escalas/{id}")
    suspend fun delete(@Path("id") id: Long)
}
