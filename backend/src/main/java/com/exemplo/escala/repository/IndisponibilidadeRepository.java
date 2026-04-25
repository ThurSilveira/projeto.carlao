package com.exemplo.escala.repository;

import com.exemplo.escala.model.Indisponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IndisponibilidadeRepository extends JpaRepository<Indisponibilidade, Long> {
    List<Indisponibilidade> findByMinistroId(Long ministroId);
    void deleteByMinistroId(Long ministroId);
}
