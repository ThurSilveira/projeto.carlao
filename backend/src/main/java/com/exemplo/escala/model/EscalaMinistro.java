package com.exemplo.escala.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "escala_ministro")
public class EscalaMinistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id", nullable = false)
    private Escala escala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministro_id", nullable = false)
    private Ministro ministro;

    private boolean confirmacaoMinistro = false;
    private LocalDate dataConfirmacao;
    private boolean substituido = false;

    public EscalaMinistro() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Escala getEscala() { return escala; }
    public void setEscala(Escala escala) { this.escala = escala; }

    public Ministro getMinistro() { return ministro; }
    public void setMinistro(Ministro ministro) { this.ministro = ministro; }

    public boolean isConfirmacaoMinistro() { return confirmacaoMinistro; }
    public void setConfirmacaoMinistro(boolean confirmacaoMinistro) { this.confirmacaoMinistro = confirmacaoMinistro; }

    public LocalDate getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDate dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }

    public boolean isSubstituido() { return substituido; }
    public void setSubstituido(boolean substituido) { this.substituido = substituido; }
}
