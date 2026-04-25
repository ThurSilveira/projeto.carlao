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
import androidx.compose.material3.CardDefaults
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Auditoria",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
    ) { padding ->
        when (val state = uiState) {
            is AuditoriaUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is AuditoriaUiState.Error -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is AuditoriaUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                // Filter Section
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Filtros",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            FilterChip(
                                selected = state.entidadeFilter == null,
                                onClick = { viewModel.setEntidadeFilter(null) },
                                label = { Text("Todos") },
                                shape = MaterialTheme.shapes.large,
                            )
                        }
                        items(ENTIDADES) { entidade ->
                            FilterChip(
                                selected = state.entidadeFilter == entidade,
                                onClick = { viewModel.setEntidadeFilter(entidade) },
                                label = { Text(entidade) },
                                shape = MaterialTheme.shapes.large,
                            )
                        }
                    }
                }

                // Audit Logs List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header with entity, action and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = log.entidade, variant = BadgeVariant.NEUTRAL)
                    StatusBadge(text = log.acao.label, variant = acaoBadgeVariant)
                }
                Text(
                    log.dataHora.format(DT_FORMATTER),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status change
            if (log.statusAnterior != null || log.statusNovo != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Status: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    log.statusAnterior?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            " → ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    log.statusNovo?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // User who performed the action
            log.realizadoPorId?.let {
                Text(
                    "Realizado por: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
