package br.com.escalaministerial.service;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.LogAuditoria;
import br.com.escalaministerial.repository.LogAuditoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final LogAuditoriaRepository repository;

    public AuditoriaService(LogAuditoriaRepository repository) {
        this.repository = repository;
    }

    public LogAuditoria registrar(String entidade, TipoAcao acao, String statusAnterior, String statusNovo) {
        LogAuditoria log = new LogAuditoria(entidade, acao, statusAnterior, statusNovo, null);
        return repository.save(log);
    }
}
