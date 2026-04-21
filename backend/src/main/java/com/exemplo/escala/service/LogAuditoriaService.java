package com.exemplo.escala.service;

import com.exemplo.escala.dto.LogAuditoriaDTO;
import com.exemplo.escala.model.LogAuditoria;
import com.exemplo.escala.model.enums.TipoAcao;
import com.exemplo.escala.repository.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogAuditoriaService {

    @Autowired
    private LogAuditoriaRepository repository;

    public void registrar(String entidade, String acao, String statusAnterior, String statusNovo) {
        try {
            LogAuditoria log = new LogAuditoria(entidade, TipoAcao.valueOf(acao), statusAnterior, statusNovo);
            repository.save(log);
        } catch (IllegalArgumentException ignored) {}
    }

    public List<LogAuditoriaDTO> listar() {
        return repository.findAllByOrderByDataHoraDesc().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LogAuditoriaDTO toDTO(LogAuditoria log) {
        LogAuditoriaDTO dto = new LogAuditoriaDTO();
        dto.setId(log.getId());
        dto.setEntidade(log.getEntidade());
        dto.setAcao(log.getAcao() != null ? log.getAcao().name() : null);
        dto.setStatusAnterior(log.getStatusAnterior());
        dto.setStatusNovo(log.getStatusNovo());
        dto.setRealizadoPorId(log.getRealizadoPorId());
        dto.setDataHora(log.getDataHora());
        return dto;
    }
}
