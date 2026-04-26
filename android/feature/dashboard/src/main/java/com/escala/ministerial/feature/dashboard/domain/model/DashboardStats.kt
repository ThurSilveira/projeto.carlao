package com.escala.ministerial.feature.dashboard.domain.model

import java.time.LocalDate

data class DashboardStats(
    val totalMinistros: Int,
    val ministrosAtivos: Int,
    val totalEventos: Int,
    val eventosAtivos: Int,
    val totalEscalas: Int,
    val escalasAprovadas: Int,
    val feedbacksPendentes: Int,
    val mediaNota: Double,
    val logsAuditoria: Int,
    val proximosEventos: List<ProximoEvento>,
    val escalasRecentes: List<EscalaRecente>,
)

data class ProximoEvento(
    val id: Long,
    val nome: String,
    val data: LocalDate,
    val horario: String,
    val tipo: String,
)

data class EscalaRecente(
    val id: Long,
    val eventoNome: String,
    val eventoData: LocalDate,
    val status: String,
    val totalMinistros: Int,
)
