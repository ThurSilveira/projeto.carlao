package com.escala.ministerial.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.core.data.database.dao.EscalaDao
import com.escala.ministerial.core.data.database.dao.EventoDao
import com.escala.ministerial.core.data.database.dao.FeedbackDao
import com.escala.ministerial.core.data.database.dao.MinistroDao
import com.escala.ministerial.feature.dashboard.domain.model.DashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val ministroDao: MinistroDao,
    private val eventoDao: EventoDao,
    private val escalaDao: EscalaDao,
    private val feedbackDao: FeedbackDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        observeLocalStats()
    }

    fun loadDashboard() {
        _uiState.value = DashboardUiState.Loading
        observeLocalStats()
    }

    private fun observeLocalStats() {
        viewModelScope.launch {
            combine(
                ministroDao.observeAll(),
                eventoDao.observeAll(),
                escalaDao.observeAll(),
                feedbackDao.observeAll(),
            ) { ministros, eventos, escalas, feedbacks ->
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
            }.catch { e ->
                _uiState.value = DashboardUiState.Error(e.message ?: "Erro ao carregar dados")
            }.collect { stats ->
                _uiState.value = DashboardUiState.Success(stats)
            }
        }
    }
}
