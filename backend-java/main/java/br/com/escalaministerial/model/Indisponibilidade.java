package br.com.escalaministerial.model;

import java.time.LocalDate;

/**
 * POO — Classe de domínio (entidade).
 *
 * Representa uma data/período em que um Ministro está indisponível.
 * Relacionamento: Agregação com Ministro (N Indisponibilidades → 1 Ministro).
 * Encapsulamento: todos os atributos são privados e acessados por getters/setters.
 */
public class Indisponibilidade {

    private Long id;

    /** Chave estrangeira conceitual — referência ao dono desta indisponibilidade. */
    private Ministro ministro;

    /** Data do bloqueio. */
    private LocalDate data;

    /**
     * Horário de início do bloqueio no formato "HH:MM".
     * Null indica dia inteiro indisponível.
     */
    private String horarioInicio;

    /**
     * Horário de fim do bloqueio no formato "HH:MM".
     * Null se não houver janela de tempo definida.
     */
    private String horarioFim;

    /** Descrição opcional do motivo da indisponibilidade. */
    private String motivo;

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public Indisponibilidade() {}

    /**
     * Construtor completo para criação programática de uma indisponibilidade.
     *
     * @param ministro     ministro associado
     * @param data         data do bloqueio
     * @param horarioInicio início do intervalo bloqueado (null = dia inteiro)
     * @param horarioFim   fim do intervalo bloqueado
     * @param motivo       descrição textual opcional
     */
    public Indisponibilidade(Ministro ministro, LocalDate data,
                             String horarioInicio, String horarioFim, String motivo) {
        this.ministro = ministro;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.motivo = motivo;
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ministro getMinistro() { return ministro; }
    public void setMinistro(Ministro ministro) { this.ministro = ministro; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }

    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    // ── Override ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Indisponibilidade{" +
                "id=" + id +
                ", ministro=" + (ministro != null ? ministro.getNome() : "null") +
                ", data=" + data +
                ", horarioInicio='" + horarioInicio + '\'' +
                ", horarioFim='" + horarioFim + '\'' +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
