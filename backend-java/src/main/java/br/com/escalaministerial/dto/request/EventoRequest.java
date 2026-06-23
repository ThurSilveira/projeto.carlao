package br.com.escalaministerial.dto.request;

import br.com.escalaministerial.enums.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventoRequest(
        @NotBlank String nome,
        @NotNull LocalDate data,
        @NotBlank String horario,
        @NotNull TipoEvento tipoEvento,
        String tipoEspecificado,
        int maxMinistros,
        String local,
        boolean cancelado
) {}
