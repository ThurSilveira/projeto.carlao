package com.exemplo.escala.service;

import com.exemplo.escala.dto.IndisponibilidadeDTO;
import com.exemplo.escala.model.Indisponibilidade;
import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.repository.IndisponibilidadeRepository;
import com.exemplo.escala.repository.MinistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndisponibilidadeService {

    @Autowired private IndisponibilidadeRepository repository;
    @Autowired private MinistroRepository ministroRepository;

    public List<IndisponibilidadeDTO> listarPorMinistro(Long ministroId) {
        return repository.findByMinistroId(ministroId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public IndisponibilidadeDTO criar(Long ministroId, IndisponibilidadeDTO dto) {
        Ministro ministro = ministroRepository.findById(ministroId)
                .orElseThrow(() -> new IllegalArgumentException("Ministro não encontrado: " + ministroId));
        Indisponibilidade entity = new Indisponibilidade();
        entity.setMinistro(ministro);
        entity.setData(dto.getData());
        entity.setHorarioInicio(dto.getHorarioInicio());
        entity.setHorarioFim(dto.getHorarioFim());
        entity.setMotivo(dto.getMotivo());
        return toDTO(repository.save(entity));
    }

    public IndisponibilidadeDTO atualizar(Long id, IndisponibilidadeDTO dto) {
        Indisponibilidade entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Indisponibilidade não encontrada: " + id));
        entity.setData(dto.getData());
        entity.setHorarioInicio(dto.getHorarioInicio());
        entity.setHorarioFim(dto.getHorarioFim());
        entity.setMotivo(dto.getMotivo());
        return toDTO(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public IndisponibilidadeDTO toDTO(Indisponibilidade e) {
        IndisponibilidadeDTO dto = new IndisponibilidadeDTO();
        dto.setId(e.getId());
        dto.setMinistroId(e.getMinistro() != null ? e.getMinistro().getId() : null);
        dto.setData(e.getData());
        dto.setHorarioInicio(e.getHorarioInicio());
        dto.setHorarioFim(e.getHorarioFim());
        dto.setMotivo(e.getMotivo());
        return dto;
    }
}
