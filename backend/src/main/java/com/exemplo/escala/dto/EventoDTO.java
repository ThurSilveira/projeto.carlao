package com.exemplo.escala.dto;

import java.time.LocalDate;

public class EventoDTO {
    private Long id;
    private String nome;
    private LocalDate data;
    private String horario;
    private String tipoEvento;
    private String tipoEspecificado;
    private Integer maxMinistros;
    private String local;
    private boolean cancelado;

    public EventoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getTipoEspecificado() { return tipoEspecificado; }
    public void setTipoEspecificado(String tipoEspecificado) { this.tipoEspecificado = tipoEspecificado; }

    public Integer getMaxMinistros() { return maxMinistros; }
    public void setMaxMinistros(Integer maxMinistros) { this.maxMinistros = maxMinistros; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }
}
