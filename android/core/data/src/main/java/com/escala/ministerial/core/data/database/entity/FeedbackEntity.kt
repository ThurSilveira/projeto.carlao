package com.escala.ministerial.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "feedbacks")
data class FeedbackEntity(
    @PrimaryKey val id: Long,
    val ministroId: Long,
    val ministroNome: String,
    val eventoId: Long,
    val eventoNome: String,
    val nota: Int,
    val comentario: String?,
    val dataEnvio: LocalDateTime,
    val status: String,
    val resposta: String?,
)
