package com.exemplo.escala.model;

import com.exemplo.escala.model.enums.TipoAcao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAcao acao;

    private String statusAnterior;
    private String statusNovo;
    private String realizadoPorId;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public LogAuditoria() {}

    public LogAuditoria(String entidade, TipoAcao acao, String statusAnterior, String statusNovo) {
        this.entidade = entidade;
        this.acao = acao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }

    public TipoAcao getAcao() { return acao; }
    public void setAcao(TipoAcao acao) { this.acao = acao; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }

    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }

    public String getRealizadoPorId() { return realizadoPorId; }
    public void setRealizadoPorId(String realizadoPorId) { this.realizadoPorId = realizadoPorId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
