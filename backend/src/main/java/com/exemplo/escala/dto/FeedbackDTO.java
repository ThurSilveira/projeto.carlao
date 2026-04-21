package com.exemplo.escala.dto;

import java.time.LocalDateTime;

public class FeedbackDTO {
    private Long id;
    private Long ministroId;
    private Long eventoId;
    private Integer nota;
    private String comentario;
    private LocalDateTime dataEnvio;
    private String status;
    private String resposta;

    public FeedbackDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMinistroId() { return ministroId; }
    public void setMinistroId(Long ministroId) { this.ministroId = ministroId; }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
}
