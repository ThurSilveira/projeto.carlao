package br.com.escalaministerial.dto.response;

import br.com.escalaministerial.enums.TipoAcao;

import java.time.LocalDateTime;

public record AuditoriaResponse(
        Long id,
        String entidade,
        TipoAcao acao,
        String statusAnterior,
        String statusNovo,
        String realizadoPorId,
        LocalDateTime dataHora
) {}
