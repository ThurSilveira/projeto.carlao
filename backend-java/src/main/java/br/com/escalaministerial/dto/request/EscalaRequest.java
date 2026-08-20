package br.com.escalaministerial.dto.request;

import jakarta.validation.constraints.NotNull;

public record EscalaRequest(
        @NotNull Long eventoId,
        String observacao
) {}
