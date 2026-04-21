package com.exemplo.escala.repository;

import com.exemplo.escala.model.Feedback;
import com.exemplo.escala.model.enums.StatusFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStatus(StatusFeedback status);
    List<Feedback> findByMinistroId(Long ministroId);
}
