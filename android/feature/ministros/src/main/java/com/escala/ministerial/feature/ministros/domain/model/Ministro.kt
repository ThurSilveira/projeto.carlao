package com.escala.ministerial.feature.ministros.domain.model

import java.time.LocalDate

data class Ministro(
    val id: Long,
    val nome: String,
    val email: String,
    val telefone: String?,
    val dataNascimento: LocalDate?,
    val observacoes: String?,
    val ativo: Boolean,
    val visitasAoInfermo: Boolean,
    val statusCurso: Boolean,
    val escalasMes: Int,
    val funcao: FuncaoMinistro,
)

enum class FuncaoMinistro(val label: String) {
    EUCARISTIA("Eucaristia"),
    LEITURA("Leitura"),
    ACOLHIMENTO("Acolhimento"),
    MUSICA("Música"),
    CATEQUESE("Catequese"),
    ADORACAO("Adoração"),
    OUTRO("Outro"),
}
