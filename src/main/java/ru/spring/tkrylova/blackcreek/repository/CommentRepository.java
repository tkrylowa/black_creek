package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(Long eventId);
}
