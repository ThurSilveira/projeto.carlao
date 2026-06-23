package br.com.escalaministerial.service;

import br.com.escalaministerial.audit.AuditPublisher;
import br.com.escalaministerial.dto.request.EventoRequest;
import br.com.escalaministerial.dto.response.EventoResponse;
import br.com.escalaministerial.enums.TipoAcao;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.model.Evento;
import br.com.escalaministerial.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;
    private final AuditPublisher auditPublisher;

    public EventoService(EventoRepository repository, AuditPublisher auditPublisher) {
        this.repository = repository;
        this.auditPublisher = auditPublisher;
    }

    public List<EventoResponse> listar() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EventoResponse obter(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Transactional
    public EventoResponse criar(EventoRequest request) {
        Evento evento = new Evento(request.nome(), request.data(), request.horario(), request.tipoEvento());
        evento.setTipoEspecificado(request.tipoEspecificado());
        evento.setMaxMinistros(request.maxMinistros());
        evento.setLocal(request.local());
        evento.setCancelado(request.cancelado());
        Evento salvo = repository.save(evento);
        auditPublisher.publicar("Evento", TipoAcao.CRIADO, null, salvo.getNome());
        return toResponse(salvo);
    }

    @Transactional
    public EventoResponse atualizar(Long id, EventoRequest request) {
        return repository.findById(id).map(existente -> {
            String anterior = existente.getNome();
            existente.setNome(request.nome());
            existente.setData(request.data());
            existente.setHorario(request.horario());
            existente.setTipoEvento(request.tipoEvento());
            existente.setTipoEspecificado(request.tipoEspecificado());
            existente.setMaxMinistros(request.maxMinistros());
            existente.setLocal(request.local());
            existente.setCancelado(request.cancelado());
            Evento salvo = repository.save(existente);
            auditPublisher.publicar("Evento", TipoAcao.ATUALIZADO, anterior, salvo.getNome());
            return toResponse(salvo);
        }).orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Transactional
    public void deletar(Long id) {
        Evento evento = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
        repository.delete(evento);
        auditPublisher.publicar("Evento", TipoAcao.DELETADO, evento.getNome(), null);
    }

    private EventoResponse toResponse(Evento e) {
        return new EventoResponse(
                e.getId(), e.getNome(), e.getData(), e.getHorario(),
                e.getTipoEvento(), e.getTipoEspecificado(), e.getMaxMinistros(),
                e.getLocal(), e.isCancelado()
        );
    }
}
