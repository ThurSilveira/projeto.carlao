package br.com.escalaministerial.controller;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.Evento;
import br.com.escalaministerial.repository.EventoRepository;
import br.com.escalaministerial.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoRepository repository;
    private final AuditoriaService auditoriaService;

    public EventoController(EventoRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<Evento> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obter(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Evento>build());
    }

    @PostMapping
    public ResponseEntity<Evento> criar(@RequestBody Evento evento) {
        Evento salvo = repository.save(evento);
        auditoriaService.registrar("Evento", TipoAcao.CRIADO, null, salvo.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(@PathVariable Long id, @RequestBody Evento dados) {
        return repository.findById(id).map(existente -> {
            existente.setNome(dados.getNome());
            existente.setData(dados.getData());
            existente.setHorario(dados.getHorario());
            existente.setTipoEvento(dados.getTipoEvento());
            existente.setTipoEspecificado(dados.getTipoEspecificado());
            existente.setMaxMinistros(dados.getMaxMinistros());
            existente.setLocal(dados.getLocal());
            existente.setCancelado(dados.isCancelado());
            Evento salvo = repository.save(existente);
            auditoriaService.registrar("Evento", TipoAcao.ATUALIZADO, existente.getNome(), salvo.getNome());
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Evento>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            auditoriaService.registrar("Evento", TipoAcao.DELETADO, existente.getNome(), null);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
