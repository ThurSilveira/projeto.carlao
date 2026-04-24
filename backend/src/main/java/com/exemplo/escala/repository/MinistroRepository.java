package com.exemplo.escala.repository;

import com.exemplo.escala.model.Ministro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MinistroRepository extends JpaRepository<Ministro, Long> {
    List<Ministro> findByAtivoTrue();
}
