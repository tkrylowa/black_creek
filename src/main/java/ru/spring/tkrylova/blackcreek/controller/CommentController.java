package ru.spring.tkrylova.blackcreek.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spring.tkrylova.blackcreek.entity.Comment;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserDetailService;
import ru.spring.tkrylova.blackcreek.servce.CommentService;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final BlackCreekEventService blackCreekEventService;
    private final BlackCreekUserDetailService blackCreekUserDetailService;
    private final CommentService commentService;

    public CommentController(BlackCreekEventService blackCreekEventService, BlackCreekUserDetailService blackCreekUserDetailService, CommentService commentService) {
        this.blackCreekEventService = blackCreekEventService;
        this.blackCreekUserDetailService = blackCreekUserDetailService;
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public String addComment(@RequestParam Long eventId, @RequestParam String content, Principal principal) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setEvent(blackCreekEventService.getEventById(eventId));
        comment.setUser(blackCreekUserDetailService.getUserByLogin(principal.getName()).orElseThrow());
        commentService.saveComment(comment);
        return "redirect:/events/" + eventId;
    }
}
