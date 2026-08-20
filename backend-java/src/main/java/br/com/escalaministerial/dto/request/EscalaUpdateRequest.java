package br.com.escalaministerial.dto.request;

import br.com.escalaministerial.enums.StatusEscala;

public record EscalaUpdateRequest(
        Long eventoId,
        String observacao,
        StatusEscala status
) {}
