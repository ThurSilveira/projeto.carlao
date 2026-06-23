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
@Table(name = "escala_ministros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EscalaMinistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id")
    @JsonIgnore
    private Escala escala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministro_id")
    private Ministro ministro;

    private boolean confirmacaoMinistro;
    private LocalDate dataConfirmacao;
    private boolean substituido;

    public EscalaMinistro(Escala escala, Ministro ministro) {
        this.escala = escala;
        this.ministro = ministro;
        this.confirmacaoMinistro = false;
        this.substituido = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EscalaMinistro that)) return false;
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
                ", ministro=" + (ministro != null ? ministro.getNome() : "null") +
                ", confirmado=" + confirmacaoMinistro +
                ", substituido=" + substituido +
                '}';
    }
}
