package com.escala.ministerial.feature.eventos.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventoDto(
    val id: Long? = null,
    val nome: String = "",
    val data: String = "",
    val horario: String = "",
    val tipoEvento: String = "MISSA_PAROQUIAL",
    val tipoEspecificado: String? = null,
    val maxMinistros: Int = 6,
    val local: String? = null,
    val cancelado: Boolean = false,
)
