package br.com.escalaministerial.audit;

import br.com.escalaministerial.enums.TipoAcao;

public record AuditEvent(
        String entidade,
        TipoAcao acao,
        String statusAnterior,
        String statusNovo
) {}
