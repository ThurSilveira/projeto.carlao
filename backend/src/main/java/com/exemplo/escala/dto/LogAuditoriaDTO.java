package com.exemplo.escala.dto;

import java.time.LocalDateTime;

public class LogAuditoriaDTO {
    private Long id;
    private String entidade;
    private String acao;
    private String statusAnterior;
    private String statusNovo;
    private String realizadoPorId;
    private LocalDateTime dataHora;

    public LogAuditoriaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }

    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }

    public String getRealizadoPorId() { return realizadoPorId; }
    public void setRealizadoPorId(String realizadoPorId) { this.realizadoPorId = realizadoPorId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
