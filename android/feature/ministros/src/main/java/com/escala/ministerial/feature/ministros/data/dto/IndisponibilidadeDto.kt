package com.escala.ministerial.feature.ministros.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class IndisponibilidadeDto(
    val id: Long? = null,
    val ministroId: Long? = null,
    val data: String = "",
    val horarioInicio: String? = null,
    val horarioFim: String? = null,
    val motivo: String? = null,
)
