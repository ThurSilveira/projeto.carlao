package br.com.escalaministerial.controller;

import br.com.escalaministerial.model.LogAuditoria;
import br.com.escalaministerial.repository.LogAuditoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final LogAuditoriaRepository repository;

    public AuditoriaController(LogAuditoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<LogAuditoria> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogAuditoria> obter(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
