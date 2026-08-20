package br.com.escalaministerial.repository;

import br.com.escalaministerial.model.Indisponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndisponibilidadeRepository extends JpaRepository<Indisponibilidade, Long> {
}
