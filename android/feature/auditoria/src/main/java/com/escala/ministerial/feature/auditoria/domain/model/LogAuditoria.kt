package com.escala.ministerial.feature.auditoria.domain.model

import java.time.LocalDateTime

data class LogAuditoria(
    val id: Long,
    val entidade: String,
    val acao: TipoAcao,
    val statusAnterior: String?,
    val statusNovo: String?,
    val realizadoPorId: String?,
    val dataHora: LocalDateTime,
)

enum class TipoAcao(val label: String) {
    CRIADO("Criado"),
    ATUALIZADO("Atualizado"),
    DELETADO("Deletado"),
    APROVADO("Aprovado"),
    CANCELADO("Cancelado"),
    SUBSTITUIDO("Substituído"),
    CONFIRMADO("Confirmado"),
    NOTIFICADO("Notificado"),
}
