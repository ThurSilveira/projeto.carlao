package com.escala.ministerial.feature.ministros.presentation

import com.escala.ministerial.feature.ministros.domain.model.Ministro

sealed interface MinistrosUiState {
    data object Loading : MinistrosUiState
    data class Success(
        val ministros: List<Ministro>,
        val filteredMinistros: List<Ministro> = ministros,
        val query: String = "",
        val soAtivos: Boolean = false,
    ) : MinistrosUiState
    data class Error(val message: String) : MinistrosUiState
}

sealed interface MinistroEvent {
    data class ShowMessage(val message: String) : MinistroEvent
    data object Saved : MinistroEvent
    data object Deleted : MinistroEvent
}
