package com.escala.ministerial.feature.ministros.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinistroFormDialog(
    ministro: Ministro?,
    onDismiss: () -> Unit,
    onSave: (Ministro) -> Unit,
) {
    var nome by remember { mutableStateOf(ministro?.nome ?: "") }
    var email by remember { mutableStateOf(ministro?.email ?: "") }
    var telefone by remember { mutableStateOf(ministro?.telefone ?: "") }
    var dataNascimento by remember { mutableStateOf(ministro?.dataNascimento?.toString() ?: "") }
    var observacoes by remember { mutableStateOf(ministro?.observacoes ?: "") }
    var ativo by remember { mutableStateOf(ministro?.ativo ?: true) }
    var funcao by remember { mutableStateOf(ministro?.funcao ?: FuncaoMinistro.LEITURA) }
    var funcaoEspecificada by remember { mutableStateOf(ministro?.funcaoEspecificada ?: "") }
    var visitasAoInfermo by remember { mutableStateOf(ministro?.visitasAoInfermo ?: false) }
    var statusCurso by remember { mutableStateOf(ministro?.statusCurso ?: false) }
    var funcaoMenuExpanded by remember { mutableStateOf(false) }
    var nomeError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (ministro == null) "Novo Ministro" else "Editar Ministro") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it; nomeError = "" },
                    label = { Text("Nome *") },
                    isError = nomeError.isNotBlank(),
                    supportingText = { if (nomeError.isNotBlank()) Text(nomeError) },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = { Text("E-mail *") },
                    isError = emailError.isNotBlank(),
                    supportingText = { if (emailError.isNotBlank()) Text(emailError) },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = { dataNascimento = it },
                    label = { Text("Data de Nascimento (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = observacoes,
                    onValueChange = { observacoes = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
                ExposedDropdownMenuBox(
                    expanded = funcaoMenuExpanded,
                    onExpandedChange = { funcaoMenuExpanded = it },
                ) {
                    OutlinedTextField(
                        value = funcao.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Função") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = funcaoMenuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = funcaoMenuExpanded, onDismissRequest = { funcaoMenuExpanded = false }) {
                        FuncaoMinistro.entries.forEach { f ->
                            DropdownMenuItem(
                                text = { Text(f.label) },
                                onClick = { funcao = f; funcaoMenuExpanded = false },
                            )
                        }
                    }
                }
                if (funcao == FuncaoMinistro.OUTRO) {
                    OutlinedTextField(
                        value = funcaoEspecificada,
                        onValueChange = { funcaoEspecificada = it },
                        label = { Text("Especifique a função") },
                        placeholder = { Text("Ex: Proclamador, Coroinha...") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = ativo, onCheckedChange = { ativo = it })
                    Text("Ativo")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = visitasAoInfermo, onCheckedChange = { visitasAoInfermo = it })
                    Text("Visitas ao Infermo")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = statusCurso, onCheckedChange = { statusCurso = it })
                    Text("Curso concluído")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                nomeError = if (nome.length < 3) "Nome deve ter pelo menos 3 caracteres" else ""
                emailError = if (!email.contains("@")) "E-mail inválido" else ""
                if (nomeError.isBlank() && emailError.isBlank()) {
                    onSave(
                        Ministro(
                            id = ministro?.id ?: 0L,
                            nome = nome.trim(),
                            email = email.trim(),
                            telefone = telefone.ifBlank { null },
                            dataNascimento = dataNascimento.ifBlank { null }?.let {
                                runCatching { LocalDate.parse(it) }.getOrNull()
                            },
                            observacoes = observacoes.ifBlank { null },
                            ativo = ativo,
                            visitasAoInfermo = visitasAoInfermo,
                            statusCurso = statusCurso,
                            escalasMes = ministro?.escalasMes ?: 0,
                            funcao = funcao,
                            funcaoEspecificada = funcaoEspecificada.ifBlank { null },
                        )
                    )
                }
            }) {
                Text("Salvar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
