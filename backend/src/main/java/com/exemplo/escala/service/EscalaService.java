package com.exemplo.escala.service;

import com.exemplo.escala.dto.EscalaDTO;
import com.exemplo.escala.dto.EscalaMinistroDTO;
import com.exemplo.escala.model.Escala;
import com.exemplo.escala.model.EscalaMinistro;
import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.model.enums.StatusEscala;
import com.exemplo.escala.repository.EscalaRepository;
import com.exemplo.escala.repository.EventoRepository;
import com.exemplo.escala.repository.MinistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EscalaService {

    @Autowired
    private EscalaRepository repository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private MinistroRepository ministroRepository;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private LogAuditoriaService auditoriaService;

    // -------------------------------------------------------------------------
    // Algoritmo de sorteio
    //
    // Garante distribuição justa: ministros com menos escalas no mês são
    // selecionados primeiro. Dentro do mesmo patamar, a ordem é aleatória.
    // Invariante: max(escalasMes) - min(escalasMes) ≤ 1 após cada rodada.
    // -------------------------------------------------------------------------

    /** Package-private para ser testável diretamente em EscalaServiceTest. */
    List<Ministro> selecionarMinistros(List<Ministro> candidatos, int vagas) {
        if (candidatos.isEmpty()) return Collections.emptyList();

        List<Ministro> pool = new ArrayList<>(candidatos);

        // Embaralha primeiro para quebrar empates de forma aleatória
        Collections.shuffle(pool);

        // Ordena por escalasMes ascendente (menos escalas = maior prioridade)
        pool.sort(Comparator.comparingInt(m -> (m.getEscalasMes() != null ? m.getEscalasMes() : 0)));

        int limite = Math.min(vagas, pool.size());
        return new ArrayList<>(pool.subList(0, limite));
    }

    @Transactional
    public EscalaDTO gerarEscala(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + eventoId));

        if (evento.isCancelado()) {
            throw new IllegalArgumentException("Não é possível gerar escala para evento cancelado");
        }

        List<Ministro> candidatos = ministroRepository.findByAtivoTrue();
        if (candidatos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum ministro ativo disponível");
        }

        int vagas = evento.getMaxMinistros() != null ? evento.getMaxMinistros() : 6;
        List<Ministro> selecionados = selecionarMinistros(candidatos, vagas);

        Escala escala = new Escala();
        escala.setEvento(evento);
        escala.setObservacao("Gerado por sorteio em " + LocalDate.now()
                + " — " + selecionados.size() + " ministros sorteados");
        escala.setStatus(StatusEscala.PROPOSTA);

        for (Ministro m : selecionados) {
            EscalaMinistro em = new EscalaMinistro();
            em.setEscala(escala);
            em.setMinistro(m);
            escala.getEscalaMinistros().add(em);
            m.setEscalasMes(m.getEscalasMes() != null ? m.getEscalasMes() + 1 : 1);
        }

        Escala salvo = repository.save(escala);
        ministroRepository.saveAll(selecionados);
        auditoriaService.registrar("Escala", "CRIADO", null, "PROPOSTA");
        return toDTO(salvo);
    }

    // -------------------------------------------------------------------------
    // CRUD padrão
    // -------------------------------------------------------------------------

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
            if (em.getMinistro() != null) {
                emDto.setMinistroId(em.getMinistro().getId());
                emDto.setMinistroNome(em.getMinistro().getNome());
                emDto.setMinistroFuncao(em.getMinistro().getFuncao() != null
                        ? em.getMinistro().getFuncao().name() : null);
            }
            emDto.setConfirmacaoMinistro(em.isConfirmacaoMinistro());
            emDto.setDataConfirmacao(em.getDataConfirmacao());
            emDto.setSubstituido(em.isSubstituido());
            return emDto;
        }).collect(Collectors.toList());
        dto.setEscalaMinistros(emDTOs);
        return dto;
    }
}
