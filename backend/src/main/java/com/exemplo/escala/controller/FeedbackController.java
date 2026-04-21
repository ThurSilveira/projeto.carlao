package com.exemplo.escala.controller;

import com.exemplo.escala.dto.FeedbackDTO;
import com.exemplo.escala.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService service;

    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> criar(@RequestBody FeedbackDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<FeedbackDTO> responder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String resposta = body.get("resposta");
        if (resposta == null || resposta.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        FeedbackDTO dto = service.responder(id, resposta);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}
