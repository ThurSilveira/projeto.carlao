package br.com.escalaministerial.controller;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.Escala;
import br.com.escalaministerial.repository.EscalaRepository;
import br.com.escalaministerial.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escalas")
public class EscalaController {

    private final EscalaRepository repository;
    private final AuditoriaService auditoriaService;

    public EscalaController(EscalaRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<Escala> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Escala> obter(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Escala>build());
    }

    @PostMapping
    public ResponseEntity<Escala> criar(@RequestBody Escala escala) {
        Escala salvo = repository.save(escala);
        auditoriaService.registrar("Escala", TipoAcao.CRIADO, null, salvo.getObservacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Escala> atualizar(@PathVariable Long id, @RequestBody Escala dados) {
        return repository.findById(id).map(existente -> {
            existente.setEvento(dados.getEvento());
            existente.setDataAtribuicao(dados.getDataAtribuicao());
            existente.setObservacao(dados.getObservacao());
            existente.setStatus(dados.getStatus());
            Escala salvo = repository.save(existente);
            auditoriaService.registrar("Escala", TipoAcao.ATUALIZADO, existente.getObservacao(), salvo.getObservacao());
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Escala>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            auditoriaService.registrar("Escala", TipoAcao.DELETADO, existente.getObservacao(), null);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
