package com.exemplo.escala.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EscalaDTO {
    private Long id;
    private Long eventoId;
    private EventoDTO evento;
    private LocalDate dataAtribuicao;
    private String observacao;
    private String status;
    private List<EscalaMinistroDTO> escalaMinistros = new ArrayList<>();

    public EscalaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public EventoDTO getEvento() { return evento; }
    public void setEvento(EventoDTO evento) { this.evento = evento; }

    public LocalDate getDataAtribuicao() { return dataAtribuicao; }
    public void setDataAtribuicao(LocalDate dataAtribuicao) { this.dataAtribuicao = dataAtribuicao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<EscalaMinistroDTO> getEscalaMinistros() { return escalaMinistros; }
    public void setEscalaMinistros(List<EscalaMinistroDTO> escalaMinistros) { this.escalaMinistros = escalaMinistros; }
}
