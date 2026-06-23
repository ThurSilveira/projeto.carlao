package br.com.escalaministerial.controller;

import br.com.escalaministerial.dto.request.IndisponibilidadeRequest;
import br.com.escalaministerial.dto.response.IndisponibilidadeResponse;
import br.com.escalaministerial.service.IndisponibilidadeService;
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
@RequestMapping("/api/ministros/{ministroId}/indisponibilidades")
public class IndisponibilidadeController {

    private final IndisponibilidadeService service;

    public IndisponibilidadeController(IndisponibilidadeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<IndisponibilidadeResponse>> listar(@PathVariable Long ministroId) {
        return ResponseEntity.ok(service.listarPorMinistro(ministroId));
    }

    @PostMapping
    public ResponseEntity<IndisponibilidadeResponse> criar(@PathVariable Long ministroId,
                                                            @Valid @RequestBody IndisponibilidadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(ministroId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndisponibilidadeResponse> atualizar(@PathVariable Long ministroId,
                                                                @PathVariable Long id,
                                                                @Valid @RequestBody IndisponibilidadeRequest request) {
        return ResponseEntity.ok(service.atualizar(ministroId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long ministroId, @PathVariable Long id) {
        service.deletar(ministroId, id);
        return ResponseEntity.noContent().build();
    }
}
