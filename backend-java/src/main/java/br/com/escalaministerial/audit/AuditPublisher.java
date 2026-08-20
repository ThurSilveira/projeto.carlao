package br.com.escalaministerial.audit;

import br.com.escalaministerial.enums.TipoAcao;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AuditPublisher {

    private final ApplicationEventPublisher publisher;

    public AuditPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicar(String entidade, TipoAcao acao, String statusAnterior, String statusNovo) {
        publisher.publishEvent(new AuditEvent(entidade, acao, statusAnterior, statusNovo));
    }
}
