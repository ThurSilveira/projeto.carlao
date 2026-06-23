package br.com.escalaministerial.service;

import br.com.escalaministerial.audit.AuditPublisher;
import br.com.escalaministerial.dto.request.IndisponibilidadeRequest;
import br.com.escalaministerial.dto.response.IndisponibilidadeResponse;
import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.model.Indisponibilidade;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.IndisponibilidadeRepository;
import br.com.escalaministerial.repository.MinistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndisponibilidadeService {

    private final IndisponibilidadeRepository repository;
    private final MinistroRepository ministroRepository;
    private final AuditPublisher auditPublisher;

    public IndisponibilidadeService(IndisponibilidadeRepository repository,
                                    MinistroRepository ministroRepository,
                                    AuditPublisher auditPublisher) {
        this.repository = repository;
        this.ministroRepository = ministroRepository;
        this.auditPublisher = auditPublisher;
    }

    public List<IndisponibilidadeResponse> listarPorMinistro(Long ministroId) {
        Ministro ministro = ministroRepository.findById(ministroId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministro", ministroId));
        return ministro.getIndisponibilidades().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public IndisponibilidadeResponse criar(Long ministroId, IndisponibilidadeRequest request) {
        Ministro ministro = ministroRepository.findById(ministroId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministro", ministroId));
        Indisponibilidade item = new Indisponibilidade(ministro, request.data(),
                request.horarioInicio(), request.horarioFim(), request.motivo());
        Indisponibilidade salvo = repository.save(item);
        ministro.getIndisponibilidades().add(salvo);
        auditPublisher.publicar("Indisponibilidade", TipoAcao.CRIADO, null, item.getMotivo());
        return toResponse(salvo);
    }

    @Transactional
    public IndisponibilidadeResponse atualizar(Long ministroId, Long id, IndisponibilidadeRequest request) {
        Indisponibilidade existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Indisponibilidade", id));
        if (existente.getMinistro() == null || !existente.getMinistro().getId().equals(ministroId)) {
            throw new IllegalArgumentException("Indisponibilidade não pertence ao ministro informado");
        }
        existente.setData(request.data());
        existente.setHorarioInicio(request.horarioInicio());
        existente.setHorarioFim(request.horarioFim());
        existente.setMotivo(request.motivo());
        Indisponibilidade salvo = repository.save(existente);
        auditPublisher.publicar("Indisponibilidade", TipoAcao.ATUALIZADO,
                String.valueOf(existente.getId()), String.valueOf(salvo.getId()));
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long ministroId, Long id) {
        Indisponibilidade existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Indisponibilidade", id));
        if (existente.getMinistro() == null || !existente.getMinistro().getId().equals(ministroId)) {
            throw new IllegalArgumentException("Indisponibilidade não pertence ao ministro informado");
        }
        repository.delete(existente);
        auditPublisher.publicar("Indisponibilidade", TipoAcao.DELETADO,
                String.valueOf(existente.getId()), null);
    }

    private IndisponibilidadeResponse toResponse(Indisponibilidade i) {
        return new IndisponibilidadeResponse(
                i.getId(), i.getData(), i.getHorarioInicio(),
                i.getHorarioFim(), i.getMotivo()
        );
    }
}
