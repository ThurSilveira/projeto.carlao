package br.com.escalaministerial.dto.request;

import br.com.escalaministerial.enums.FuncaoMinistro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MinistroRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        String telefone,
        LocalDate dataNascimento,
        String observacoes,
        boolean ativo,
        boolean visitasAoInfermo,
        boolean statusCurso,
        int escalasMes,
        @NotNull FuncaoMinistro funcao,
        String funcaoEspecificada
) {}
