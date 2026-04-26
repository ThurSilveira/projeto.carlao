package com.escala.ministerial.feature.feedback.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontStyle
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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val state = uiState) {
            is FeedbackUiState.Loading -> LoadingScreen(Modifier.padding(padding))
            is FeedbackUiState.Error   -> ErrorScreen(state.message, viewModel::refresh, Modifier.padding(padding))
            is FeedbackUiState.Success -> Column(Modifier.padding(padding).fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Feedbacks", style = MaterialTheme.typography.headlineSmall)
                        Text("${state.feedbacks.size} feedback(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "%.1f".format(state.mediaNota),
                        label = "Nota Média",
                        valueColor = MaterialTheme.colorScheme.tertiary,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${state.pendentes}",
                        label = "Pendentes",
                        valueColor = if (state.pendentes > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.feedbacks, key = { it.id }) { feedback ->
                        FeedbackCard(feedback = feedback, onResponder = { respondingFeedback = it; respostaText = "" })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
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
                    feedback.comentario?.let {
                        Text(
                            "\"$it\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
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
                TextButton(onClick = { if (respostaText.isNotBlank()) viewModel.responder(feedback.id, respostaText) }) { Text("Enviar") }
            },
            dismissButton = { TextButton(onClick = { respondingFeedback = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, value: String, label: String, valueColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = valueColor)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FeedbackCard(feedback: Feedback, onResponder: (Feedback) -> Unit) {
    val notaColor: Color = when {
        feedback.nota >= 8 -> MaterialTheme.colorScheme.secondary
        feedback.nota >= 6 -> MaterialTheme.colorScheme.tertiary
        else               -> MaterialTheme.colorScheme.error
    }
    val badgeVariant = when (feedback.status) {
        StatusFeedback.PENDENTE   -> BadgeVariant.WARNING
        StatusFeedback.RESPONDIDO -> BadgeVariant.SUCCESS
        StatusFeedback.ARQUIVADO  -> BadgeVariant.NEUTRAL
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(feedback.ministroNome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(feedback.eventoNome, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    feedback.dataEnvio?.let {
                        Text(it.format(DT_FORMATTER), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Star, null, tint = notaColor, modifier = Modifier.size(18.dp))
                    Text("${feedback.nota}/10", style = MaterialTheme.typography.titleMedium, color = notaColor)
                }
            }

            feedback.comentario?.let {
                Text(
                    "\"$it\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(text = feedback.status.label, variant = badgeVariant)
                if (feedback.status == StatusFeedback.PENDENTE) {
                    TextButton(
                        onClick = { onResponder(feedback) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Icon(Icons.Default.Reply, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Responder", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            feedback.resposta?.let { resposta ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)),
                ) {
                    Box(
                        Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.secondary),
                    )
                    Column(
                        Modifier.padding(start = 10.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text("Resposta:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        Text(resposta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
