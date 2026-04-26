package com.escala.ministerial.feature.escalas.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.escalas.domain.model.StatusEscala
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import com.escala.ministerial.feature.escalas.domain.usecase.AprovarEscalaUseCase
import com.escala.ministerial.feature.escalas.domain.usecase.CancelarEscalaUseCase
import com.escala.ministerial.feature.escalas.domain.usecase.GerarEscalaUseCase
import com.escala.ministerial.feature.escalas.domain.usecase.GetEscalasUseCase
import com.escala.ministerial.feature.escalas.domain.usecase.RefreshEscalasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EscalasViewModel @Inject constructor(
    private val getEscalas: GetEscalasUseCase,
    private val refreshEscalas: RefreshEscalasUseCase,
    private val aprovarEscala: AprovarEscalaUseCase,
    private val cancelarEscala: CancelarEscalaUseCase,
    private val gerarEscala: GerarEscalaUseCase,
    private val repository: EscalaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EscalasUiState>(EscalasUiState.Loading)
    val uiState: StateFlow<EscalasUiState> = _uiState

    private val _events = Channel<EscalaEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeEscalas()
        refresh()
    }

    private fun observeEscalas() {
        viewModelScope.launch {
            getEscalas()
                .catch { e -> _uiState.value = EscalasUiState.Error(e.message ?: "Erro") }
                .collect { escalas ->
                    val current = _uiState.value
                    val filter = if (current is EscalasUiState.Success) current.statusFilter else null
                    _uiState.value = EscalasUiState.Success(
                        escalas = escalas,
                        filtered = if (filter == null) escalas else escalas.filter { it.status == filter },
                        statusFilter = filter,
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            when (val result = refreshEscalas()) {
                is ApiResult.Error -> _events.send(EscalaEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun setStatusFilter(status: StatusEscala?) {
        val current = _uiState.value as? EscalasUiState.Success ?: return
        _uiState.value = current.copy(
            statusFilter = status,
            filtered = if (status == null) current.escalas else current.escalas.filter { it.status == status },
        )
    }

    fun aprovar(id: Long) {
        viewModelScope.launch {
            when (val result = aprovarEscala(id)) {
                is ApiResult.Success -> _events.send(EscalaEvent.Approved)
                is ApiResult.Error -> _events.send(EscalaEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun confirmar(id: Long) {
        viewModelScope.launch {
            when (val result = repository.confirmar(id)) {
                is ApiResult.Success -> _events.send(EscalaEvent.Confirmed)
                is ApiResult.Error -> _events.send(EscalaEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun cancelar(id: Long) {
        viewModelScope.launch {
            when (val result = cancelarEscala(id)) {
                is ApiResult.Success -> _events.send(EscalaEvent.Cancelled)
                is ApiResult.Error -> _events.send(EscalaEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun gerar(eventoId: Long) {
        viewModelScope.launch {
            when (val result = gerarEscala(eventoId)) {
                is ApiResult.Success -> _events.send(EscalaEvent.Generated)
                is ApiResult.Error -> _events.send(EscalaEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            when (val result = repository.delete(id)) {
                is ApiResult.Success -> _events.send(EscalaEvent.Deleted)
                is ApiResult.Error -> _events.send(EscalaEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }
}
