package br.com.escalaministerial.audit;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.LogAuditoria;
import br.com.escalaministerial.repository.LogAuditoriaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuditListener {

    private final LogAuditoriaRepository repository;

    public AuditListener(LogAuditoriaRepository repository) {
        this.repository = repository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditEvent(AuditEvent event) {
        LogAuditoria log = new LogAuditoria(
                event.entidade(),
                event.acao(),
                event.statusAnterior(),
                event.statusNovo(),
                null
        );
        repository.save(log);
    }
}
