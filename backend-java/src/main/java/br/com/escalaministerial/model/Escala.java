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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "escalas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Escala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private Evento evento;

    private LocalDate dataAtribuicao;
    private String observacao;
    private StatusEscala status;

    @OneToMany(mappedBy = "escala", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EscalaMinistro> escalaMinistros = new ArrayList<>();

    public Escala(Evento evento, String observacao) {
        this.evento = evento;
        this.observacao = observacao;
        this.status = StatusEscala.PROPOSTA;
        this.dataAtribuicao = LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Escala escala)) return false;
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
                ", dataAtribuicao=" + dataAtribuicao +
                ", status=" + status +
                ", ministros=" + (escalaMinistros != null ? escalaMinistros.size() : 0) +
                '}';
    }
}
