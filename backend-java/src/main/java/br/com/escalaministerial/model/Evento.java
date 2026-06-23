package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.TipoEvento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate data;
    private String horario;
    private TipoEvento tipoEvento;
    private String tipoEspecificado;
    private int maxMinistros;
    private String local;
    private boolean cancelado;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<Escala> escalas = new ArrayList<>();

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    public Evento(String nome, LocalDate data, String horario, TipoEvento tipoEvento) {
        this.nome = nome;
        this.data = data;
        this.horario = horario;
        this.tipoEvento = tipoEvento;
        this.maxMinistros = 6;
        this.cancelado = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento evento)) return false;
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
