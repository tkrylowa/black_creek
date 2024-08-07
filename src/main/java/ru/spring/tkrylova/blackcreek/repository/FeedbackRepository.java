package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.spring.tkrylova.blackcreek.entity.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM feedbacks f WHERE event_id = :eventId")
    List<Feedback> findByEventId(Long eventId);
}
