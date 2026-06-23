package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.FuncaoMinistro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ministros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ministro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String observacoes;
    private boolean ativo;
    private boolean visitasAoInfermo;
    private boolean statusCurso;
    private int escalasMes;
    private FuncaoMinistro funcao;
    private String funcaoEspecificada;

    @OneToMany(mappedBy = "ministro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Indisponibilidade> indisponibilidades = new ArrayList<>();

    @OneToMany(mappedBy = "ministro", fetch = FetchType.LAZY)
    private List<EscalaMinistro> escalaMinistros = new ArrayList<>();

    @OneToMany(mappedBy = "ministro", fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    public Ministro(String nome, String email, FuncaoMinistro funcao) {
        this.nome = nome;
        this.email = email;
        this.funcao = funcao;
        this.ativo = true;
        this.escalasMes = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ministro ministro)) return false;
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
