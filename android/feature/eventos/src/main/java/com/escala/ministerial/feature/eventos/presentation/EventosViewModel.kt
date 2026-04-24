package com.escala.ministerial.feature.eventos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.eventos.domain.model.Evento
import com.escala.ministerial.feature.eventos.domain.usecase.CancelarEventoUseCase
import com.escala.ministerial.feature.eventos.domain.usecase.DeleteEventoUseCase
import com.escala.ministerial.feature.eventos.domain.usecase.GetEventosUseCase
import com.escala.ministerial.feature.eventos.domain.usecase.RefreshEventosUseCase
import com.escala.ministerial.feature.eventos.domain.usecase.SaveEventoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventosViewModel @Inject constructor(
    private val getEventos: GetEventosUseCase,
    private val refreshEventos: RefreshEventosUseCase,
    private val saveEvento: SaveEventoUseCase,
    private val cancelarEvento: CancelarEventoUseCase,
    private val deleteEvento: DeleteEventoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventosUiState>(EventosUiState.Loading)
    val uiState: StateFlow<EventosUiState> = _uiState

    private val _events = Channel<EventoEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeEventos()
        refresh()
    }

    private fun observeEventos() {
        viewModelScope.launch {
            getEventos()
                .catch { e -> _uiState.value = EventosUiState.Error(e.message ?: "Erro") }
                .collect { eventos ->
                    val current = _uiState.value
                    val query = if (current is EventosUiState.Success) current.query else ""
                    _uiState.value = EventosUiState.Success(
                        eventos = eventos,
                        filtered = eventos.filter { q -> query.isBlank() || q.nome.contains(query, true) || q.local?.contains(query, true) == true },
                        query = query,
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            if (_uiState.value is EventosUiState.Error) _uiState.value = EventosUiState.Loading
            when (val result = refreshEventos()) {
                is ApiResult.Error -> _events.send(EventoEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun search(query: String) {
        val current = _uiState.value as? EventosUiState.Success ?: return
        _uiState.value = current.copy(
            query = query,
            filtered = current.eventos.filter { q -> query.isBlank() || q.nome.contains(query, true) || q.local?.contains(query, true) == true },
        )
    }

    fun save(evento: Evento) {
        viewModelScope.launch {
            when (val result = saveEvento(evento)) {
                is ApiResult.Success -> _events.send(EventoEvent.Saved)
                is ApiResult.Error -> _events.send(EventoEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun cancelar(id: Long) {
        viewModelScope.launch {
            when (val result = cancelarEvento(id)) {
                is ApiResult.Success -> _events.send(EventoEvent.Cancelled)
                is ApiResult.Error -> _events.send(EventoEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            when (val result = deleteEvento(id)) {
                is ApiResult.Success -> _events.send(EventoEvent.Deleted)
                is ApiResult.Error -> _events.send(EventoEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }
}
