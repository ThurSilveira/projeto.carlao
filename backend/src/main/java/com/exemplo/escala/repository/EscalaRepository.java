package com.exemplo.escala.repository;

import com.exemplo.escala.model.Escala;
import com.exemplo.escala.model.enums.StatusEscala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EscalaRepository extends JpaRepository<Escala, Long> {
    List<Escala> findByStatus(StatusEscala status);

    @Query("SELECT e FROM Escala e LEFT JOIN FETCH e.escalaMinistros LEFT JOIN FETCH e.evento")
    List<Escala> findAllWithDetails();
}
