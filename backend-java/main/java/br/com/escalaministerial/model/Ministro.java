package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.FuncaoMinistro;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * POO — Classe de domínio central (entidade raiz do agregado Ministro).
 *
 * Encapsulamento: atributos privados protegem o estado interno; somente
 *   getters/setters públicos permitem acesso controlado.
 *
 * Composição: um Ministro é composto de sua lista de Indisponibilidades —
 *   elas não existem sem ele (ciclo de vida dependente).
 *
 * Associação: referencia EscalaMinistro e Feedback sem possuí-los
 *   (ciclo de vida independente).
 */
public class Ministro {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String observacoes;
    private boolean ativo;
    private boolean visitasAoInfermo;
    private boolean statusCurso;

    /**
     * Contador de escalas atribuídas no mês corrente.
     * Usado pelo algoritmo de sorteio para garantir equilíbrio.
     */
    private int escalasMes;

    private FuncaoMinistro funcao;

    /**
     * Detalhamento textual quando funcao == OUTRO.
     */
    private String funcaoEspecificada;

    // ── Composição: Indisponibilidades pertencem ao Ministro ──────────────────
    private List<Indisponibilidade> indisponibilidades = new ArrayList<>();

    // ── Associação bidirecional: participações em escalas ─────────────────────
    private List<EscalaMinistro> escalaMinistros = new ArrayList<>();

    // ── Associação bidirecional: feedbacks enviados ───────────────────────────
    private List<Feedback> feedbacks = new ArrayList<>();

    // ── Construtores ──────────────────────────────────────────────────────────

    /** Construtor padrão exigido pelo framework. */
    public Ministro() {}

    /**
     * Construtor de criação com os campos obrigatórios.
     *
     * @param nome   nome completo do ministro
     * @param email  e-mail único de contato
     * @param funcao função litúrgica principal
     */
    public Ministro(String nome, String email, FuncaoMinistro funcao) {
        this.nome = nome;
        this.email = email;
        this.funcao = funcao;
        this.ativo = true;
        this.escalasMes = 0;
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isVisitasAoInfermo() { return visitasAoInfermo; }
    public void setVisitasAoInfermo(boolean visitasAoInfermo) { this.visitasAoInfermo = visitasAoInfermo; }

    public boolean isStatusCurso() { return statusCurso; }
    public void setStatusCurso(boolean statusCurso) { this.statusCurso = statusCurso; }

    public int getEscalasMes() { return escalasMes; }
    public void setEscalasMes(int escalasMes) { this.escalasMes = escalasMes; }

    public FuncaoMinistro getFuncao() { return funcao; }
    public void setFuncao(FuncaoMinistro funcao) { this.funcao = funcao; }

    public String getFuncaoEspecificada() { return funcaoEspecificada; }
    public void setFuncaoEspecificada(String funcaoEspecificada) { this.funcaoEspecificada = funcaoEspecificada; }

    public List<Indisponibilidade> getIndisponibilidades() { return indisponibilidades; }
    public void setIndisponibilidades(List<Indisponibilidade> indisponibilidades) {
        this.indisponibilidades = indisponibilidades;
    }

    public List<EscalaMinistro> getEscalaMinistros() { return escalaMinistros; }
    public void setEscalaMinistros(List<EscalaMinistro> escalaMinistros) {
        this.escalaMinistros = escalaMinistros;
    }

    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }

    // ── Override ──────────────────────────────────────────────────────────────

    /**
     * Igualdade baseada no e-mail, que é único no domínio.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ministro)) return false;
        Ministro ministro = (Ministro) o;
        return Objects.equals(email, ministro.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Ministro{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", funcao=" + funcao +
                ", ativo=" + ativo +
                ", escalasMes=" + escalasMes +
                '}';
    }
}
