package com.exemplo.escala.service;

import com.exemplo.escala.dto.EventoDTO;
import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.enums.TipoEvento;
import com.exemplo.escala.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository repository;

    @Autowired
    private LogAuditoriaService auditoriaService;

    public EventoDTO criar(EventoDTO dto) {
        Evento evento = new Evento();
        preencherEvento(evento, dto);
        Evento salvo = repository.save(evento);
        auditoriaService.registrar("Evento", "CRIADO", null, salvo.getNome());
        return toDTO(salvo);
    }

    public List<EventoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EventoDTO obterPorId(Long id) {
        return repository.findById(id).map(this::toDTO).orElse(null);
    }

    public EventoDTO atualizar(Long id, EventoDTO dto) {
        Evento evento = repository.findById(id).orElse(null);
        if (evento == null) return null;
        String nomePrev = evento.getNome();
        preencherEvento(evento, dto);
        Evento salvo = repository.save(evento);
        auditoriaService.registrar("Evento", "ATUALIZADO", nomePrev, salvo.getNome());
        return toDTO(salvo);
    }

    public EventoDTO cancelar(Long id) {
        Evento evento = repository.findById(id).orElse(null);
        if (evento == null) return null;
        evento.setCancelado(true);
        Evento salvo = repository.save(evento);
        auditoriaService.registrar("Evento", "CANCELADO", "ATIVO", "CANCELADO");
        return toDTO(salvo);
    }

    public void deletar(Long id) {
        repository.findById(id).ifPresent(e -> {
            auditoriaService.registrar("Evento", "DELETADO", e.getNome(), null);
            repository.deleteById(id);
        });
    }

    private void preencherEvento(Evento evento, EventoDTO dto) {
        evento.setNome(dto.getNome());
        evento.setData(dto.getData());
        evento.setHorario(dto.getHorario());
        evento.setLocal(dto.getLocal());
        evento.setMaxMinistros(dto.getMaxMinistros() != null ? dto.getMaxMinistros() : 6);
        evento.setCancelado(dto.isCancelado());
        evento.setTipoEspecificado(dto.getTipoEspecificado());
        if (dto.getTipoEvento() != null) {
            try {
                evento.setTipoEvento(TipoEvento.valueOf(dto.getTipoEvento()));
            } catch (IllegalArgumentException e) {
                evento.setTipoEvento(TipoEvento.OUTRO);
            }
        }
    }

    public EventoDTO toDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setNome(e.getNome());
        dto.setData(e.getData());
        dto.setHorario(e.getHorario());
        dto.setTipoEvento(e.getTipoEvento() != null ? e.getTipoEvento().name() : null);
        dto.setTipoEspecificado(e.getTipoEspecificado());
        dto.setMaxMinistros(e.getMaxMinistros());
        dto.setLocal(e.getLocal());
        dto.setCancelado(e.isCancelado());
        return dto;
    }
}
