package com.escala.ministerial.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.feature.dashboard.data.datasource.DashboardApiService
import com.escala.ministerial.feature.dashboard.domain.model.DashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val api: DashboardApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            runCatching {
                val ministrosDeferred = async { api.getMinistros() }
                val eventosDeferred = async { api.getEventos() }
                val escalasDeferred = async { api.getEscalas() }
                val feedbacksDeferred = async { api.getFeedbacks() }

                val ministros = ministrosDeferred.await()
                val eventos = eventosDeferred.await()
                val escalas = escalasDeferred.await()
                val feedbacks = feedbacksDeferred.await()

                val notas = feedbacks.map { it.nota }
                DashboardStats(
                    totalMinistros = ministros.size,
                    ministrosAtivos = ministros.count { it.ativo },
                    totalEventos = eventos.size,
                    eventosAtivos = eventos.count { !it.cancelado },
                    totalEscalas = escalas.size,
                    escalasAprovadas = escalas.count { it.status == "APROVADA" },
                    feedbacksPendentes = feedbacks.count { it.status == "PENDENTE" },
                    mediaNota = if (notas.isEmpty()) 0.0 else notas.average(),
                )
            }.onSuccess { stats ->
                _uiState.value = DashboardUiState.Success(stats)
            }.onFailure { e ->
                _uiState.value = DashboardUiState.Error(e.message ?: "Erro ao carregar dados")
            }
        }
    }
}
