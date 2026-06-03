package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.StatusFeedback;
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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * POO — Classe de domínio (entidade).
 *
 * Registra a avaliação de um Ministro sobre um Evento em que participou.
 *
 * Associação: referencia Ministro e Evento sem possuí-los —
 *   Feedback é uma entidade independente que conecta os dois.
 *
 * Estado: ciclo de vida controlado por StatusFeedback
 *   (PENDENTE → RESPONDIDO | ARQUIVADO).
 *
 * Encapsulamento: a nota é um inteiro restrito ao intervalo [1, 5] pelo domínio,
 *   mas a validação é responsabilidade da camada de serviço.
 */
@Entity
@Table(name = "feedbacks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ministro que enviou o feedback. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministro_id")
    @JsonIgnore
    private Ministro ministro;

    /** Evento avaliado. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private Evento evento;

    /**
     * Nota de 1 a 5 atribuída ao evento.
     */
    private int nota;

    /** Comentário livre opcional. */
    private String comentario;

    /** Momento do envio do feedback. */
    private LocalDateTime dataEnvio;

    private StatusFeedback status;

    /** Resposta do coordenador ao feedback, preenchida quando status = RESPONDIDO. */
    private String resposta;

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public Feedback() {}

    /**
     * Construtor de criação de um feedback.
     * Status inicial é PENDENTE e dataEnvio é definida como o momento atual.
     *
     * @param ministro   ministro que avalia
     * @param evento     evento avaliado
     * @param nota       nota de 1 a 5
     * @param comentario observação livre
     */
    public Feedback(Ministro ministro, Evento evento, int nota, String comentario) {
        this.ministro = ministro;
        this.evento = evento;
        this.nota = nota;
        this.comentario = comentario;
        this.dataEnvio = LocalDateTime.now();
        this.status = StatusFeedback.PENDENTE;
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ministro getMinistro() { return ministro; }
    public void setMinistro(Ministro ministro) { this.ministro = ministro; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public StatusFeedback getStatus() { return status; }
    public void setStatus(StatusFeedback status) { this.status = status; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }

    // ── Override ──────────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(id, feedback.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", ministro=" + (ministro != null ? ministro.getNome() : "null") +
                ", evento=" + (evento != null ? evento.getNome() : "null") +
                ", nota=" + nota +
                ", status=" + status +
                '}';
    }
}
