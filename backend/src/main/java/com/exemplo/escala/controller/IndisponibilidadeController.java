package com.exemplo.escala.controller;

import com.exemplo.escala.dto.IndisponibilidadeDTO;
import com.exemplo.escala.service.IndisponibilidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ministros/{ministroId}/indisponibilidades")
public class IndisponibilidadeController {

    @Autowired
    private IndisponibilidadeService service;

    @GetMapping
    public ResponseEntity<List<IndisponibilidadeDTO>> listar(@PathVariable Long ministroId) {
        return ResponseEntity.ok(service.listarPorMinistro(ministroId));
    }

    @PostMapping
    public ResponseEntity<IndisponibilidadeDTO> criar(
            @PathVariable Long ministroId,
            @RequestBody IndisponibilidadeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(ministroId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndisponibilidadeDTO> atualizar(
            @PathVariable Long ministroId,
            @PathVariable Long id,
            @RequestBody IndisponibilidadeDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long ministroId,
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
