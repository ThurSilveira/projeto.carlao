package com.escala.ministerial.feature.auditoria.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun AuditoriaScreen(viewModel: AuditoriaViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        when (val state = uiState) {
            is AuditoriaUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is AuditoriaUiState.Error   -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is AuditoriaUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Auditoria", style = MaterialTheme.typography.headlineSmall)
                        Text("${state.filtered.size} registro(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = state.entidadeFilter == null,
                            onClick = { viewModel.setEntidadeFilter(null) },
                            label = { Text("Todas") },
                            shape = RoundedCornerShape(99.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    items(ENTIDADES) { entidade ->
                        FilterChip(
                            selected = state.entidadeFilter == entidade,
                            onClick = { viewModel.setEntidadeFilter(entidade) },
                            label = { Text(entidade) },
                            shape = RoundedCornerShape(99.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.filtered, key = { it.id }) { log ->
                        LogCard(log)
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: LogAuditoria) {
    val iconColor: Color = acaoIconColor(log.acao)
    val icon: ImageVector = acaoIcon(log.acao)
    val acaoBadgeVariant = when (log.acao) {
        TipoAcao.CRIADO                        -> BadgeVariant.SUCCESS
        TipoAcao.DELETADO                      -> BadgeVariant.ERROR
        TipoAcao.APROVADO, TipoAcao.CONFIRMADO -> BadgeVariant.PRIMARY
        TipoAcao.CANCELADO                     -> BadgeVariant.ERROR
        TipoAcao.ATUALIZADO, TipoAcao.SUBSTITUIDO -> BadgeVariant.WARNING
        TipoAcao.NOTIFICADO                    -> BadgeVariant.INFO
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = log.entidade, variant = BadgeVariant.NEUTRAL)
                    StatusBadge(text = log.acao.label, variant = acaoBadgeVariant)
                }

                if (log.statusAnterior != null || log.statusNovo != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        log.statusAnterior?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("→", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        log.statusNovo?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(log.dataHora.format(DT_FORMATTER), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    log.realizadoPorId?.let {
                        Text("por $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun acaoIconColor(acao: TipoAcao): Color = when (acao) {
    TipoAcao.CRIADO                        -> MaterialTheme.colorScheme.secondary
    TipoAcao.DELETADO, TipoAcao.CANCELADO  -> MaterialTheme.colorScheme.error
    TipoAcao.APROVADO, TipoAcao.CONFIRMADO -> MaterialTheme.colorScheme.primary
    TipoAcao.SUBSTITUIDO                   -> MaterialTheme.colorScheme.tertiary
    TipoAcao.ATUALIZADO                    -> MaterialTheme.colorScheme.onSurfaceVariant
    TipoAcao.NOTIFICADO                    -> MaterialTheme.colorScheme.tertiary
}

private fun acaoIcon(acao: TipoAcao): ImageVector = when (acao) {
    TipoAcao.CRIADO      -> Icons.Default.Add
    TipoAcao.ATUALIZADO  -> Icons.Default.Edit
    TipoAcao.DELETADO    -> Icons.Default.Delete
    TipoAcao.APROVADO    -> Icons.Default.CheckCircle
    TipoAcao.CONFIRMADO  -> Icons.Default.Check
    TipoAcao.CANCELADO   -> Icons.Default.Block
    TipoAcao.SUBSTITUIDO -> Icons.Default.Group
    TipoAcao.NOTIFICADO  -> Icons.Default.Feedback
}
