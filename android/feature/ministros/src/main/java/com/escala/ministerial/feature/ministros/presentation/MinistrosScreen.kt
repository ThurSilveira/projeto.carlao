package com.escala.ministerial.feature.ministros.presentation

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.Avatar
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
                is MinistroEvent.Saved       -> { snackbarHostState.showSnackbar("Ministro salvo"); showForm = false; editingMinistro = null }
                is MinistroEvent.Deleted     -> snackbarHostState.showSnackbar("Ministro removido")
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = true; editingMinistro = null },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) { Icon(Icons.Default.Add, "Novo Ministro") }
        },
    ) { padding ->
        when (val state = uiState) {
            is MinistrosUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is MinistrosUiState.Error   -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is MinistrosUiState.Success -> MinistrosContent(
                state = state,
                onSearch = viewModel::search,
                onEdit = { editingMinistro = it; showForm = true },
                onDelete = { deletingId = it.id },
                onIndisponibilidades = viewModel::openIndisponibilidades,
                modifier = Modifier.padding(padding),
            )
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
            title = { Text("Remover Ministro") },
            text = { Text("Deseja remover este ministro? Esta ação não pode ser desfeita.") },
            confirmButton = { TextButton(onClick = { viewModel.delete(id); deletingId = null }) { Text("Remover", color = MaterialTheme.colorScheme.error) } },
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

@Composable
private fun MinistrosContent(
    state: MinistrosUiState.Success,
    onSearch: (String) -> Unit,
    onEdit: (Ministro) -> Unit,
    onDelete: (Ministro) -> Unit,
    onIndisponibilidades: (Ministro) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Ministros", style = MaterialTheme.typography.headlineSmall)
                Text("${state.filteredMinistros.size} ministro(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        OutlinedTextField(
            value = state.query,
            onValueChange = onSearch,
            placeholder = { Text("Buscar por nome ou função…") },
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
            items(state.filteredMinistros, key = { it.id }) { ministro ->
                MinistroCard(ministro, onEdit, onDelete, onIndisponibilidades)
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun MinistroCard(
    ministro: Ministro,
    onEdit: (Ministro) -> Unit,
    onDelete: (Ministro) -> Unit,
    onIndisponibilidades: (Ministro) -> Unit,
) {
    val funcaoLabel = if (ministro.funcao == FuncaoMinistro.OUTRO && !ministro.funcaoEspecificada.isNullOrBlank())
        ministro.funcaoEspecificada!! else ministro.funcao.label

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(
            Modifier.padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(
                nome = ministro.nome, size = 44.dp,
                color = if (ministro.ativo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(ministro.nome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    if (!ministro.ativo) StatusBadge("Inativo", BadgeVariant.NEUTRAL)
                }
                Text(funcaoLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (!ministro.telefone.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(ministro.telefone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Row {
                SmallIconBtn(Icons.Default.CalendarMonth, MaterialTheme.colorScheme.secondary, "Indisponibilidades") { onIndisponibilidades(ministro) }
                SmallIconBtn(Icons.Default.Edit, MaterialTheme.colorScheme.onSurfaceVariant, "Editar") { onEdit(ministro) }
                SmallIconBtn(Icons.Default.Delete, MaterialTheme.colorScheme.error, "Remover") { onDelete(ministro) }
            }
        }
    }
}

@Composable
private fun SmallIconBtn(icon: ImageVector, tint: Color, desc: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(34.dp)) {
        Icon(icon, desc, tint = tint, modifier = Modifier.size(16.dp))
    }
}

// ── Indisponibilidade dialog ───────────────────────────────────────────────────

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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showForm) {
                    IndisponibilidadeForm(
                        data = formData, onChange = { formData = it },
                        onSave = {
                            if (editing != null) onUpdate(formData.copy(id = editing!!.id, ministroId = state.ministroId))
                            else onCreate(formData.copy(ministroId = state.ministroId))
                            showForm = false; editing = null; formData = emptyForm()
                        },
                        onCancel = { showForm = false; editing = null; formData = emptyForm() },
                    )
                }
                if (state.loading) {
                    Text("Carregando…", style = MaterialTheme.typography.bodySmall)
                } else if (state.items.isEmpty() && !showForm) {
                    Text("Nenhuma indisponibilidade registrada.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    state.items.forEach { ind ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(ind.data.format(DATE_FMT), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                                ind.motivo?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
                            }
                            Row {
                                IconButton(onClick = { editing = ind; formData = ind.copy(); showForm = true }, Modifier.size(32.dp)) { Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp)) }
                                IconButton(onClick = { onDelete(ind.id) }, Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Remover", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp)) }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showForm) TextButton(onClick = { showForm = true; editing = null; formData = emptyForm() }) { Icon(Icons.Default.Add, null); Text("Nova") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Fechar") } },
    )
}

@Composable
private fun IndisponibilidadeForm(data: Indisponibilidade, onChange: (Indisponibilidade) -> Unit, onSave: () -> Unit, onCancel: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Registrar indisponibilidade", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = data.data.toString(),
            onValueChange = { runCatching { LocalDate.parse(it) }.getOrNull()?.let { d -> onChange(data.copy(data = d)) } },
            label = { Text("Data (AAAA-MM-DD) *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            value = data.motivo ?: "",
            onValueChange = { onChange(data.copy(motivo = it.ifBlank { null })) },
            label = { Text("Motivo (opcional)") },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onCancel) { Text("Cancelar") }
            TextButton(onClick = onSave) { Text("Registrar") }
        }
    }
}

private fun emptyForm() = Indisponibilidade(0L, 0L, LocalDate.now(), null, null, null)
