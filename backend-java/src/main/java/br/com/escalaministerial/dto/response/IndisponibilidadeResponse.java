package br.com.escalaministerial.dto.response;

import java.time.LocalDate;

public record IndisponibilidadeResponse(
        Long id,
        LocalDate data,
        String horarioInicio,
        String horarioFim,
        String motivo
) {}
