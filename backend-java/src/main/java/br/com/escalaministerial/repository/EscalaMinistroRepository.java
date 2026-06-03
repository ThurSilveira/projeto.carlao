package br.com.escalaministerial.repository;

import br.com.escalaministerial.model.EscalaMinistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalaMinistroRepository extends JpaRepository<EscalaMinistro, Long> {
}
