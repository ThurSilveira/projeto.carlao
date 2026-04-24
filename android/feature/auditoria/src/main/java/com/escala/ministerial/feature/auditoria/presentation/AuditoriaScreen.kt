package com.escala.ministerial.feature.auditoria.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.BadgeVariant
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.core.ui.components.StatusBadge
import com.escala.ministerial.feature.auditoria.domain.model.LogAuditoria
import com.escala.ministerial.feature.auditoria.domain.model.TipoAcao
import java.time.format.DateTimeFormatter

private val DT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
private val ENTIDADES = listOf("Ministro", "Evento", "Escala", "Feedback")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditoriaScreen(viewModel: AuditoriaViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Auditoria") }) },
    ) { padding ->
        when (val state = uiState) {
            is AuditoriaUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is AuditoriaUiState.Error -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is AuditoriaUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = state.entidadeFilter == null,
                            onClick = { viewModel.setEntidadeFilter(null) },
                            label = { Text("Todos") },
                        )
                    }
                    items(ENTIDADES) { entidade ->
                        FilterChip(
                            selected = state.entidadeFilter == entidade,
                            onClick = { viewModel.setEntidadeFilter(entidade) },
                            label = { Text(entidade) },
                        )
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.filtered, key = { it.id }) { log ->
                        LogCard(log)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: LogAuditoria) {
    val acaoBadgeVariant = when (log.acao) {
        TipoAcao.CRIADO -> BadgeVariant.SUCCESS
        TipoAcao.DELETADO -> BadgeVariant.ERROR
        TipoAcao.APROVADO, TipoAcao.CONFIRMADO -> BadgeVariant.INFO
        TipoAcao.CANCELADO -> BadgeVariant.WARNING
        else -> BadgeVariant.NEUTRAL
    }
    val acaoColor = when (log.acao) {
        TipoAcao.CRIADO -> Color(0xFF4CAF50)
        TipoAcao.DELETADO -> Color(0xFFB00020)
        TipoAcao.APROVADO -> Color(0xFF1565C0)
        TipoAcao.CANCELADO -> Color(0xFFF59E0B)
        else -> Color.Gray
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = log.entidade, variant = BadgeVariant.NEUTRAL)
                    StatusBadge(text = log.acao.label, variant = acaoBadgeVariant)
                }
                Text(log.dataHora.format(DT_FORMATTER), style = MaterialTheme.typography.bodySmall)
            }
            if (log.statusAnterior != null || log.statusNovo != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    log.statusAnterior?.let { Text("$it →", style = MaterialTheme.typography.bodySmall) }
                    log.statusNovo?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = acaoColor) }
                }
            }
            log.realizadoPorId?.let {
                Text("Por: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
