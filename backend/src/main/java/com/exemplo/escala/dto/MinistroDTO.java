package com.exemplo.escala.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MinistroDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String observacoes;
    private boolean ativo;
    private boolean visitasAoInfermo;
    private boolean statusCurso;
    private Integer escalasMes;
    private String funcao;
    private List<String> aptidoes = new ArrayList<>();
    private List<Object> indisponibilidades = new ArrayList<>();
    private List<Object> disponibilidadesRec = new ArrayList<>();
    private List<Object> escalasMinistro = new ArrayList<>();

    public MinistroDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isVisitasAoInfermo() { return visitasAoInfermo; }
    public void setVisitasAoInfermo(boolean visitasAoInfermo) { this.visitasAoInfermo = visitasAoInfermo; }

    public boolean isStatusCurso() { return statusCurso; }
    public void setStatusCurso(boolean statusCurso) { this.statusCurso = statusCurso; }

    public Integer getEscalasMes() { return escalasMes; }
    public void setEscalasMes(Integer escalasMes) { this.escalasMes = escalasMes; }

    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }

    public List<String> getAptidoes() { return aptidoes; }
    public void setAptidoes(List<String> aptidoes) { this.aptidoes = aptidoes; }

    public List<Object> getIndisponibilidades() { return indisponibilidades; }
    public void setIndisponibilidades(List<Object> indisponibilidades) { this.indisponibilidades = indisponibilidades; }

    public List<Object> getDisponibilidadesRec() { return disponibilidadesRec; }
    public void setDisponibilidadesRec(List<Object> disponibilidadesRec) { this.disponibilidadesRec = disponibilidadesRec; }

    public List<Object> getEscalasMinistro() { return escalasMinistro; }
    public void setEscalasMinistro(List<Object> escalasMinistro) { this.escalasMinistro = escalasMinistro; }
}
