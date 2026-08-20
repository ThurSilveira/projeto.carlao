package br.com.escalaministerial.service;

import br.com.escalaministerial.audit.AuditPublisher;
import br.com.escalaministerial.dto.request.FeedbackRequest;
import br.com.escalaministerial.dto.request.FeedbackUpdateRequest;
import br.com.escalaministerial.dto.response.FeedbackResponse;
import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.model.Evento;
import br.com.escalaministerial.model.Feedback;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.EventoRepository;
import br.com.escalaministerial.repository.FeedbackRepository;
import br.com.escalaministerial.repository.MinistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository repository;
    private final MinistroRepository ministroRepository;
    private final EventoRepository eventoRepository;
    private final AuditPublisher auditPublisher;

    public FeedbackService(FeedbackRepository repository,
                           MinistroRepository ministroRepository,
                           EventoRepository eventoRepository,
                           AuditPublisher auditPublisher) {
        this.repository = repository;
        this.ministroRepository = ministroRepository;
        this.eventoRepository = eventoRepository;
        this.auditPublisher = auditPublisher;
    }

    public List<FeedbackResponse> listar() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FeedbackResponse obter(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", id));
    }

    @Transactional
    public FeedbackResponse criar(FeedbackRequest request) {
        Ministro ministro = ministroRepository.findById(request.ministroId())
                .orElseThrow(() -> new ResourceNotFoundException("Ministro", request.ministroId()));
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", request.eventoId()));
        Feedback feedback = new Feedback(ministro, evento, request.nota(), request.comentario());
        Feedback salvo = repository.save(feedback);
        auditPublisher.publicar("Feedback", TipoAcao.CRIADO, null, String.valueOf(salvo.getNota()));
        return toResponse(salvo);
    }

    @Transactional
    public FeedbackResponse atualizar(Long id, FeedbackUpdateRequest request) {
        return repository.findById(id).map(existente -> {
            existente.setNota(request.nota());
            existente.setComentario(request.comentario());
            existente.setStatus(request.status());
            existente.setResposta(request.resposta());
            Feedback salvo = repository.save(existente);
            auditPublisher.publicar("Feedback", TipoAcao.ATUALIZADO,
                    String.valueOf(existente.getId()), String.valueOf(salvo.getId()));
            return toResponse(salvo);
        }).orElseThrow(() -> new ResourceNotFoundException("Feedback", id));
    }

    @Transactional
    public void deletar(Long id) {
        Feedback feedback = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", id));
        repository.delete(feedback);
        auditPublisher.publicar("Feedback", TipoAcao.DELETADO, String.valueOf(feedback.getId()), null);
    }

    private FeedbackResponse toResponse(Feedback f) {
        return new FeedbackResponse(
                f.getId(),
                f.getMinistro() != null ? f.getMinistro().getId() : null,
                f.getMinistro() != null ? f.getMinistro().getNome() : null,
                f.getEvento() != null ? f.getEvento().getId() : null,
                f.getEvento() != null ? f.getEvento().getNome() : null,
                f.getNota(),
                f.getComentario(),
                f.getDataEnvio(),
                f.getStatus(),
                f.getResposta()
        );
    }
}
