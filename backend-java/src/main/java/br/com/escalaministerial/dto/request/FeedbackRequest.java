package br.com.escalaministerial.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull Long ministroId,
        @NotNull Long eventoId,
        @Min(1) @Max(5) int nota,
        String comentario
) {}
