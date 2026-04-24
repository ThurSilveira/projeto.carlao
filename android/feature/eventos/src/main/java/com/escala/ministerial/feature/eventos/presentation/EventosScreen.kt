package com.escala.ministerial.feature.eventos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.escala.ministerial.core.ui.components.BadgeVariant
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.core.ui.components.StatusBadge
import com.escala.ministerial.feature.eventos.domain.model.Evento
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
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
                is EventoEvent.Saved -> { snackbarHostState.showSnackbar("Evento salvo"); showForm = false }
                is EventoEvent.Cancelled -> snackbarHostState.showSnackbar("Evento cancelado")
                is EventoEvent.Deleted -> snackbarHostState.showSnackbar("Evento removido")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Eventos") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { editingEvento = null; showForm = true }) {
                Icon(Icons.Default.Add, null)
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is EventosUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is EventosUiState.Error -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is EventosUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::search,
                    label = { Text("Buscar evento") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    singleLine = true,
                )
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.filtered, key = { it.id }) { evento ->
                        EventoCard(
                            evento = evento,
                            onEdit = { editingEvento = it; showForm = true },
                            onCancelar = { confirmAction = "Cancelar evento?" to { viewModel.cancelar(it.id) } },
                            onDelete = { confirmAction = "Excluir evento?" to { viewModel.delete(it.id) } },
                        )
                    }
                }
            }
        }
    }

    if (showForm) {
        EventoFormDialog(
            evento = editingEvento,
            onDismiss = { showForm = false },
            onSave = viewModel::save,
        )
    }

    confirmAction?.let { (msg, action) ->
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            title = { Text("Confirmar") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { action(); confirmAction = null }) { Text("Confirmar") } },
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(evento.nome, style = MaterialTheme.typography.titleMedium)
                    Text("${evento.data.format(DATE_FORMATTER)} • ${evento.horario}", style = MaterialTheme.typography.bodySmall)
                    evento.local?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
                Row {
                    if (!evento.cancelado) {
                        IconButton(onClick = { onEdit(evento) }) { Icon(Icons.Default.Edit, null) }
                        IconButton(onClick = { onCancelar(evento) }) { Icon(Icons.Default.Block, null) }
                    }
                    IconButton(onClick = { onDelete(evento) }) { Icon(Icons.Default.Delete, null) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = evento.tipoEvento.label, variant = BadgeVariant.INFO)
                if (evento.cancelado) StatusBadge(text = "Cancelado", variant = BadgeVariant.ERROR)
                Text("Máx: ${evento.maxMinistros}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
