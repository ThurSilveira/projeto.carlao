package br.com.escalaministerial.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IndisponibilidadeRequest(
        @NotNull LocalDate data,
        String horarioInicio,
        String horarioFim,
        String motivo
) {}
