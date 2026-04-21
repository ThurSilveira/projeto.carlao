package com.exemplo.escala.service;

import com.exemplo.escala.dto.FeedbackDTO;
import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.Feedback;
import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.model.enums.StatusFeedback;
import com.exemplo.escala.repository.EventoRepository;
import com.exemplo.escala.repository.FeedbackRepository;
import com.exemplo.escala.repository.MinistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository repository;

    @Autowired
    private MinistroRepository ministroRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private LogAuditoriaService auditoriaService;

    public FeedbackDTO criar(FeedbackDTO dto) {
        Ministro ministro = ministroRepository.findById(dto.getMinistroId())
                .orElseThrow(() -> new IllegalArgumentException("Ministro não encontrado"));
        Evento evento = eventoRepository.findById(dto.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        Feedback feedback = new Feedback();
        feedback.setMinistro(ministro);
        feedback.setEvento(evento);
        feedback.setNota(dto.getNota());
        feedback.setComentario(dto.getComentario());
        feedback.setStatus(StatusFeedback.PENDENTE);
        Feedback salvo = repository.save(feedback);
        auditoriaService.registrar("Feedback", "CRIADO", null, "PENDENTE");
        return toDTO(salvo);
    }

    public List<FeedbackDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public FeedbackDTO responder(Long id, String resposta) {
        Feedback feedback = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback não encontrado"));
        String prev = feedback.getStatus().name();
        feedback.setResposta(resposta);
        feedback.setStatus(StatusFeedback.RESPONDIDO);
        Feedback salvo = repository.save(feedback);
        auditoriaService.registrar("Feedback", "ATUALIZADO", prev, "RESPONDIDO");
        return toDTO(salvo);
    }

    public FeedbackDTO toDTO(Feedback f) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(f.getId());
        dto.setMinistroId(f.getMinistro() != null ? f.getMinistro().getId() : null);
        dto.setEventoId(f.getEvento() != null ? f.getEvento().getId() : null);
        dto.setNota(f.getNota());
        dto.setComentario(f.getComentario());
        dto.setDataEnvio(f.getDataEnvio());
        dto.setStatus(f.getStatus() != null ? f.getStatus().name() : null);
        dto.setResposta(f.getResposta());
        return dto;
    }
}
