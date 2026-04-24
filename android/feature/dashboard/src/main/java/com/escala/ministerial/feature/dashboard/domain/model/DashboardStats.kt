package com.escala.ministerial.feature.dashboard.domain.model

data class DashboardStats(
    val totalMinistros: Int,
    val ministrosAtivos: Int,
    val totalEventos: Int,
    val eventosAtivos: Int,
    val totalEscalas: Int,
    val escalasAprovadas: Int,
    val feedbacksPendentes: Int,
    val mediaNota: Double,
)
