package com.escala.ministerial.feature.auditoria.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.auditoria.domain.usecase.GetLogsAuditoriaUseCase
import com.escala.ministerial.feature.auditoria.domain.usecase.RefreshAuditoriaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuditoriaViewModel @Inject constructor(
    private val getLogs: GetLogsAuditoriaUseCase,
    private val refreshAuditoria: RefreshAuditoriaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuditoriaUiState>(AuditoriaUiState.Loading)
    val uiState: StateFlow<AuditoriaUiState> = _uiState

    init {
        viewModelScope.launch {
            getLogs()
                .catch { e -> _uiState.value = AuditoriaUiState.Error(e.message ?: "Erro") }
                .collect { logs -> _uiState.value = AuditoriaUiState.Success(logs) }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { refreshAuditoria() }
    }

    fun setEntidadeFilter(entidade: String?) {
        val current = _uiState.value as? AuditoriaUiState.Success ?: return
        _uiState.value = current.copy(
            entidadeFilter = entidade,
            filtered = if (entidade == null) current.logs else current.logs.filter { it.entidade.equals(entidade, true) },
        )
    }
}
