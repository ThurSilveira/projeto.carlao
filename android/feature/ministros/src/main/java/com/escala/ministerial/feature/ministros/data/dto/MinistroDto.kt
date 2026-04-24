package com.escala.ministerial.feature.ministros.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MinistroDto(
    val id: Long? = null,
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null,
    val dataNascimento: String? = null,
    val observacoes: String? = null,
    val ativo: Boolean = true,
    val visitasAoInfermo: Boolean = false,
    val statusCurso: Boolean = false,
    val escalasMes: Int = 0,
    val funcao: String = "LEITURA",
)
