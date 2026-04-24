package com.escala.ministerial.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "eventos")
data class EventoEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val data: LocalDate,
    val horario: String,
    val tipoEvento: String,
    val maxMinistros: Int,
    val local: String?,
    val cancelado: Boolean,
)
