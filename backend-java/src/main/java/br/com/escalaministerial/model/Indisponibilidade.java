package br.com.escalaministerial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "indisponibilidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Indisponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministro_id")
    @JsonIgnore
    private Ministro ministro;

    private LocalDate data;
    private String horarioInicio;
    private String horarioFim;
    private String motivo;

    public Indisponibilidade(Ministro ministro, LocalDate data,
                             String horarioInicio, String horarioFim, String motivo) {
        this.ministro = ministro;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.motivo = motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Indisponibilidade that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Indisponibilidade{" +
                "id=" + id +
                ", data=" + data +
                ", horarioInicio='" + horarioInicio + '\'' +
                ", horarioFim='" + horarioFim + '\'' +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
