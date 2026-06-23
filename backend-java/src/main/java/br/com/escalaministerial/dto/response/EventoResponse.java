package br.com.escalaministerial.dto.response;

import br.com.escalaministerial.enums.TipoEvento;

import java.time.LocalDate;

public record EventoResponse(
        Long id,
        String nome,
        LocalDate data,
        String horario,
        TipoEvento tipoEvento,
        String tipoEspecificado,
        int maxMinistros,
        String local,
        boolean cancelado
) {}
