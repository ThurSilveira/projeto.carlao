package com.escala.ministerial.feature.feedback.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackDto(
    val id: Long? = null,
    val ministroId: Long = 0,
    val ministroNome: String? = null,
    val eventoId: Long = 0,
    val eventoNome: String? = null,
    val nota: Int = 5,
    val comentario: String? = null,
    val dataEnvio: String? = null,
    val status: String = "PENDENTE",
    val resposta: String? = null,
)

@Serializable
data class ResponderFeedbackRequest(val resposta: String)
