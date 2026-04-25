package com.escala.ministerial.feature.eventos.domain.model

import java.time.LocalDate

data class Evento(
    val id: Long,
    val nome: String,
    val data: LocalDate,
    val horario: String,
    val tipoEvento: TipoEvento,
    val tipoEspecificado: String? = null,
    val maxMinistros: Int,
    val local: String?,
    val cancelado: Boolean,
)

enum class TipoEvento(val label: String) {
    MISSA_PAROQUIAL("Missa Paroquial"),
    MISSA_ESPECIAL("Missa Especial"),
    RETIRO("Retiro"),
    BATIZADO("Batizado"),
    CASAMENTO("Casamento"),
    ADORACAO("Adoração"),
    OUTRO("Outro"),
}
