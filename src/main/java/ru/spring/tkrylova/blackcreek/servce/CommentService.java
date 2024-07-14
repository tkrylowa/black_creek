package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.Comment;
import ru.spring.tkrylova.blackcreek.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByEventId(Long eventId) {
        return commentRepository.findByEventId(eventId);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
