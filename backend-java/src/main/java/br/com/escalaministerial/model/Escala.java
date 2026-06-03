package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.StatusEscala;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * POO — Classe de domínio (entidade agregadora).
 *
 * Representa a escala de ministros atribuída a um Evento.
 * É o agregado raiz que coordena quais Ministros participam de um Evento.
 *
 * Composição: uma Escala é composta de seus EscalaMinistro —
 *   ao deletar a Escala, os vínculos são removidos junto.
 *
 * Associação: referencia Evento sem possuí-lo.
 *
 * Estado: o ciclo de vida (StatusEscala) define as transições válidas
 *   e é central para as regras de negócio (ex.: só gera escala no estado PROPOSTA).
 */
@Entity
@Table(name = "escalas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Escala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Evento ao qual esta escala está vinculada. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private Evento evento;

    /** Data em que a escala foi gerada/atribuída. */
    private LocalDate dataAtribuicao;

    /** Observação gerada automaticamente pelo sorteio ou escrita manualmente. */
    private String observacao;

    private StatusEscala status;

    // ── Composição: ministros vinculados pertencem a esta escala ──────────────
    @OneToMany(mappedBy = "escala", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EscalaMinistro> escalaMinistros = new ArrayList<>();

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public Escala() {}

    /**
     * Construtor de criação. O status inicial é sempre PROPOSTA.
     *
     * @param evento     evento para o qual a escala é gerada
     * @param observacao texto descritivo gerado pelo sorteio
     */
    public Escala(Evento evento, String observacao) {
        this.evento = evento;
        this.observacao = observacao;
        this.status = StatusEscala.PROPOSTA;
        this.dataAtribuicao = LocalDate.now();
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

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
    public void setEscalaMinistros(List<EscalaMinistro> escalaMinistros) {
        this.escalaMinistros = escalaMinistros;
    }

    // ── Override ──────────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Escala)) return false;
        Escala escala = (Escala) o;
        return Objects.equals(id, escala.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Escala{" +
                "id=" + id +
                ", evento=" + (evento != null ? evento.getNome() : "null") +
                ", dataAtribuicao=" + dataAtribuicao +
                ", status=" + status +
                ", ministros=" + escalaMinistros.size() +
                '}';
    }
}
