package com.escala.ministerial.feature.ministros.presentation

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.escala.ministerial.feature.ministros.domain.model.Ministro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinistrosScreen(viewModel: MinistrosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showForm by remember { mutableStateOf(false) }
    var editingMinistro by remember { mutableStateOf<Ministro?>(null) }
    var deletingId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MinistroEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is MinistroEvent.Saved -> {
                    snackbarHostState.showSnackbar("Ministro salvo com sucesso")
                    showForm = false
                    editingMinistro = null
                }
                is MinistroEvent.Deleted -> snackbarHostState.showSnackbar("Ministro removido")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ministros") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true; editingMinistro = null }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Ministro")
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is MinistrosUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is MinistrosUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding),
            )
            is MinistrosUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::search,
                        label = { Text("Buscar por nome ou e-mail") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    FilterChip(
                        selected = state.soAtivos,
                        onClick = { viewModel.toggleSoAtivos(!state.soAtivos) },
                        label = { Text("Somente ativos") },
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.filteredMinistros, key = { it.id }) { ministro ->
                        MinistroCard(
                            ministro = ministro,
                            onEdit = { editingMinistro = it; showForm = true },
                            onDelete = { deletingId = it.id },
                        )
                    }
                }
            }
        }
    }

    if (showForm) {
        MinistroFormDialog(
            ministro = editingMinistro,
            onDismiss = { showForm = false; editingMinistro = null },
            onSave = viewModel::save,
        )
    }

    deletingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Deseja remover este ministro?") },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(id); deletingId = null }) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingId = null }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun MinistroCard(
    ministro: Ministro,
    onEdit: (Ministro) -> Unit,
    onDelete: (Ministro) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(ministro.nome, style = MaterialTheme.typography.titleMedium)
                    Text(ministro.email, style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    IconButton(onClick = { onEdit(ministro) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDelete(ministro) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(
                    text = if (ministro.ativo) "Ativo" else "Inativo",
                    variant = if (ministro.ativo) BadgeVariant.SUCCESS else BadgeVariant.NEUTRAL,
                )
                StatusBadge(text = ministro.funcao.label, variant = BadgeVariant.INFO)
            }
            ministro.telefone?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
