package com.exemplo.escala.dto;

import java.time.LocalDate;

public class IndisponibilidadeDTO {
    private Long id;
    private Long ministroId;
    private LocalDate data;
    private String horarioInicio;
    private String horarioFim;
    private String motivo;

    public IndisponibilidadeDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMinistroId() { return ministroId; }
    public void setMinistroId(Long ministroId) { this.ministroId = ministroId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }

    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
