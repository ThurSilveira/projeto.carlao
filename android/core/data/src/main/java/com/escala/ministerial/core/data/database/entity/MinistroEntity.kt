package com.escala.ministerial.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "ministros")
data class MinistroEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val email: String,
    val telefone: String?,
    val dataNascimento: LocalDate?,
    val observacoes: String?,
    val ativo: Boolean,
    val visitasAoInfermo: Boolean,
    val statusCurso: Boolean,
    val escalasMes: Int,
    val funcao: String,
    val funcaoEspecificada: String? = null,
)
