package br.com.escalaministerial.service;

import br.com.escalaministerial.audit.AuditPublisher;
import br.com.escalaministerial.dto.request.MinistroRequest;
import br.com.escalaministerial.dto.response.MinistroResponse;
import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.MinistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MinistroService {

    private final MinistroRepository repository;
    private final AuditPublisher auditPublisher;

    public MinistroService(MinistroRepository repository, AuditPublisher auditPublisher) {
        this.repository = repository;
        this.auditPublisher = auditPublisher;
    }

    public List<MinistroResponse> listar() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MinistroResponse obter(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ministro", id));
    }

    @Transactional
    public MinistroResponse criar(MinistroRequest request) {
        Ministro ministro = new Ministro(request.nome(), request.email(), request.funcao());
        ministro.setTelefone(request.telefone());
        ministro.setDataNascimento(request.dataNascimento());
        ministro.setObservacoes(request.observacoes());
        ministro.setAtivo(request.ativo());
        ministro.setVisitasAoInfermo(request.visitasAoInfermo());
        ministro.setStatusCurso(request.statusCurso());
        ministro.setEscalasMes(request.escalasMes());
        ministro.setFuncaoEspecificada(request.funcaoEspecificada());
        Ministro salvo = repository.save(ministro);
        auditPublisher.publicar("Ministro", TipoAcao.CRIADO, null, salvo.getNome());
        return toResponse(salvo);
    }

    @Transactional
    public MinistroResponse atualizar(Long id, MinistroRequest request) {
        return repository.findById(id).map(existente -> {
            String anterior = existente.getNome();
            existente.setNome(request.nome());
            existente.setEmail(request.email());
            existente.setTelefone(request.telefone());
            existente.setDataNascimento(request.dataNascimento());
            existente.setObservacoes(request.observacoes());
            existente.setAtivo(request.ativo());
            existente.setVisitasAoInfermo(request.visitasAoInfermo());
            existente.setStatusCurso(request.statusCurso());
            existente.setEscalasMes(request.escalasMes());
            existente.setFuncao(request.funcao());
            existente.setFuncaoEspecificada(request.funcaoEspecificada());
            Ministro salvo = repository.save(existente);
            auditPublisher.publicar("Ministro", TipoAcao.ATUALIZADO, anterior, salvo.getNome());
            return toResponse(salvo);
        }).orElseThrow(() -> new ResourceNotFoundException("Ministro", id));
    }

    @Transactional
    public void deletar(Long id) {
        Ministro ministro = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ministro", id));
        repository.delete(ministro);
        auditPublisher.publicar("Ministro", TipoAcao.DELETADO, ministro.getNome(), null);
    }

    private MinistroResponse toResponse(Ministro m) {
        return new MinistroResponse(
                m.getId(), m.getNome(), m.getEmail(), m.getTelefone(),
                m.getDataNascimento(), m.getObservacoes(), m.isAtivo(),
                m.isVisitasAoInfermo(), m.isStatusCurso(), m.getEscalasMes(),
                m.getFuncao(), m.getFuncaoEspecificada()
        );
    }
}
