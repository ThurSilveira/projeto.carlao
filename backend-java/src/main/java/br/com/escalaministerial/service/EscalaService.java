package br.com.escalaministerial.service;

import br.com.escalaministerial.audit.AuditPublisher;
import br.com.escalaministerial.dto.request.EscalaRequest;
import br.com.escalaministerial.dto.request.EscalaUpdateRequest;
import br.com.escalaministerial.dto.response.EscalaMinistroResponse;
import br.com.escalaministerial.dto.response.EscalaResponse;
import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.model.Escala;
import br.com.escalaministerial.model.EscalaMinistro;
import br.com.escalaministerial.model.Evento;
import br.com.escalaministerial.repository.EscalaRepository;
import br.com.escalaministerial.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscalaService {

    private final EscalaRepository escalaRepository;
    private final EventoRepository eventoRepository;
    private final AuditPublisher auditPublisher;

    public EscalaService(EscalaRepository escalaRepository,
                         EventoRepository eventoRepository,
                         AuditPublisher auditPublisher) {
        this.escalaRepository = escalaRepository;
        this.eventoRepository = eventoRepository;
        this.auditPublisher = auditPublisher;
    }

    public List<EscalaResponse> listar() {
        return escalaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EscalaResponse obter(Long id) {
        return escalaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Escala", id));
    }

    @Transactional
    public EscalaResponse criar(EscalaRequest request) {
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", request.eventoId()));
        Escala escala = new Escala(evento, request.observacao());
        Escala salvo = escalaRepository.save(escala);
        auditPublisher.publicar("Escala", TipoAcao.CRIADO, null, salvo.getObservacao());
        return toResponse(salvo);
    }

    @Transactional
    public EscalaResponse atualizar(Long id, EscalaUpdateRequest request) {
        return escalaRepository.findById(id).map(existente -> {
            if (request.eventoId() != null) {
                Evento evento = eventoRepository.findById(request.eventoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Evento", request.eventoId()));
                existente.setEvento(evento);
            }
            if (request.observacao() != null) {
                existente.setObservacao(request.observacao());
            }
            if (request.status() != null) {
                existente.setStatus(request.status());
            }
            Escala salvo = escalaRepository.save(existente);
            auditPublisher.publicar("Escala", TipoAcao.ATUALIZADO, existente.getObservacao(), salvo.getObservacao());
            return toResponse(salvo);
        }).orElseThrow(() -> new ResourceNotFoundException("Escala", id));
    }

    @Transactional
    public void deletar(Long id) {
        Escala escala = escalaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escala", id));
        escalaRepository.delete(escala);
        auditPublisher.publicar("Escala", TipoAcao.DELETADO, escala.getObservacao(), null);
    }

    private EscalaResponse toResponse(Escala e) {
        List<EscalaMinistroResponse> ministros = e.getEscalaMinistros().stream()
                .map(this::toMinistroResponse)
                .toList();
        return new EscalaResponse(
                e.getId(),
                e.getEvento() != null ? e.getEvento().getId() : null,
                e.getEvento() != null ? e.getEvento().getNome() : null,
                e.getDataAtribuicao(),
                e.getObservacao(),
                e.getStatus(),
                ministros
        );
    }

    private EscalaMinistroResponse toMinistroResponse(EscalaMinistro em) {
        return new EscalaMinistroResponse(
                em.getId(),
                em.getMinistro() != null ? em.getMinistro().getId() : null,
                em.getMinistro() != null ? em.getMinistro().getNome() : null,
                em.isConfirmacaoMinistro(),
                em.getDataConfirmacao(),
                em.isSubstituido()
        );
    }
}
