package com.escala.ministerial.feature.feedback.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.escala.ministerial.core.ui.components.BadgeVariant
import com.escala.ministerial.core.ui.components.ErrorScreen
import com.escala.ministerial.core.ui.components.LoadingScreen
import com.escala.ministerial.core.ui.components.StatusBadge
import com.escala.ministerial.feature.feedback.domain.model.Feedback
import com.escala.ministerial.feature.feedback.domain.model.StatusFeedback
import java.time.format.DateTimeFormatter

private val DT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(viewModel: FeedbackViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var respondingFeedback by remember { mutableStateOf<Feedback?>(null) }
    var respostaText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FeedbackEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is FeedbackEvent.Responded -> {
                    snackbarHostState.showSnackbar("Resposta enviada")
                    respondingFeedback = null
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Feedbacks") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val state = uiState) {
            is FeedbackUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is FeedbackUiState.Error -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is FeedbackUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${state.feedbacks.size}", style = MaterialTheme.typography.titleLarge)
                        Text("Total", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("%.1f".format(state.mediaNota), style = MaterialTheme.typography.titleLarge, color = Color(0xFFFF9800))
                        Text("Nota média", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${state.pendentes}", style = MaterialTheme.typography.titleLarge, color = Color(0xFFF59E0B))
                        Text("Pendentes", style = MaterialTheme.typography.bodySmall)
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.feedbacks, key = { it.id }) { feedback ->
                        FeedbackCard(
                            feedback = feedback,
                            onResponder = { respondingFeedback = it; respostaText = "" },
                        )
                    }
                }
            }
        }
    }

    respondingFeedback?.let { feedback ->
        AlertDialog(
            onDismissRequest = { respondingFeedback = null },
            title = { Text("Responder Feedback") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("De: ${feedback.ministroNome}", style = MaterialTheme.typography.bodySmall)
                    Text("Evento: ${feedback.eventoNome}", style = MaterialTheme.typography.bodySmall)
                    feedback.comentario?.let { Text("\"$it\"", style = MaterialTheme.typography.bodyMedium) }
                    OutlinedTextField(
                        value = respostaText,
                        onValueChange = { respostaText = it },
                        label = { Text("Sua resposta") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (respostaText.isNotBlank()) viewModel.responder(feedback.id, respostaText)
                }) { Text("Enviar") }
            },
            dismissButton = { TextButton(onClick = { respondingFeedback = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun FeedbackCard(feedback: Feedback, onResponder: (Feedback) -> Unit) {
    val badgeVariant = when (feedback.status) {
        StatusFeedback.PENDENTE -> BadgeVariant.WARNING
        StatusFeedback.RESPONDIDO -> BadgeVariant.SUCCESS
        StatusFeedback.ARQUIVADO -> BadgeVariant.NEUTRAL
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(feedback.ministroNome, style = MaterialTheme.typography.titleMedium)
                    Text(feedback.eventoNome, style = MaterialTheme.typography.bodySmall)
                    feedback.dataEnvio?.let { Text(it.format(DT_FORMATTER), style = MaterialTheme.typography.bodySmall) }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFF9800))
                        Text("${feedback.nota}/10", style = MaterialTheme.typography.titleMedium)
                    }
                    if (feedback.status == StatusFeedback.PENDENTE) {
                        IconButton(onClick = { onResponder(feedback) }) {
                            Icon(Icons.Default.Reply, contentDescription = "Responder")
                        }
                    }
                }
            }
            feedback.comentario?.let { Text("\"$it\"", style = MaterialTheme.typography.bodyMedium) }
            StatusBadge(text = feedback.status.label, variant = badgeVariant)
            feedback.resposta?.let {
                Text("Resposta: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
            }
        }
    }
}
