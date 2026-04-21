package com.exemplo.escala.dto;

import java.time.LocalDate;

public class EscalaMinistroDTO {
    private Long id;
    private Long ministroId;
    private Long escalaId;
    private boolean confirmacaoMinistro;
    private LocalDate dataConfirmacao;
    private boolean substituido;

    public EscalaMinistroDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMinistroId() { return ministroId; }
    public void setMinistroId(Long ministroId) { this.ministroId = ministroId; }

    public Long getEscalaId() { return escalaId; }
    public void setEscalaId(Long escalaId) { this.escalaId = escalaId; }

    public boolean isConfirmacaoMinistro() { return confirmacaoMinistro; }
    public void setConfirmacaoMinistro(boolean confirmacaoMinistro) { this.confirmacaoMinistro = confirmacaoMinistro; }

    public LocalDate getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDate dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }

    public boolean isSubstituido() { return substituido; }
    public void setSubstituido(boolean substituido) { this.substituido = substituido; }
}
