package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);
}
