package com.exemplo.escala.model;

import com.exemplo.escala.model.enums.FuncaoMinistro;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ministro")
public class Ministro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefone;
    private LocalDate dataNascimento;
    private String observacoes;
    private boolean ativo = true;
    private boolean visitasAoInfermo = false;
    private boolean statusCurso = false;
    private Integer escalasMes = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuncaoMinistro funcao = FuncaoMinistro.LEITURA;

    public Ministro() {}

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

    public FuncaoMinistro getFuncao() { return funcao; }
    public void setFuncao(FuncaoMinistro funcao) { this.funcao = funcao; }
}
