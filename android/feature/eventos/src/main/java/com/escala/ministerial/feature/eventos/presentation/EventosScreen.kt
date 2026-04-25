package com.escala.ministerial.feature.eventos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Eventos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    OutlinedButton(
                        onClick = viewModel::seedTestData,
                        modifier = Modifier.padding(end = 16.dp),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text("Dados Teste")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingEvento = null; showForm = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo evento")
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is EventosUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is EventosUiState.Error -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is EventosUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                // Search Section
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::search,
                        label = { Text("Buscar evento") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                    )
                }

                // Events List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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
            // Header with event info and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        evento.nome,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${evento.data.format(DATE_FORMATTER)} • ${evento.horario}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    evento.local?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (!evento.cancelado) {
                        IconButton(
                            onClick = { onEdit(evento) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { onCancelar(evento) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = "Cancelar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(
                        onClick = { onDelete(evento) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Status badges and capacity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(text = evento.tipoEvento.label, variant = BadgeVariant.INFO)
                    if (evento.cancelado) StatusBadge(text = "Cancelado", variant = BadgeVariant.ERROR)
                }
                Text(
                    "Máx: ${evento.maxMinistros}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
