package com.escala.ministerial.feature.escalas.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EscalaDto(
    val id: Long? = null,
    val eventoId: Long = 0,
    val eventoNome: String? = null,
    val eventoData: String? = null,
    val eventoHorario: String? = null,
    val dataAtribuicao: String? = null,
    val observacao: String? = null,
    val status: String = "PROPOSTA",
    val ministros: List<EscalaMinistroDto> = emptyList(),
)

@Serializable
data class EscalaMinistroDto(
    val id: Long? = null,
    val ministroId: Long = 0,
    val ministroNome: String? = null,
    val confirmacaoMinistro: Boolean = false,
    val dataConfirmacao: String? = null,
    val substituido: Boolean = false,
)
