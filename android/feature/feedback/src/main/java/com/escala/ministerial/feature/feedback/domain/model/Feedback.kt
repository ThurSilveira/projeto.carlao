package com.escala.ministerial.feature.feedback.domain.model

import java.time.LocalDateTime

data class Feedback(
    val id: Long,
    val ministroId: Long,
    val ministroNome: String,
    val eventoId: Long,
    val eventoNome: String,
    val nota: Int,
    val comentario: String?,
    val dataEnvio: LocalDateTime?,
    val status: StatusFeedback,
    val resposta: String?,
)

enum class StatusFeedback(val label: String) {
    PENDENTE("Pendente"),
    RESPONDIDO("Respondido"),
    ARQUIVADO("Arquivado"),
}
