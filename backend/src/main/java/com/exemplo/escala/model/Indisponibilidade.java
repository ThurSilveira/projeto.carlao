package com.exemplo.escala.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "indisponibilidade")
public class Indisponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministro_id", nullable = false)
    private Ministro ministro;

    @Column(nullable = false)
    private LocalDate data;

    private String horarioInicio;
    private String horarioFim;
    private String motivo;

    public Indisponibilidade() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ministro getMinistro() { return ministro; }
    public void setMinistro(Ministro ministro) { this.ministro = ministro; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }

    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
