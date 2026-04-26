package com.escala.ministerial.feature.escalas.presentation

import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.model.StatusEscala

sealed interface EscalasUiState {
    data object Loading : EscalasUiState
    data class Success(
        val escalas: List<Escala>,
        val filtered: List<Escala> = escalas,
        val statusFilter: StatusEscala? = null,
    ) : EscalasUiState
    data class Error(val message: String) : EscalasUiState
}

sealed interface EscalaEvent {
    data class ShowMessage(val message: String) : EscalaEvent
    data object Created : EscalaEvent
    data object Approved : EscalaEvent
    data object Confirmed : EscalaEvent
    data object Cancelled : EscalaEvent
    data object Generated : EscalaEvent
    data object Deleted : EscalaEvent
}
