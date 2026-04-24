package com.exemplo.escala.controller;

import com.exemplo.escala.dto.EscalaDTO;
import com.exemplo.escala.service.EscalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/escalas")
public class EscalaController {

    @Autowired
    private EscalaService service;

    @GetMapping
    public ResponseEntity<List<EscalaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscalaDTO> obterPorId(@PathVariable Long id) {
        EscalaDTO dto = service.obterPorId(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EscalaDTO> criar(@RequestBody EscalaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<EscalaDTO> aprovar(@PathVariable Long id) {
        EscalaDTO dto = service.aprovar(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<EscalaDTO> cancelar(@PathVariable Long id) {
        EscalaDTO dto = service.cancelar(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/gerar/{eventoId}")
    public ResponseEntity<EscalaDTO> gerar(@PathVariable Long eventoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarEscala(eventoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
