package com.exemplo.escala.model;

import com.exemplo.escala.model.enums.StatusEscala;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "escala")
public class Escala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    private LocalDate dataAtribuicao = LocalDate.now();
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEscala status = StatusEscala.PROPOSTA;

    @OneToMany(mappedBy = "escala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscalaMinistro> escalaMinistros = new ArrayList<>();

    public Escala() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public LocalDate getDataAtribuicao() { return dataAtribuicao; }
    public void setDataAtribuicao(LocalDate dataAtribuicao) { this.dataAtribuicao = dataAtribuicao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public StatusEscala getStatus() { return status; }
    public void setStatus(StatusEscala status) { this.status = status; }

    public List<EscalaMinistro> getEscalaMinistros() { return escalaMinistros; }
    public void setEscalaMinistros(List<EscalaMinistro> escalaMinistros) { this.escalaMinistros = escalaMinistros; }
}
