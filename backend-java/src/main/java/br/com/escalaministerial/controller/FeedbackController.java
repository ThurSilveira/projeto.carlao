package br.com.escalaministerial.controller;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.Feedback;
import br.com.escalaministerial.repository.FeedbackRepository;
import br.com.escalaministerial.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackRepository repository;
    private final AuditoriaService auditoriaService;

    public FeedbackController(FeedbackRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<Feedback> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> obter(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Feedback>build());
    }

    @PostMapping
    public ResponseEntity<Feedback> criar(@RequestBody Feedback feedback) {
        Feedback salvo = repository.save(feedback);
        auditoriaService.registrar("Feedback", TipoAcao.CRIADO, null, String.valueOf(salvo.getNota()));
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> atualizar(@PathVariable Long id, @RequestBody Feedback dados) {
        return repository.findById(id).map(existente -> {
            existente.setNota(dados.getNota());
            existente.setComentario(dados.getComentario());
            existente.setStatus(dados.getStatus());
            existente.setResposta(dados.getResposta());
            Feedback salvo = repository.save(existente);
            auditoriaService.registrar("Feedback", TipoAcao.ATUALIZADO, String.valueOf(existente.getId()), String.valueOf(salvo.getId()));
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Feedback>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            auditoriaService.registrar("Feedback", TipoAcao.DELETADO, String.valueOf(existente.getId()), null);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
