package com.escala.ministerial.feature.auditoria.presentation

import com.escala.ministerial.feature.auditoria.domain.model.LogAuditoria

sealed interface AuditoriaUiState {
    data object Loading : AuditoriaUiState
    data class Success(
        val logs: List<LogAuditoria>,
        val filtered: List<LogAuditoria> = logs,
        val entidadeFilter: String? = null,
    ) : AuditoriaUiState
    data class Error(val message: String) : AuditoriaUiState
}
