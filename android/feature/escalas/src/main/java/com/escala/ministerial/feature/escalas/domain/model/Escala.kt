package com.escala.ministerial.feature.escalas.domain.model

import java.time.LocalDate

data class Escala(
    val id: Long,
    val eventoId: Long,
    val eventoNome: String,
    val eventoData: LocalDate?,
    val eventoHorario: String?,
    val dataAtribuicao: LocalDate?,
    val observacao: String?,
    val status: StatusEscala,
    val ministros: List<EscalaMinistro>,
    val totalMinistros: Int = ministros.size,
)

data class EscalaMinistro(
    val id: Long,
    val ministroId: Long,
    val ministroNome: String,
    val confirmacaoMinistro: Boolean,
    val substituido: Boolean,
)

enum class StatusEscala(val label: String) {
    PROPOSTA("Proposta"),
    APROVADA("Aprovada"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
}
