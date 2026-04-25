package com.exemplo.escala.model;

import com.exemplo.escala.model.enums.TipoEvento;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private String horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento = TipoEvento.MISSA_PAROQUIAL;

    private Integer maxMinistros = 6;
    private String local;
    private boolean cancelado = false;
    private String tipoEspecificado;

    public Evento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }

    public Integer getMaxMinistros() { return maxMinistros; }
    public void setMaxMinistros(Integer maxMinistros) { this.maxMinistros = maxMinistros; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }

    public String getTipoEspecificado() { return tipoEspecificado; }
    public void setTipoEspecificado(String tipoEspecificado) { this.tipoEspecificado = tipoEspecificado; }
}
