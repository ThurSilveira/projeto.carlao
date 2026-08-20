package br.com.escalaministerial.controller;

import br.com.escalaministerial.dto.request.EscalaRequest;
import br.com.escalaministerial.dto.request.EscalaUpdateRequest;
import br.com.escalaministerial.dto.response.EscalaResponse;
import br.com.escalaministerial.service.EscalaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/escalas")
public class EscalaController {

    private final EscalaService service;

    public EscalaController(EscalaService service) {
        this.service = service;
    }

    @GetMapping
    public List<EscalaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscalaResponse> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PostMapping
    public ResponseEntity<EscalaResponse> criar(@Valid @RequestBody EscalaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscalaResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody EscalaUpdateRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
