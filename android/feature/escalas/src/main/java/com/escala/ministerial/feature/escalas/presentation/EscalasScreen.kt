package com.escala.ministerial.feature.escalas.presentation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.Avatar
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.core.ui.components.StatusBadge
import com.escala.ministerial.core.ui.components.escalaBadgeVariant
import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.model.StatusEscala
import java.time.format.DateTimeFormatter

private val DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun EscalasScreen(viewModel: EscalasViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmAction by remember { mutableStateOf<Triple<String, String, () -> Unit>?>(null) }
    var gerarEventoId by remember { mutableStateOf("") }
    var showGerarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EscalaEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is EscalaEvent.Approved   -> snackbarHostState.showSnackbar("Escala aprovada")
                is EscalaEvent.Confirmed  -> snackbarHostState.showSnackbar("Escala confirmada")
                is EscalaEvent.Cancelled  -> snackbarHostState.showSnackbar("Escala cancelada")
                is EscalaEvent.Generated  -> snackbarHostState.showSnackbar("Escala gerada")
                is EscalaEvent.Created    -> snackbarHostState.showSnackbar("Escala criada")
                is EscalaEvent.Deleted    -> snackbarHostState.showSnackbar("Escala deletada")
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showGerarDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) { Icon(Icons.Default.Add, "Gerar Escala") }
        },
    ) { padding ->
        when (val state = uiState) {
            is EscalasUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is EscalasUiState.Error   -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is EscalasUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Escalas", style = MaterialTheme.typography.headlineSmall)
                        Text("${state.filtered.size} escala(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Filter chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = state.statusFilter == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text("Todos") },
                            shape = RoundedCornerShape(99.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    items(StatusEscala.entries) { s ->
                        FilterChip(
                            selected = state.statusFilter == s,
                            onClick = { viewModel.setStatusFilter(s) },
                            label = { Text(s.label) },
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.filtered, key = { it.id }) { escala ->
                        EscalaCard(
                            escala = escala,
                            onAprovar = { confirmAction = Triple("Aprovar Escala", "Deseja aprovar a escala de \"${it.eventoNome}\"?") { viewModel.aprovar(it.id) } },
                            onConfirmar = { confirmAction = Triple("Confirmar Escala", "Deseja confirmar a escala de \"${it.eventoNome}\"?") { viewModel.confirmar(it.id) } },
                            onCancelar = { confirmAction = Triple("Cancelar Escala", "Deseja cancelar a escala de \"${it.eventoNome}\"?") { viewModel.cancelar(it.id) } },
                            onDeletar = { confirmAction = Triple("Excluir Escala", "Deseja excluir permanentemente?") { viewModel.delete(it.id) } },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showGerarDialog) {
        AlertDialog(
            onDismissRequest = { showGerarDialog = false },
            title = { Text("Gerar Nova Escala") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Informe o ID do evento para gerar a escala automaticamente.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(value = gerarEventoId, onValueChange = { gerarEventoId = it }, label = { Text("ID do Evento") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = { gerarEventoId.toLongOrNull()?.let { viewModel.gerar(it) }; showGerarDialog = false }) { Text("Gerar") }
            },
            dismissButton = { TextButton(onClick = { showGerarDialog = false }) { Text("Cancelar") } },
        )
    }

    confirmAction?.let { (title, msg, action) ->
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            title = { Text(title) },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { action(); confirmAction = null }) { Text("Confirmar") } },
            dismissButton = { TextButton(onClick = { confirmAction = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun EscalaCard(
    escala: Escala,
    onAprovar: (Escala) -> Unit,
    onConfirmar: (Escala) -> Unit,
    onCancelar: (Escala) -> Unit,
    onDeletar: (Escala) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(escala.eventoNome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    escala.eventoData?.let {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                            Text(it.format(DATE_FMT), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                StatusBadge(text = escala.status.label, variant = escalaBadgeVariant(escala.status.name))
            }

            // Ministers list
            if (escala.ministros.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "MINISTROS ESCALADOS",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.08.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    escala.ministros.forEach { m ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Avatar(
                                nome = m.ministroNome, size = 28.dp,
                                color = if (m.confirmacaoMinistro) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Column(Modifier.weight(1f)) {
                                Text(m.ministroNome, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            if (m.confirmacaoMinistro) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(12.dp))
                                    Text("Confirmado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            } else {
                                Text("Pendente", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Actions
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (escala.status == StatusEscala.PROPOSTA) {
                    Button(
                        onClick = { onAprovar(escala) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Aprovar", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (escala.status == StatusEscala.APROVADA) {
                    Button(
                        onClick = { onConfirmar(escala) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Confirmar", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (escala.status == StatusEscala.PROPOSTA || escala.status == StatusEscala.APROVADA) {
                    TextButton(onClick = { onCancelar(escala) }) {
                        Icon(Icons.Default.Block, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Cancelar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onDeletar(escala) }, Modifier.size(34.dp)) {
                    Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
private val Double.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
