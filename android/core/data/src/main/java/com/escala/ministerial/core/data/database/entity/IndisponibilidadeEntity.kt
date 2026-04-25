package com.escala.ministerial.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "indisponibilidades")
data class IndisponibilidadeEntity(
    @PrimaryKey val id: Long,
    val ministroId: Long,
    val data: LocalDate,
    val horarioInicio: String?,
    val horarioFim: String?,
    val motivo: String?,
)
