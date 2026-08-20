package br.com.escalaministerial.repository;

import br.com.escalaministerial.model.Ministro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinistroRepository extends JpaRepository<Ministro, Long> {
}
