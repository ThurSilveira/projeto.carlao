package com.escala.ministerial.feature.ministros.domain.model

import java.time.LocalDate

data class Indisponibilidade(
    val id: Long,
    val ministroId: Long,
    val data: LocalDate,
    val horarioInicio: String?,
    val horarioFim: String?,
    val motivo: String?,
)
