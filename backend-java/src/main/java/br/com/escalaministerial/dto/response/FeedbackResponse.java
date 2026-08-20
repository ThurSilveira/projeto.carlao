package br.com.escalaministerial.dto.response;

import br.com.escalaministerial.enums.StatusFeedback;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        Long ministroId,
        String ministroNome,
        Long eventoId,
        String eventoNome,
        int nota,
        String comentario,
        LocalDateTime dataEnvio,
        StatusFeedback status,
        String resposta
) {}
