package ru.spring.tkrylova.blackcreek.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;

import java.util.List;

@Controller
@RequestMapping("/events")
public class BlackCreekEventController {
    private final BlackCreekEventService blackCreekEventService;

    public BlackCreekEventController(BlackCreekEventService blackCreekEventService) {
        this.blackCreekEventService = blackCreekEventService;
    }

    @GetMapping
    public String getAllEvents(Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.getAllEvents();
        model.addAttribute("events", events);
        return "event/events";
    }

    @GetMapping("/new")
    public String showEventForm(Model model) {
        model.addAttribute("event", new BlackCreekEvent());
        return "event/event_form";
    }

    @PostMapping("/events")
    public String createEvent(@ModelAttribute BlackCreekEvent event) {
        blackCreekEventService.saveEvent(event);
        return "redirect:/event/events";
    }

}
