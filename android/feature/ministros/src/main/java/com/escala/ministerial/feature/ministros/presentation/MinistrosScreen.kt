package com.escala.ministerial.feature.ministros.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
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
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Indisponibilidade
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinistrosScreen(viewModel: MinistrosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val indispState by viewModel.indisponibilidadeState.collectAsState()
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ministros",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                onClick = { showForm = true; editingMinistro = null },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
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
                // Search and Filter Section
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Stats Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "${state.filteredMinistros.size} ministros",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                if (state.soAtivos) "Exibindo apenas ativos" else "Exibindo todos os ministros",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            )
                        }
                    }

                    // Search Field
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::search,
                        label = { Text("Buscar por nome ou e-mail") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                    )

                    // Filter Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = state.soAtivos,
                            onClick = { viewModel.toggleSoAtivos(!state.soAtivos) },
                            label = { Text("Somente ativos") },
                            shape = MaterialTheme.shapes.large,
                        )
                        SuggestionChip(
                            onClick = viewModel::seedTestData,
                            label = { Text("Adicionar dados teste") },
                            shape = MaterialTheme.shapes.large,
                        )
                    }
                }

                // Ministers List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.filteredMinistros, key = { it.id }) { ministro ->
                        MinistroCard(
                            ministro = ministro,
                            onEdit = { editingMinistro = it; showForm = true },
                            onDelete = { deletingId = it.id },
                            onIndisponibilidades = { viewModel.openIndisponibilidades(it) },
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
                TextButton(onClick = { viewModel.delete(id); deletingId = null }) { Text("Remover") }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Cancelar") } },
        )
    }

    if (indispState is IndisponibilidadeUiState.Active) {
        IndisponibilidadeDialog(
            state = indispState as IndisponibilidadeUiState.Active,
            onCreate = viewModel::createIndisponibilidade,
            onUpdate = viewModel::updateIndisponibilidade,
            onDelete = viewModel::deleteIndisponibilidade,
            onDismiss = viewModel::closeIndisponibilidades,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MinistroCard(
    ministro: Ministro,
    onEdit: (Ministro) -> Unit,
    onDelete: (Ministro) -> Unit,
    onIndisponibilidades: (Ministro) -> Unit,
) {
    val funcaoLabel = if (ministro.funcao == FuncaoMinistro.OUTRO && !ministro.funcaoEspecificada.isNullOrBlank())
        ministro.funcaoEspecificada else ministro.funcao.label

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
            // Header with name and actions
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        ministro.nome,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        ministro.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ministro.telefone?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { onIndisponibilidades(ministro) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Indisponibilidades",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onEdit(ministro) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { onDelete(ministro) },
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

            // Status badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(
                    text = if (ministro.ativo) "Ativo" else "Inativo",
                    variant = if (ministro.ativo) BadgeVariant.SUCCESS else BadgeVariant.NEUTRAL,
                )
                StatusBadge(text = funcaoLabel, variant = BadgeVariant.INFO)
            }

            // Scheduled scales
            if (ministro.escalasAgendadas.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    "Escalas agendadas:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ministro.escalasAgendadas.forEach { d ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    d.format(DATE_FMT),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            shape = MaterialTheme.shapes.small,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IndisponibilidadeDialog(
    state: IndisponibilidadeUiState.Active,
    onCreate: (Indisponibilidade) -> Unit,
    onUpdate: (Indisponibilidade) -> Unit,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Indisponibilidade?>(null) }
    var formData by remember { mutableStateOf(emptyForm()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Indisponibilidades — ${state.ministroNome}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showForm) {
                    IndisponibilidadeForm(
                        data = formData,
                        onChange = { formData = it },
                        onSave = {
                            if (editing != null) {
                                onUpdate(formData.copy(id = editing!!.id, ministroId = state.ministroId))
                            } else {
                                onCreate(formData.copy(ministroId = state.ministroId))
                            }
                            showForm = false
                            editing = null
                            formData = emptyForm()
                        },
                        onCancel = { showForm = false; editing = null; formData = emptyForm() },
                    )
                    HorizontalDivider()
                }

                if (state.loading) {
                    Text("Carregando...", style = MaterialTheme.typography.bodySmall)
                } else if (state.items.isEmpty()) {
                    Text("Nenhuma indisponibilidade registrada.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    state.items.forEach { ind ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(ind.data.format(DATE_FMT), style = MaterialTheme.typography.bodyMedium)
                                if (ind.horarioInicio != null) {
                                    Text("${ind.horarioInicio}–${ind.horarioFim ?: ""}", style = MaterialTheme.typography.bodySmall)
                                }
                                ind.motivo?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            Row {
                                IconButton(onClick = {
                                    editing = ind
                                    formData = ind.copy()
                                    showForm = true
                                }) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary) }
                                IconButton(onClick = { onDelete(ind.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            if (!showForm) {
                TextButton(onClick = { showForm = true; editing = null; formData = emptyForm() }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Nova")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Fechar") } },
    )
}

@Composable
private fun IndisponibilidadeForm(
    data: Indisponibilidade,
    onChange: (Indisponibilidade) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Nova indisponibilidade", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = data.data.toString(),
            onValueChange = { txt -> runCatching { LocalDate.parse(txt) }.getOrNull()?.let { onChange(data.copy(data = it)) } ?: onChange(data) },
            label = { Text("Data (AAAA-MM-DD) *") },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.horarioInicio ?: "",
                onValueChange = { onChange(data.copy(horarioInicio = it.ifBlank { null })) },
                label = { Text("Início") },
                modifier = Modifier.weight(1f),
                placeholder = { Text("HH:mm") },
            )
            OutlinedTextField(
                value = data.horarioFim ?: "",
                onValueChange = { onChange(data.copy(horarioFim = it.ifBlank { null })) },
                label = { Text("Fim") },
                modifier = Modifier.weight(1f),
                placeholder = { Text("HH:mm") },
            )
        }
        OutlinedTextField(
            value = data.motivo ?: "",
            onValueChange = { onChange(data.copy(motivo = it.ifBlank { null })) },
            label = { Text("Motivo") },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onCancel) { Text("Cancelar") }
            Button(onClick = onSave) { Text("Salvar") }
        }
    }
}

private fun emptyForm() = Indisponibilidade(
    id = 0L,
    ministroId = 0L,
    data = LocalDate.now(),
    horarioInicio = null,
    horarioFim = null,
    motivo = null,
)
