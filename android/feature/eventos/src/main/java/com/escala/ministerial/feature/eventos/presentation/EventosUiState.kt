package com.escala.ministerial.feature.eventos.presentation

import com.escala.ministerial.feature.eventos.domain.model.Evento

sealed interface EventosUiState {
    data object Loading : EventosUiState
    data class Success(
        val eventos: List<Evento>,
        val filtered: List<Evento> = eventos,
        val query: String = "",
    ) : EventosUiState
    data class Error(val message: String) : EventosUiState
}

sealed interface EventoEvent {
    data class ShowMessage(val message: String) : EventoEvent
    data object Saved : EventoEvent
    data object Cancelled : EventoEvent
    data object Deleted : EventoEvent
}
