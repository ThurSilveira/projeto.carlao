package com.escala.ministerial.feature.eventos.data.datasource

import com.escala.ministerial.feature.eventos.data.dto.EventoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EventoApiService {
    @GET("eventos")
    suspend fun getAll(): List<EventoDto>

    @GET("eventos/{id}")
    suspend fun getById(@Path("id") id: Long): EventoDto

    @POST("eventos")
    suspend fun create(@Body dto: EventoDto): EventoDto

    @PUT("eventos/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: EventoDto): EventoDto

    @PUT("eventos/{id}/cancelar")
    suspend fun cancelar(@Path("id") id: Long): EventoDto

    @DELETE("eventos/{id}")
    suspend fun delete(@Path("id") id: Long)
}
