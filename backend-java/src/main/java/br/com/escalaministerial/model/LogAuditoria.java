package br.com.escalaministerial.model;

import br.com.escalaministerial.enums.TipoAcao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidade;
    private TipoAcao acao;
    private String statusAnterior;
    private String statusNovo;
    private String realizadoPorId;
    private LocalDateTime dataHora;

    public LogAuditoria(String entidade, TipoAcao acao,
                        String statusAnterior, String statusNovo,
                        String realizadoPorId) {
        this.entidade = entidade;
        this.acao = acao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.realizadoPorId = realizadoPorId;
        this.dataHora = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogAuditoria that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LogAuditoria{" +
                "id=" + id +
                ", entidade='" + entidade + '\'' +
                ", acao=" + acao +
                ", dataHora=" + dataHora +
                '}';
    }
}
