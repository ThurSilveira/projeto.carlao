package com.escala.ministerial.feature.escalas.presentation

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.model.StatusEscala
import java.time.format.DateTimeFormatter

private val FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscalasScreen(viewModel: EscalasViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmAction by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }
    var gerarEventoId by remember { mutableStateOf("") }
    var showGerarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EscalaEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is EscalaEvent.Approved -> snackbarHostState.showSnackbar("Escala aprovada")
                is EscalaEvent.Cancelled -> snackbarHostState.showSnackbar("Escala cancelada")
                is EscalaEvent.Generated -> snackbarHostState.showSnackbar("Escala gerada automaticamente")
                is EscalaEvent.Created -> snackbarHostState.showSnackbar("Escala criada")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escalas") },
                actions = {
                    IconButton(onClick = { showGerarDialog = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Gerar escala automática")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val state = uiState) {
            is EscalasUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is EscalasUiState.Error -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is EscalasUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = state.statusFilter == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text("Todos") },
                        )
                    }
                    items(StatusEscala.entries) { s ->
                        FilterChip(
                            selected = state.statusFilter == s,
                            onClick = { viewModel.setStatusFilter(s) },
                            label = { Text(s.label) },
                        )
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.filtered, key = { it.id }) { escala ->
                        EscalaCard(
                            escala = escala,
                            onAprovar = { confirmAction = "Aprovar esta escala?" to { viewModel.aprovar(it.id) } },
                            onCancelar = { confirmAction = "Cancelar esta escala?" to { viewModel.cancelar(it.id) } },
                        )
                    }
                }
            }
        }
    }

    if (showGerarDialog) {
        AlertDialog(
            onDismissRequest = { showGerarDialog = false },
            title = { Text("Gerar Escala Automática") },
            text = {
                OutlinedTextField(
                    value = gerarEventoId,
                    onValueChange = { gerarEventoId = it },
                    label = { Text("ID do Evento") },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    gerarEventoId.toLongOrNull()?.let { viewModel.gerar(it) }
                    showGerarDialog = false
                }) { Text("Gerar") }
            },
            dismissButton = { TextButton(onClick = { showGerarDialog = false }) { Text("Cancelar") } },
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
private fun EscalaCard(
    escala: Escala,
    onAprovar: (Escala) -> Unit,
    onCancelar: (Escala) -> Unit,
) {
    val badgeVariant = when (escala.status) {
        StatusEscala.APROVADA -> BadgeVariant.SUCCESS
        StatusEscala.CANCELADA -> BadgeVariant.ERROR
        StatusEscala.CONFIRMADA -> BadgeVariant.INFO
        StatusEscala.PROPOSTA -> BadgeVariant.WARNING
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(escala.eventoNome, style = MaterialTheme.typography.titleMedium)
                    escala.eventoData?.let {
                        Text("${it.format(FORMATTER)} • ${escala.eventoHorario ?: ""}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("${escala.ministros.size} ministros", style = MaterialTheme.typography.bodySmall)
                }
                if (escala.status == StatusEscala.PROPOSTA) {
                    Row {
                        IconButton(onClick = { onAprovar(escala) }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Aprovar")
                        }
                        IconButton(onClick = { onCancelar(escala) }) {
                            Icon(Icons.Default.Block, contentDescription = "Cancelar")
                        }
                    }
                }
            }
            StatusBadge(text = escala.status.label, variant = badgeVariant)
            escala.observacao?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
