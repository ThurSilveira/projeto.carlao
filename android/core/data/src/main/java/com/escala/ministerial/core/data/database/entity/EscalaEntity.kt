package com.escala.ministerial.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "escalas")
data class EscalaEntity(
    @PrimaryKey val id: Long,
    val eventoId: Long,
    val eventoNome: String,
    val eventoData: LocalDate,
    val eventoHorario: String,
    val dataAtribuicao: LocalDate,
    val observacao: String?,
    val status: String,
    val totalMinistros: Int,
)
