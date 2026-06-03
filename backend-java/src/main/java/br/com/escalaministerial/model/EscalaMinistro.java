package br.com.escalaministerial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

/**
 * POO — Classe de associação (tabela de junção com atributos próprios).
 *
 * Representa o vínculo entre uma Escala e um Ministro, carregando
 * dados da participação: confirmação, data de confirmação e flag de substituição.
 *
 * Padrão: é uma entidade de associação — não seria necessária se o vínculo
 * fosse simples (N:N puro), mas os atributos extras justificam uma classe própria.
 *
 * Associação bidirecional:
 *   - muitos EscalaMinistro → uma Escala  (lado filho da composição)
 *   - muitos EscalaMinistro → um Ministro (associação independente)
 */
@Entity
@Table(name = "escala_ministros")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EscalaMinistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Escala à qual este vínculo pertence. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id")
    @JsonIgnore
    private Escala escala;

    /** Ministro participante da escala. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministro_id")
    private Ministro ministro;

    /**
     * Indica se o ministro confirmou sua presença.
     * Padrão: false (aguardando confirmação).
     */
    private boolean confirmacaoMinistro;

    /** Data em que o ministro confirmou, se confirmou. */
    private LocalDate dataConfirmacao;

    /**
     * Indica se este ministro foi substituído por outro durante a escala.
     */
    private boolean substituido;

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public EscalaMinistro() {}

    /**
     * Construtor de criação de um novo vínculo escala-ministro.
     * Estado inicial: não confirmado, não substituído.
     *
     * @param escala   escala a que este ministro foi atribuído
     * @param ministro ministro atribuído
     */
    public EscalaMinistro(Escala escala, Ministro ministro) {
        this.escala = escala;
        this.ministro = ministro;
        this.confirmacaoMinistro = false;
        this.substituido = false;
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Escala getEscala() { return escala; }
    public void setEscala(Escala escala) { this.escala = escala; }

    public Ministro getMinistro() { return ministro; }
    public void setMinistro(Ministro ministro) { this.ministro = ministro; }

    public boolean isConfirmacaoMinistro() { return confirmacaoMinistro; }
    public void setConfirmacaoMinistro(boolean confirmacaoMinistro) {
        this.confirmacaoMinistro = confirmacaoMinistro;
    }

    public LocalDate getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDate dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public boolean isSubstituido() { return substituido; }
    public void setSubstituido(boolean substituido) { this.substituido = substituido; }

    // ── Override ──────────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EscalaMinistro)) return false;
        EscalaMinistro that = (EscalaMinistro) o;
        return Objects.equals(escala, that.escala) && Objects.equals(ministro, that.ministro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(escala, ministro);
    }

    @Override
    public String toString() {
        return "EscalaMinistro{" +
                "id=" + id +
                ", escala=" + (escala != null ? escala.getId() : "null") +
                ", ministro=" + (ministro != null ? ministro.getNome() : "null") +
                ", confirmado=" + confirmacaoMinistro +
                ", substituido=" + substituido +
                '}';
    }
}
