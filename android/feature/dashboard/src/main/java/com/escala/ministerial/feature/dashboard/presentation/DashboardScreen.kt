package com.escala.ministerial.feature.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.escala.ministerial.core.ui.theme.*
import com.escala.ministerial.feature.dashboard.domain.model.DashboardStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::loadDashboard) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Welcome Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Bem-vindo de volta! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Aqui está um resumo dos principais indicadores do sistema",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Main Stats Section
        Text(
            "Resumo Geral",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // First Row - Ministros and Eventos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatCard(
                label = "Ministros",
                value = "${stats.ministrosAtivos}",
                subtitle = "de ${stats.totalMinistros} ativos",
                icon = Icons.Default.Group,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Eventos",
                value = "${stats.eventosAtivos}",
                subtitle = "de ${stats.totalEventos} ativos",
                icon = Icons.Default.CalendarMonth,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
            )
        }

        // Second Row - Escalas and Feedbacks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatCard(
                label = "Escalas",
                value = "${stats.escalasAprovadas}",
                subtitle = "de ${stats.totalEscalas} aprovadas",
                icon = Icons.Default.Schedule,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Feedbacks",
                value = "${stats.feedbacksPendentes}",
                subtitle = "pendentes",
                icon = Icons.Default.Feedback,
                color = WarningAmber,
                modifier = Modifier.weight(1f),
            )
        }

        // Indicators Section
        Text(
            "Indicadores de Performance",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatCard(
                label = "Nota Média",
                value = if (stats.mediaNota > 0) "%.1f".format(stats.mediaNota) else "—",
                subtitle = "feedback dos ministros",
                icon = Icons.Default.Star,
                color = TertiaryAmber,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Auditoria",
                value = "${stats.totalEscalas + stats.totalMinistros}",
                subtitle = "registros totais",
                icon = Icons.Default.Assessment,
                color = NeutralGrey,
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.2f)
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            )

            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
