package com.escala.ministerial.feature.auditoria.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LogAuditoriaDto(
    val id: Long = 0,
    val entidade: String = "",
    val acao: String = "",
    val statusAnterior: String? = null,
    val statusNovo: String? = null,
    val realizadoPorId: String? = null,
    val dataHora: String = "",
)
