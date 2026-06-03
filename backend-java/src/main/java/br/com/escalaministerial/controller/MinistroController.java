package br.com.escalaministerial.controller;

import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.MinistroRepository;
import br.com.escalaministerial.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ministros")
public class MinistroController {

    private final MinistroRepository repository;
    private final AuditoriaService auditoriaService;

    public MinistroController(MinistroRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<Ministro> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ministro> obter(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Ministro>build());
    }

    @PostMapping
    public ResponseEntity<Ministro> criar(@RequestBody Ministro ministro) {
        Ministro salvo = repository.save(ministro);
        auditoriaService.registrar("Ministro", TipoAcao.CRIADO, null, salvo.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ministro> atualizar(@PathVariable Long id, @RequestBody Ministro dados) {
        return repository.findById(id).map(existente -> {
            String anterior = existente.getNome();
            existente.setNome(dados.getNome());
            existente.setEmail(dados.getEmail());
            existente.setTelefone(dados.getTelefone());
            existente.setDataNascimento(dados.getDataNascimento());
            existente.setObservacoes(dados.getObservacoes());
            existente.setAtivo(dados.isAtivo());
            existente.setVisitasAoInfermo(dados.isVisitasAoInfermo());
            existente.setStatusCurso(dados.isStatusCurso());
            existente.setEscalasMes(dados.getEscalasMes());
            existente.setFuncao(dados.getFuncao());
            existente.setFuncaoEspecificada(dados.getFuncaoEspecificada());
            Ministro salvo = repository.save(existente);
            auditoriaService.registrar("Ministro", TipoAcao.ATUALIZADO, anterior, salvo.getNome());
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.notFound().<Ministro>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            auditoriaService.registrar("Ministro", TipoAcao.DELETADO, existente.getNome(), null);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
