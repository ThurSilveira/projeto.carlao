package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.TipoEvento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * POO — Classe de domínio (entidade).
 *
 * Representa um evento litúrgico que pode receber uma Escala de Ministros.
 *
 * Associação: um Evento possui várias Escalas e vários Feedbacks
 *   (ciclos de vida independentes — associação, não composição).
 *
 * Encapsulamento: o flag `cancelado` é controlado internamente, impedindo
 *   a geração de escalas para eventos inválidos.
 */
public class Evento {

    private Long id;
    private String nome;
    private LocalDate data;
    private String horario;
    private TipoEvento tipoEvento;

    /**
     * Detalhamento textual quando tipoEvento == OUTRO.
     */
    private String tipoEspecificado;

    /**
     * Número máximo de ministros que podem ser escalados.
     * Padrão do domínio: 6.
     */
    private int maxMinistros;

    private String local;
    private boolean cancelado;

    // ── Associações bidirecionais ──────────────────────────────────────────────
    private List<Escala> escalas = new ArrayList<>();
    private List<Feedback> feedbacks = new ArrayList<>();

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public Evento() {}

    /**
     * Construtor com campos obrigatórios do domínio.
     *
     * @param nome       nome descritivo do evento
     * @param data       data de realização
     * @param horario    horário de início no formato "HH:MM"
     * @param tipoEvento categoria litúrgica do evento
     */
    public Evento(String nome, LocalDate data, String horario, TipoEvento tipoEvento) {
        this.nome = nome;
        this.data = data;
        this.horario = horario;
        this.tipoEvento = tipoEvento;
        this.maxMinistros = 6;
        this.cancelado = false;
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

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

    public String getTipoEspecificado() { return tipoEspecificado; }
    public void setTipoEspecificado(String tipoEspecificado) { this.tipoEspecificado = tipoEspecificado; }

    public int getMaxMinistros() { return maxMinistros; }
    public void setMaxMinistros(int maxMinistros) { this.maxMinistros = maxMinistros; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }

    public List<Escala> getEscalas() { return escalas; }
    public void setEscalas(List<Escala> escalas) { this.escalas = escalas; }

    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }

    // ── Override ──────────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", data=" + data +
                ", horario='" + horario + '\'' +
                ", tipoEvento=" + tipoEvento +
                ", maxMinistros=" + maxMinistros +
                ", cancelado=" + cancelado +
                '}';
    }
}
