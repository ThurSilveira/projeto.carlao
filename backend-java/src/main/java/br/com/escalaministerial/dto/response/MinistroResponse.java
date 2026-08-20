package br.com.escalaministerial.dto.response;

import br.com.escalaministerial.enums.FuncaoMinistro;

import java.time.LocalDate;

public record MinistroResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String observacoes,
        boolean ativo,
        boolean visitasAoInfermo,
        boolean statusCurso,
        int escalasMes,
        FuncaoMinistro funcao,
        String funcaoEspecificada
) {}
