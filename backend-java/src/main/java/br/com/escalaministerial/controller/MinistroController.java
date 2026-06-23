package br.com.escalaministerial.controller;

import br.com.escalaministerial.dto.request.MinistroRequest;
import br.com.escalaministerial.dto.response.MinistroResponse;
import br.com.escalaministerial.service.MinistroService;
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
@RequestMapping("/api/ministros")
public class MinistroController {

    private final MinistroService service;

    public MinistroController(MinistroService service) {
        this.service = service;
    }

    @GetMapping
    public List<MinistroResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MinistroResponse> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PostMapping
    public ResponseEntity<MinistroResponse> criar(@Valid @RequestBody MinistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MinistroResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody MinistroRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
