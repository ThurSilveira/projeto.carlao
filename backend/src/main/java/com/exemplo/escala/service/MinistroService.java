package com.exemplo.escala.service;

import com.exemplo.escala.dto.MinistroDTO;
import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.model.enums.FuncaoMinistro;
import com.exemplo.escala.repository.MinistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MinistroService {

    @Autowired
    private MinistroRepository repository;

    @Autowired
    private LogAuditoriaService auditoriaService;

    public MinistroDTO criar(MinistroDTO dto) {
        Ministro ministro = new Ministro();
        preencherMinistro(ministro, dto);
        Ministro salvo = repository.save(ministro);
        auditoriaService.registrar("Ministro", "CRIADO", null, salvo.getNome());
        return toDTO(salvo);
    }

    public List<MinistroDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MinistroDTO obterPorId(Long id) {
        return repository.findById(id).map(this::toDTO).orElse(null);
    }

    public MinistroDTO atualizar(Long id, MinistroDTO dto) {
        Ministro ministro = repository.findById(id).orElse(null);
        if (ministro == null) return null;
        String nomePrev = ministro.getNome();
        preencherMinistro(ministro, dto);
        Ministro salvo = repository.save(ministro);
        auditoriaService.registrar("Ministro", "ATUALIZADO", nomePrev, salvo.getNome());
        return toDTO(salvo);
    }

    public void deletar(Long id) {
        repository.findById(id).ifPresent(m -> {
            auditoriaService.registrar("Ministro", "DELETADO", m.getNome(), null);
            repository.deleteById(id);
        });
    }

    private void preencherMinistro(Ministro ministro, MinistroDTO dto) {
        ministro.setNome(dto.getNome());
        ministro.setEmail(dto.getEmail());
        ministro.setTelefone(dto.getTelefone());
        ministro.setDataNascimento(dto.getDataNascimento());
        ministro.setObservacoes(dto.getObservacoes());
        ministro.setAtivo(dto.isAtivo());
        ministro.setVisitasAoInfermo(dto.isVisitasAoInfermo());
        ministro.setStatusCurso(dto.isStatusCurso());
        if (dto.getFuncao() != null) {
            try {
                ministro.setFuncao(FuncaoMinistro.valueOf(dto.getFuncao()));
            } catch (IllegalArgumentException e) {
                ministro.setFuncao(FuncaoMinistro.LEITURA);
            }
        }
    }

    public MinistroDTO toDTO(Ministro m) {
        MinistroDTO dto = new MinistroDTO();
        dto.setId(m.getId());
        dto.setNome(m.getNome());
        dto.setEmail(m.getEmail());
        dto.setTelefone(m.getTelefone());
        dto.setDataNascimento(m.getDataNascimento());
        dto.setObservacoes(m.getObservacoes());
        dto.setAtivo(m.isAtivo());
        dto.setVisitasAoInfermo(m.isVisitasAoInfermo());
        dto.setStatusCurso(m.isStatusCurso());
        dto.setEscalasMes(m.getEscalasMes());
        dto.setFuncao(m.getFuncao() != null ? m.getFuncao().name() : null);
        return dto;
    }
}
