package com.exemplo.escala.repository;

import com.exemplo.escala.model.EscalaMinistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EscalaMinistroRepository extends JpaRepository<EscalaMinistro, Long> {
    List<EscalaMinistro> findByEscalaId(Long escalaId);
    List<EscalaMinistro> findByMinistroId(Long ministroId);
}
