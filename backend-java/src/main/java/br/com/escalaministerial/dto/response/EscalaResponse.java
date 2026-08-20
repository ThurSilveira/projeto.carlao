package br.com.escalaministerial.dto.response;

import br.com.escalaministerial.enums.StatusEscala;

import java.time.LocalDate;
import java.util.List;

public record EscalaResponse(
        Long id,
        Long eventoId,
        String eventoNome,
        LocalDate dataAtribuicao,
        String observacao,
        StatusEscala status,
        List<EscalaMinistroResponse> ministros
) {}
