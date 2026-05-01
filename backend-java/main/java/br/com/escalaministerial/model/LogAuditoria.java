package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.TipoAcao;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * POO — Classe de domínio (entidade de rastreabilidade).
 *
 * Registra cada operação de escrita realizada no sistema para fins de auditoria.
 * Não possui relacionamentos bidirecionais — é imutável após a criação (append-only).
 *
 * Imutabilidade parcial: atributos definidos no construtor não deveriam ser
 *   alterados após persistência; setters existem apenas para compatibilidade com frameworks.
 *
 * Encapsulamento: o conjunto de campos captura QUEM fez O QUÊ, em QUAL entidade,
 *   com transição de QUAL → QUAL estado.
 */
public class LogAuditoria {

    private Long id;

    /**
     * Nome da entidade de domínio afetada (ex.: "Escala", "Ministro").
     */
    private String entidade;

    private TipoAcao acao;

    /** Estado da entidade antes da operação. Null quando a ação é CRIADO. */
    private String statusAnterior;

    /** Estado da entidade após a operação. Null quando a ação é DELETADO. */
    private String statusNovo;

    /**
     * Identificador do usuário ou processo que realizou a operação.
     * Null em operações automatizadas (ex.: sorteio pelo sistema).
     */
    private String realizadoPorId;

    /** Timestamp exato da operação. */
    private LocalDateTime dataHora;

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public LogAuditoria() {}

    /**
     * Construtor de criação de um registro de auditoria.
     * dataHora é sempre definida como o momento atual.
     *
     * @param entidade        nome da entidade afetada
     * @param acao            tipo de operação realizada
     * @param statusAnterior  estado antes da operação (null se CRIADO)
     * @param statusNovo      estado após a operação (null se DELETADO)
     * @param realizadoPorId  identificador do ator (null se automatizado)
     */
    public LogAuditoria(String entidade, TipoAcao acao,
                        String statusAnterior, String statusNovo,
                        String realizadoPorId) {
        this.entidade = entidade;
        this.acao = acao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.realizadoPorId = realizadoPorId;
        this.dataHora = LocalDateTime.now();
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

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

    // ── Override ──────────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogAuditoria)) return false;
        LogAuditoria that = (LogAuditoria) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LogAuditoria{" +
                "id=" + id +
                ", entidade='" + entidade + '\'' +
                ", acao=" + acao +
                ", statusAnterior='" + statusAnterior + '\'' +
                ", statusNovo='" + statusNovo + '\'' +
                ", realizadoPorId='" + realizadoPorId + '\'' +
                ", dataHora=" + dataHora +
                '}';
    }
}
