package com.escala.ministerial.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "logs_auditoria")
data class LogAuditoriaEntity(
    @PrimaryKey val id: Long,
    val entidade: String,
    val acao: String,
    val statusAnterior: String?,
    val statusNovo: String?,
    val realizadoPorId: String?,
    val dataHora: LocalDateTime,
)
