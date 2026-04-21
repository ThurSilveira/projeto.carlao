package com.exemplo.escala.controller;

import com.exemplo.escala.dto.MinistroDTO;
import com.exemplo.escala.service.MinistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ministros")
public class MinistroController {

    @Autowired
    private MinistroService service;

    @GetMapping
    public ResponseEntity<List<MinistroDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MinistroDTO> obterPorId(@PathVariable Long id) {
        MinistroDTO dto = service.obterPorId(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MinistroDTO> criar(@RequestBody MinistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MinistroDTO> atualizar(@PathVariable Long id, @RequestBody MinistroDTO dto) {
        MinistroDTO atualizado = service.atualizar(id, dto);
        return atualizado != null ? ResponseEntity.ok(atualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
