package com.escala.ministerial.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.core.ui.components.StatusBadge
import com.escala.ministerial.core.ui.components.escalaBadgeVariant
import com.escala.ministerial.core.ui.theme.DarkAmber
import com.escala.ministerial.core.ui.theme.DarkAmberContainer
import com.escala.ministerial.core.ui.theme.DarkError
import com.escala.ministerial.core.ui.theme.DarkErrorContainer
import com.escala.ministerial.core.ui.theme.DarkInfo
import com.escala.ministerial.core.ui.theme.DarkInfoContainer
import com.escala.ministerial.core.ui.theme.DarkPrimary
import com.escala.ministerial.core.ui.theme.DarkPrimaryContainer
import com.escala.ministerial.core.ui.theme.DarkSecondary
import com.escala.ministerial.core.ui.theme.DarkSecondaryContainer
import com.escala.ministerial.core.ui.theme.DarkSurface
import com.escala.ministerial.core.ui.theme.LightAmber
import com.escala.ministerial.core.ui.theme.LightAmberContainer
import com.escala.ministerial.core.ui.theme.LightError
import com.escala.ministerial.core.ui.theme.LightErrorContainer
import com.escala.ministerial.core.ui.theme.LightInfo
import com.escala.ministerial.core.ui.theme.LightInfoContainer
import com.escala.ministerial.core.ui.theme.LightPrimary
import com.escala.ministerial.core.ui.theme.LightPrimaryContainer
import com.escala.ministerial.core.ui.theme.LightSecondary
import com.escala.ministerial.core.ui.theme.LightSecondaryContainer
import com.escala.ministerial.core.ui.theme.Purple
import com.escala.ministerial.core.ui.theme.PurpleContainer
import com.escala.ministerial.feature.dashboard.domain.model.DashboardStats
import com.escala.ministerial.feature.dashboard.domain.model.EscalaRecente
import com.escala.ministerial.feature.dashboard.domain.model.ProximoEvento
import java.time.format.DateTimeFormatter

private val DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val MONTH_FMT = DateTimeFormatter.ofPattern("MMM")

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        when (val state = uiState) {
            is DashboardUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is DashboardUiState.Error   -> ErrorScreen(state.message, viewModel::loadDashboard, Modifier.padding(padding))
            is DashboardUiState.Success -> DashboardContent(
                stats = state.stats,
                onNavigate = onNavigate,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun DashboardContent(
    stats: DashboardStats,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val dark = MaterialTheme.colorScheme.surface == DarkSurface

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        // ── Hero ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(primary, primary.copy(alpha = 0.80f))),
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                )
                .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Bem-vindo de volta", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.65f))
                Text("Painel de Controle", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text("Escala Ministerial", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.60f))
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // ── KPI grid ──────────────────────────────────────────────────
            val kpis = listOf(
                KpiData("Ministros Ativos",      "${stats.ministrosAtivos}",   Icons.Default.Group,         if (dark) DarkPrimary   else LightPrimary,   if (dark) DarkPrimaryContainer   else LightPrimaryContainer,   "ministros"),
                KpiData("Eventos este Mês",       "${stats.eventosAtivos}",    Icons.Default.CalendarMonth, if (dark) DarkSecondary else LightSecondary, if (dark) DarkSecondaryContainer else LightSecondaryContainer, "eventos"),
                KpiData("Escalas Aprovadas",      "${stats.escalasAprovadas}", Icons.Default.Schedule,      Purple,                                      PurpleContainer,                                              "escalas"),
                KpiData("Nota Média",             if (stats.mediaNota > 0) "%.1f".format(stats.mediaNota) else "—", Icons.Default.Star, if (dark) DarkAmber else LightAmber, if (dark) DarkAmberContainer else LightAmberContainer, "feedback"),
                KpiData("Feedbacks Pendentes",    "${stats.feedbacksPendentes}", Icons.Default.Reply,       if (dark) DarkError     else LightError,     if (dark) DarkErrorContainer     else LightErrorContainer,     "feedback"),
                KpiData("Registros de Auditoria", "${stats.logsAuditoria}",   Icons.Default.Assessment,    if (dark) DarkInfo      else LightInfo,      if (dark) DarkInfoContainer      else LightInfoContainer,      "auditoria"),
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                kpis.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { kpi -> KpiCard(kpi, Modifier.weight(1f)) { onNavigate(kpi.screen) } }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            // ── Próximos Eventos ──────────────────────────────────────────
            if (stats.proximosEventos.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader("Próximos Eventos") { onNavigate("eventos") }
                    stats.proximosEventos.forEach { ProximoEventoCard(it) { onNavigate("eventos") } }
                }
            }

            // ── Escalas Recentes ──────────────────────────────────────────
            if (stats.escalasRecentes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Escalas Recentes") { onNavigate("escalas") }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                    ) {
                        stats.escalasRecentes.forEachIndexed { i, esc ->
                            EscalaRecenteRow(esc)
                            if (i < stats.escalasRecentes.lastIndex)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, onAction: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onAction) {
            Text("Ver todos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private data class KpiData(val label: String, val value: String, val icon: ImageVector, val iconColor: Color, val iconBg: Color, val screen: String)

@Composable
private fun KpiCard(kpi: KpiData, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(kpi.iconBg), contentAlignment = Alignment.Center) {
                Icon(kpi.icon, null, tint = kpi.iconColor, modifier = Modifier.size(20.dp))
            }
            Text(kpi.value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onSurface)
            Text(kpi.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
private fun ProximoEventoCard(ev: ProximoEvento, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(ev.data.dayOfMonth.toString(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = primary)
                Text(ev.data.format(MONTH_FMT).uppercase(), style = MaterialTheme.typography.labelSmall, color = primary)
            }
            Column(Modifier.weight(1f)) {
                Text(ev.nome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                    Text(ev.horario, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(primaryContainer).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text(ev.tipo, style = MaterialTheme.typography.labelSmall, color = primary)
            }
        }
    }
}

@Composable
private fun EscalaRecenteRow(esc: EscalaRecente) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(esc.eventoNome, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${esc.eventoData.format(DATE_FMT)} • ${esc.totalMinistros} ministro(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        StatusBadge(text = esc.status.lowercase().replaceFirstChar { it.uppercase() }, variant = escalaBadgeVariant(esc.status))
    }
}
