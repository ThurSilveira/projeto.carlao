package br.com.escalaministerial.dto.request;

import br.com.escalaministerial.enums.StatusFeedback;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FeedbackUpdateRequest(
        @Min(1) @Max(5) int nota,
        String comentario,
        StatusFeedback status,
        String resposta
) {}
