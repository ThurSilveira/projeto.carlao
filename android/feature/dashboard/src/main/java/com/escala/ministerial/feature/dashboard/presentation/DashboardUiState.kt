package com.escala.ministerial.feature.dashboard.presentation

import com.escala.ministerial.feature.dashboard.domain.model.DashboardStats

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val stats: DashboardStats) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
