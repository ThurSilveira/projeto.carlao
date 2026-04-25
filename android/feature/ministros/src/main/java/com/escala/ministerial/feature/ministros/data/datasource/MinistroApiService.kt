package com.escala.ministerial.feature.ministros.data.datasource

import com.escala.ministerial.feature.ministros.data.dto.IndisponibilidadeDto
import com.escala.ministerial.feature.ministros.data.dto.MinistroDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MinistroApiService {
    @GET("ministros")
    suspend fun getAll(): List<MinistroDto>

    @GET("ministros/{id}")
    suspend fun getById(@Path("id") id: Long): MinistroDto

    @POST("ministros")
    suspend fun create(@Body dto: MinistroDto): MinistroDto

    @PUT("ministros/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: MinistroDto): MinistroDto

    @DELETE("ministros/{id}")
    suspend fun delete(@Path("id") id: Long)

    @GET("ministros/{ministroId}/indisponibilidades")
    suspend fun getIndisponibilidades(@Path("ministroId") ministroId: Long): List<IndisponibilidadeDto>

    @POST("ministros/{ministroId}/indisponibilidades")
    suspend fun createIndisponibilidade(
        @Path("ministroId") ministroId: Long,
        @Body dto: IndisponibilidadeDto,
    ): IndisponibilidadeDto

    @PUT("ministros/{ministroId}/indisponibilidades/{id}")
    suspend fun updateIndisponibilidade(
        @Path("ministroId") ministroId: Long,
        @Path("id") id: Long,
        @Body dto: IndisponibilidadeDto,
    ): IndisponibilidadeDto

    @DELETE("ministros/{ministroId}/indisponibilidades/{id}")
    suspend fun deleteIndisponibilidade(
        @Path("ministroId") ministroId: Long,
        @Path("id") id: Long,
    )
}
