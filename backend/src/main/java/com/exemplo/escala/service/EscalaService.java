package com.exemplo.escala.service;

import com.exemplo.escala.dto.EscalaDTO;
import com.exemplo.escala.dto.EscalaMinistroDTO;
import com.exemplo.escala.model.Escala;
import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.enums.StatusEscala;
import com.exemplo.escala.repository.EscalaRepository;
import com.exemplo.escala.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EscalaService {

    @Autowired
    private EscalaRepository repository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private LogAuditoriaService auditoriaService;

    public EscalaDTO criar(EscalaDTO dto) {
        Evento evento = eventoRepository.findById(dto.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + dto.getEventoId()));
        Escala escala = new Escala();
        escala.setEvento(evento);
        escala.setObservacao(dto.getObservacao());
        escala.setStatus(StatusEscala.PROPOSTA);
        Escala salvo = repository.save(escala);
        auditoriaService.registrar("Escala", "CRIADO", null, "PROPOSTA");
        return toDTO(salvo);
    }

    public List<EscalaDTO> listar() {
        return repository.findAllWithDetails().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EscalaDTO obterPorId(Long id) {
        return repository.findById(id).map(this::toDTO).orElse(null);
    }

    public EscalaDTO aprovar(Long id) {
        Escala escala = repository.findById(id).orElse(null);
        if (escala == null) return null;
        String prev = escala.getStatus().name();
        escala.setStatus(StatusEscala.APROVADA);
        Escala salvo = repository.save(escala);
        auditoriaService.registrar("Escala", "APROVADO", prev, "APROVADA");
        return toDTO(salvo);
    }

    public EscalaDTO cancelar(Long id) {
        Escala escala = repository.findById(id).orElse(null);
        if (escala == null) return null;
        String prev = escala.getStatus().name();
        escala.setStatus(StatusEscala.CANCELADA);
        Escala salvo = repository.save(escala);
        auditoriaService.registrar("Escala", "CANCELADO", prev, "CANCELADA");
        return toDTO(salvo);
    }

    public void deletar(Long id) {
        repository.findById(id).ifPresent(e -> {
            auditoriaService.registrar("Escala", "DELETADO", e.getStatus().name(), null);
            repository.deleteById(id);
        });
    }

    public EscalaDTO toDTO(Escala e) {
        EscalaDTO dto = new EscalaDTO();
        dto.setId(e.getId());
        dto.setDataAtribuicao(e.getDataAtribuicao());
        dto.setObservacao(e.getObservacao());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        if (e.getEvento() != null) {
            dto.setEventoId(e.getEvento().getId());
            dto.setEvento(eventoService.toDTO(e.getEvento()));
        }
        List<EscalaMinistroDTO> emDTOs = e.getEscalaMinistros().stream().map(em -> {
            EscalaMinistroDTO emDto = new EscalaMinistroDTO();
            emDto.setId(em.getId());
            emDto.setEscalaId(e.getId());
            emDto.setMinistroId(em.getMinistro() != null ? em.getMinistro().getId() : null);
            emDto.setConfirmacaoMinistro(em.isConfirmacaoMinistro());
            emDto.setDataConfirmacao(em.getDataConfirmacao());
            emDto.setSubstituido(em.isSubstituido());
            return emDto;
        }).collect(Collectors.toList());
        dto.setEscalaMinistros(emDTOs);
        return dto;
    }
}
