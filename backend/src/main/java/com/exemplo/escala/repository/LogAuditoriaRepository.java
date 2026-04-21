package com.exemplo.escala.repository;

import com.exemplo.escala.model.LogAuditoria;
import com.exemplo.escala.model.enums.TipoAcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {
    List<LogAuditoria> findByEntidade(String entidade);
    List<LogAuditoria> findByAcao(TipoAcao acao);
    List<LogAuditoria> findAllByOrderByDataHoraDesc();
}
