package ru.spring.tkrylova.blackcreek.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.Comment;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;
import ru.spring.tkrylova.blackcreek.servce.CommentService;

import java.util.List;

@Controller
@RequestMapping("/events")
public class BlackCreekEventController {
    private final BlackCreekEventService blackCreekEventService;
    private final BlackCreekUserService blackCreekUserService;
    private final CommentService commentService;

    public BlackCreekEventController(BlackCreekEventService blackCreekEventService, BlackCreekUserService blackCreekUserService, CommentService commentService) {
        this.blackCreekEventService = blackCreekEventService;
        this.blackCreekUserService = blackCreekUserService;
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

    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id, Model model) {
        BlackCreekEvent event = blackCreekEventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("users", blackCreekUserService.findAllUsers());
        return "event/events/view";
    }

    @PostMapping("/{eventId}/addUser")
    public String addUserToEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.addUserToEvent(eventId, userId);
        return "redirect:/event/events/" + eventId;
    }

    @PostMapping("/{eventId}/setResponsiblePerson")
    public String setResponsiblePerson(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.setResponsibleUserToEvent(eventId, userId);
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/{eventId}/attend")
    public String markAttendance(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.markAttendance(eventId, userId);
        return "redirect:/event/events/" + eventId;
    }

    @PostMapping("/{eventId}/unattend")
    public String unmarkAttendance(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.unmarkAttendance(eventId, userId);
        return "redirect:/event/events/" + eventId;
    }

    @GetMapping("/search")
    public String searchEvents(@RequestParam("keyword") String keyword, Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.searchEvents(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        return "/event/events/search-results";
    }

    @PostMapping("/{eventId}/cancel")
    public String cancelEvent(@PathVariable Long eventId) {
        blackCreekEventService.cancelEvent(eventId);
        return "redirect:/event/events/" + eventId;
    }
}
