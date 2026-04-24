package com.escala.ministerial.feature.ministros.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.usecase.DeleteMinistroUseCase
import com.escala.ministerial.feature.ministros.domain.usecase.GetMinistrosUseCase
import com.escala.ministerial.feature.ministros.domain.usecase.RefreshMinistrosUseCase
import com.escala.ministerial.feature.ministros.domain.usecase.SaveMinistroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MinistrosViewModel @Inject constructor(
    private val getMinistros: GetMinistrosUseCase,
    private val refreshMinistros: RefreshMinistrosUseCase,
    private val saveMinistro: SaveMinistroUseCase,
    private val deleteMinistro: DeleteMinistroUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MinistrosUiState>(MinistrosUiState.Loading)
    val uiState: StateFlow<MinistrosUiState> = _uiState

    private val _events = Channel<MinistroEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeMinistros()
        refresh()
    }

    private fun observeMinistros() {
        viewModelScope.launch {
            getMinistros()
                .catch { e ->
                    _uiState.value = MinistrosUiState.Error(e.message ?: "Erro desconhecido")
                }
                .collect { ministros ->
                    val current = _uiState.value
                    val query = if (current is MinistrosUiState.Success) current.query else ""
                    val soAtivos = if (current is MinistrosUiState.Success) current.soAtivos else false
                    _uiState.value = MinistrosUiState.Success(
                        ministros = ministros,
                        filteredMinistros = ministros.filter(query, soAtivos),
                        query = query,
                        soAtivos = soAtivos,
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            when (val result = refreshMinistros()) {
                is ApiResult.Error -> _events.send(MinistroEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun search(query: String) {
        val current = _uiState.value as? MinistrosUiState.Success ?: return
        _uiState.value = current.copy(
            query = query,
            filteredMinistros = current.ministros.filter(query, current.soAtivos),
        )
    }

    fun toggleSoAtivos(enabled: Boolean) {
        val current = _uiState.value as? MinistrosUiState.Success ?: return
        _uiState.value = current.copy(
            soAtivos = enabled,
            filteredMinistros = current.ministros.filter(current.query, enabled),
        )
    }

    fun save(ministro: Ministro) {
        viewModelScope.launch {
            when (val result = saveMinistro(ministro)) {
                is ApiResult.Success -> _events.send(MinistroEvent.Saved)
                is ApiResult.Error -> _events.send(MinistroEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            when (val result = deleteMinistro(id)) {
                is ApiResult.Success -> _events.send(MinistroEvent.Deleted)
                is ApiResult.Error -> _events.send(MinistroEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    private fun List<Ministro>.filter(query: String, soAtivos: Boolean): List<Ministro> =
        this.filter { m ->
            (query.isBlank() || m.nome.contains(query, ignoreCase = true) || m.email.contains(query, ignoreCase = true)) &&
                (!soAtivos || m.ativo)
        }
}
