package com.escala.ministerial.feature.eventos.presentation

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.BadgeVariant
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.core.ui.components.StatusBadge
import com.escala.ministerial.feature.eventos.domain.model.Evento
import java.time.format.DateTimeFormatter

private val DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun EventosScreen(viewModel: EventosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showForm by remember { mutableStateOf(false) }
    var editingEvento by remember { mutableStateOf<Evento?>(null) }
    var confirmAction by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EventoEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is EventoEvent.Saved       -> { snackbarHostState.showSnackbar("Evento salvo"); showForm = false }
                is EventoEvent.Cancelled   -> snackbarHostState.showSnackbar("Evento cancelado")
                is EventoEvent.Deleted     -> snackbarHostState.showSnackbar("Evento removido")
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingEvento = null; showForm = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) { Icon(Icons.Default.Add, "Novo Evento") }
        },
    ) { padding ->
        when (val state = uiState) {
            is EventosUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is EventosUiState.Error   -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is EventosUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Eventos", style = MaterialTheme.typography.headlineSmall)
                        Text("${state.filtered.size} evento(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::search,
                    placeholder = { Text("Buscar evento, local ou tipo…") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.filtered, key = { it.id }) { evento ->
                        EventoCard(
                            evento = evento,
                            onEdit = { editingEvento = it; showForm = true },
                            onCancelar = { confirmAction = "Deseja cancelar este evento?" to { viewModel.cancelar(it.id) } },
                            onDelete = { confirmAction = "Deseja excluir este evento?" to { viewModel.delete(it.id) } },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showForm) {
        EventoFormDialog(evento = editingEvento, onDismiss = { showForm = false }, onSave = viewModel::save)
    }

    confirmAction?.let { (msg, action) ->
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            title = { Text("Confirmar ação") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { action(); confirmAction = null }) { Text("Confirmar", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { confirmAction = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun EventoCard(
    evento: Evento,
    onEdit: (Evento) -> Unit,
    onCancelar: (Evento) -> Unit,
    onDelete: (Evento) -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Name + status
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(evento.nome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        StatusBadge(
                            text = if (evento.cancelado) "Concluído" else "Ativo",
                            variant = if (evento.cancelado) BadgeVariant.NEUTRAL else BadgeVariant.SUCCESS,
                        )
                    }
                    // Metadata row
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MetaItem(Icons.Default.CalendarMonth, evento.data.format(DATE_FMT))
                        MetaItem(Icons.Default.Schedule, evento.horario)
                        evento.local?.let { MetaItem(Icons.Default.LocationOn, it) }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // Footer: type chip + capacity + actions
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(primaryContainer).padding(horizontal = 10.dp, vertical = 3.dp)) {
                        Text(evento.tipoEvento.label, style = MaterialTheme.typography.labelSmall, color = primary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Group, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text("Cap. ${evento.maxMinistros}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row {
                    if (!evento.cancelado) {
                        IconButton(onClick = { onEdit(evento) }, Modifier.size(34.dp)) { Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp)) }
                        IconButton(onClick = { onCancelar(evento) }, Modifier.size(34.dp)) { Icon(Icons.Default.Block, "Cancelar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)) }
                    }
                    IconButton(onClick = { onDelete(evento) }, Modifier.size(34.dp)) { Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MetaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
