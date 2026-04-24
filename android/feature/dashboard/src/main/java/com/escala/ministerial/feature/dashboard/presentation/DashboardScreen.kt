package com.escala.ministerial.feature.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.feature.dashboard.domain.model.DashboardStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = viewModel::loadDashboard) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is DashboardUiState.Loading -> LoadingScreen(modifier = Modifier.padding(padding))
            is DashboardUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = viewModel::loadDashboard,
                modifier = Modifier.padding(padding),
            )
            is DashboardUiState.Success -> DashboardContent(
                stats = state.stats,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun DashboardContent(stats: DashboardStats, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Resumo Geral", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Ministros",
                value = "${stats.ministrosAtivos}",
                subtitle = "de ${stats.totalMinistros} ativos",
                icon = Icons.Default.Group,
                color = Color(0xFF1565C0),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Eventos",
                value = "${stats.eventosAtivos}",
                subtitle = "de ${stats.totalEventos} ativos",
                icon = Icons.Default.CalendarMonth,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Escalas",
                value = "${stats.escalasAprovadas}",
                subtitle = "de ${stats.totalEscalas} aprovadas",
                icon = Icons.Default.Schedule,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Feedbacks",
                value = "${stats.feedbacksPendentes}",
                subtitle = "pendentes",
                icon = Icons.Default.Feedback,
                color = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Indicadores", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Nota Média",
                value = if (stats.mediaNota > 0) "%.1f".format(stats.mediaNota) else "—",
                subtitle = "feedback dos ministros",
                icon = Icons.Default.Star,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Auditoria",
                value = "${stats.totalEscalas + stats.totalMinistros}",
                subtitle = "registros totais",
                icon = Icons.Default.Assessment,
                color = Color(0xFF607D8B),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(icon, contentDescription = null, tint = color)
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = color)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
