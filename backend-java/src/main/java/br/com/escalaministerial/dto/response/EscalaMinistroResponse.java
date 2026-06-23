package br.com.escalaministerial.dto.response;

import java.time.LocalDate;

public record EscalaMinistroResponse(
        Long id,
        Long ministroId,
        String ministroNome,
        boolean confirmacaoMinistro,
        LocalDate dataConfirmacao,
        boolean substituido
) {}
