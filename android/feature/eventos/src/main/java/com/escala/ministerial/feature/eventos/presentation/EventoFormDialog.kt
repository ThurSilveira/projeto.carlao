package com.escala.ministerial.feature.eventos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.escala.ministerial.feature.eventos.domain.model.Evento
import com.escala.ministerial.feature.eventos.domain.model.TipoEvento
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoFormDialog(
    evento: Evento?,
    onDismiss: () -> Unit,
    onSave: (Evento) -> Unit,
) {
    var nome by remember { mutableStateOf(evento?.nome ?: "") }
    var data by remember { mutableStateOf(evento?.data?.toString() ?: "") }
    var horario by remember { mutableStateOf(evento?.horario ?: "") }
    var local by remember { mutableStateOf(evento?.local ?: "") }
    var maxMinistros by remember { mutableStateOf(evento?.maxMinistros?.toString() ?: "6") }
    var tipo by remember { mutableStateOf(evento?.tipoEvento ?: TipoEvento.MISSA_PAROQUIAL) }
    var tipoEspecificado by remember { mutableStateOf(evento?.tipoEspecificado ?: "") }
    var tipoExpanded by remember { mutableStateOf(false) }
    var nomeError by remember { mutableStateOf("") }
    var dataError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (evento == null) "Novo Evento" else "Editar Evento") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = nome, onValueChange = { nome = it; nomeError = "" },
                    label = { Text("Nome *") }, isError = nomeError.isNotBlank(),
                    supportingText = { if (nomeError.isNotBlank()) Text(nomeError) },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = data, onValueChange = { data = it; dataError = "" },
                    label = { Text("Data * (AAAA-MM-DD)") }, isError = dataError.isNotBlank(),
                    supportingText = { if (dataError.isNotBlank()) Text(dataError) },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = horario, onValueChange = { horario = it },
                    label = { Text("Horário * (HH:mm)") }, modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = local, onValueChange = { local = it },
                    label = { Text("Local") }, modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = maxMinistros, onValueChange = { maxMinistros = it },
                    label = { Text("Máx. Ministros") }, modifier = Modifier.fillMaxWidth(),
                )
                ExposedDropdownMenuBox(expanded = tipoExpanded, onExpandedChange = { tipoExpanded = it }) {
                    OutlinedTextField(
                        value = tipo.label, onValueChange = {}, readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false }) {
                        TipoEvento.entries.forEach { t ->
                            DropdownMenuItem(text = { Text(t.label) }, onClick = { tipo = t; tipoExpanded = false })
                        }
                    }
                }
                if (tipo == TipoEvento.OUTRO) {
                    OutlinedTextField(
                        value = tipoEspecificado,
                        onValueChange = { tipoEspecificado = it },
                        label = { Text("Especifique o tipo") },
                        placeholder = { Text("Ex: Celebração de aniversário...") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                nomeError = if (nome.isBlank()) "Nome é obrigatório" else ""
                val parsedDate = runCatching { LocalDate.parse(data) }.getOrNull()
                dataError = if (parsedDate == null) "Data inválida (use AAAA-MM-DD)" else ""
                if (nomeError.isBlank() && dataError.isBlank() && parsedDate != null && horario.isNotBlank()) {
                    onSave(Evento(
                        id = evento?.id ?: 0L,
                        nome = nome.trim(),
                        data = parsedDate,
                        horario = horario.trim(),
                        tipoEvento = tipo,
                        tipoEspecificado = tipoEspecificado.ifBlank { null },
                        maxMinistros = maxMinistros.toIntOrNull() ?: 6,
                        local = local.ifBlank { null },
                        cancelado = evento?.cancelado ?: false,
                    ))
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
