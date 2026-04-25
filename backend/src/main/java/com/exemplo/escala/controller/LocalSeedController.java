package com.exemplo.escala.controller;

import com.exemplo.escala.config.LocalDataSeeder;
import com.exemplo.escala.repository.EventoRepository;
import com.exemplo.escala.repository.MinistroRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/seed")
@Profile("local")
public class LocalSeedController {

    private final LocalDataSeeder seeder;
    private final MinistroRepository ministroRepository;
    private final EventoRepository eventoRepository;

    public LocalSeedController(LocalDataSeeder seeder,
                                MinistroRepository ministroRepository,
                                EventoRepository eventoRepository) {
        this.seeder = seeder;
        this.ministroRepository = ministroRepository;
        this.eventoRepository = eventoRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addRandom() {
        seeder.addRandom(10);
        return ResponseEntity.ok(Map.of(
            "ministros", ministroRepository.count(),
            "eventos", eventoRepository.count(),
            "mensagem", "+10 ministros e +10 eventos aleatórios adicionados!"
        ));
    }
}
