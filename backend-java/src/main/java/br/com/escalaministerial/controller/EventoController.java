package br.com.escalaministerial.controller;

import br.com.escalaministerial.dto.request.EventoRequest;
import br.com.escalaministerial.dto.response.EventoResponse;
import br.com.escalaministerial.service.EventoService;
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
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody EventoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
