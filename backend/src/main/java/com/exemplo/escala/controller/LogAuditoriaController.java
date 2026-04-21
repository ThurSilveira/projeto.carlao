package com.exemplo.escala.controller;

import com.exemplo.escala.dto.LogAuditoriaDTO;
import com.exemplo.escala.service.LogAuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class LogAuditoriaController {

    @Autowired
    private LogAuditoriaService service;

    @GetMapping
    public ResponseEntity<List<LogAuditoriaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
