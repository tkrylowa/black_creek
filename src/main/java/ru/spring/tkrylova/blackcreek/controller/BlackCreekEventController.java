package ru.spring.tkrylova.blackcreek.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.Comment;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.CommentService;

import java.util.List;

@Controller
@RequestMapping("/events")
public class BlackCreekEventController {
    private final BlackCreekEventService blackCreekEventService;
    private final CommentService commentService;

    public BlackCreekEventController(BlackCreekEventService blackCreekEventService, CommentService commentService) {
        this.blackCreekEventService = blackCreekEventService;
        this.commentService = commentService;
    }

    @GetMapping
    public String getAllEvents(Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.getAllEvents();
        model.addAttribute("events", events);
        return "event/events";
    }

    @GetMapping("/events/{id}")
    public String getEventById(@PathVariable Long id, Model model) {
        BlackCreekEvent event = blackCreekEventService.getEventById(id);
        List<Comment> comments = commentService.getCommentsByEventId(id);
        model.addAttribute("event", event);
        model.addAttribute("comments", comments);
        return "event/event_detail";
    }

    @GetMapping("/new")
    public String showEventForm(Model model) {
        model.addAttribute("event", new BlackCreekEvent());
        return "event/event_form";
    }

    @PostMapping("/add")
    public String createEvent(@ModelAttribute BlackCreekEvent event) {
        blackCreekEventService.saveEvent(event);
        return "redirect:/event/events";
    }

}
