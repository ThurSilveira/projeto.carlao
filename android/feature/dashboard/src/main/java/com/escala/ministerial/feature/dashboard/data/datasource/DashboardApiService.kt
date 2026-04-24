package com.escala.ministerial.feature.dashboard.data.datasource

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface DashboardApiService {
    @GET("ministros")
    suspend fun getMinistros(): List<MinistroSummaryDto>

    @GET("eventos")
    suspend fun getEventos(): List<EventoSummaryDto>

    @GET("escalas")
    suspend fun getEscalas(): List<EscalaSummaryDto>

    @GET("feedbacks")
    suspend fun getFeedbacks(): List<FeedbackSummaryDto>
}

@Serializable
data class MinistroSummaryDto(val id: Long? = null, val ativo: Boolean = true)

@Serializable
data class EventoSummaryDto(val id: Long? = null, val cancelado: Boolean = false, val data: String = "")

@Serializable
data class EscalaSummaryDto(val id: Long? = null, val status: String = "PROPOSTA")

@Serializable
data class FeedbackSummaryDto(val id: Long? = null, val nota: Int = 5, val status: String = "PENDENTE")
