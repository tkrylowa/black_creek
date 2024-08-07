package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface BlackCreekEventRepository extends JpaRepository<BlackCreekEvent, Long> {
    List<BlackCreekEvent> findByEventStartDateBetween(LocalDateTime start, LocalDateTime end);

    @Query(nativeQuery = true, value = "SELECT * FROM Event e WHERE LOWER(e.event_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.event_description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BlackCreekEvent> searchEventByEventNameOrEventDescription(String keyword);
}
