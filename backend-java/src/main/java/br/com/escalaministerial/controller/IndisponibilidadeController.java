package br.com.escalaministerial.controller;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.Indisponibilidade;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.IndisponibilidadeRepository;
import br.com.escalaministerial.repository.MinistroRepository;
import br.com.escalaministerial.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ministros/{ministroId}/indisponibilidades")
public class IndisponibilidadeController {

    private final MinistroRepository ministroRepository;
    private final IndisponibilidadeRepository repository;
    private final AuditoriaService auditoriaService;

    public IndisponibilidadeController(MinistroRepository ministroRepository,
                                      IndisponibilidadeRepository repository,
                                      AuditoriaService auditoriaService) {
        this.ministroRepository = ministroRepository;
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Indisponibilidade>> listar(@PathVariable Long ministroId) {
        return ministroRepository.findById(ministroId)
                .map(ministro -> ResponseEntity.ok(ministro.getIndisponibilidades()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<List<Indisponibilidade>>build());
    }

    @PostMapping
    public ResponseEntity<Indisponibilidade> criar(@PathVariable Long ministroId,
                                                   @RequestBody Indisponibilidade item) {
        return ministroRepository.findById(ministroId).map(ministro -> {
            item.setMinistro(ministro);
            Indisponibilidade salvo = repository.save(item);
            ministro.getIndisponibilidades().add(salvo);
            auditoriaService.registrar("Indisponibilidade", TipoAcao.CRIADO, null, item.getMotivo());
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Indisponibilidade>build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Indisponibilidade> atualizar(@PathVariable Long ministroId,
                                                       @PathVariable Long id,
                                                       @RequestBody Indisponibilidade dados) {
        return repository.findById(id).map(existente -> {
            if (existente.getMinistro() == null || !existente.getMinistro().getId().equals(ministroId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<Indisponibilidade>build();
            }
            existente.setData(dados.getData());
            existente.setHorarioInicio(dados.getHorarioInicio());
            existente.setHorarioFim(dados.getHorarioFim());
            existente.setMotivo(dados.getMotivo());
            Indisponibilidade salvo = repository.save(existente);
            auditoriaService.registrar("Indisponibilidade", TipoAcao.ATUALIZADO, String.valueOf(existente.getId()), String.valueOf(salvo.getId()));
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Indisponibilidade>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long ministroId,
                                        @PathVariable Long id) {
        return repository.findById(id).map(existente -> {
            if (existente.getMinistro() == null || !existente.getMinistro().getId().equals(ministroId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<Void>build();
            }
            repository.delete(existente);
            auditoriaService.registrar("Indisponibilidade", TipoAcao.DELETADO, String.valueOf(existente.getId()), null);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
