package ru.spring.tkrylova.blackcreek.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.entity.Photo;
import ru.spring.tkrylova.blackcreek.repository.PhotoRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;
import ru.spring.tkrylova.blackcreek.servce.FeedbackService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/events")
public class BlackCreekEventController {
    private final BlackCreekEventService blackCreekEventService;
    private final BlackCreekUserService blackCreekUserService;
    private final FeedbackService feedbackService;

    public BlackCreekEventController(BlackCreekEventService blackCreekEventService, BlackCreekUserService blackCreekUserService, FeedbackService feedbackService, PhotoRepository photoRepository) {
        this.blackCreekEventService = blackCreekEventService;
        this.blackCreekUserService = blackCreekUserService;
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String getAllEvents(Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.getAllEvents();
        log.atInfo().log("Found {} events", events.size());
        model.addAttribute("events", events);
        return "event/events";
    }

    @GetMapping("/new")
    public String showEventForm(Model model) {
        model.addAttribute("event", new BlackCreekEvent());
        return "event/event_form";
    }

    @PostMapping("/add")
    public String createEvent(@ModelAttribute BlackCreekEvent event) {
        BlackCreekEvent savedEvent = blackCreekEventService.saveEvent(event);
        log.atInfo().log("New event with id {} was successfully created", savedEvent.getEventId());
        return "redirect:/event/events";
    }

    @GetMapping("/{eventId}")
    public String viewEvent(@PathVariable Long eventId, Model model) {
        BlackCreekEvent event = blackCreekEventService.findEventById(eventId);
        List<Feedback> feedbacks = feedbackService.getFeedbackByEventId(eventId);
        log.atInfo().log("Event with id {} was found", eventId);
        model.addAttribute("event", event);
        model.addAttribute("users", blackCreekUserService.findAllUsers());
        model.addAttribute("comments", feedbacks);
        return "event/events/view";
    }

    @PostMapping("/{eventId}/addUser")
    public String addUserToEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.addUserToEvent(eventId, userId);
        log.atInfo().log("User was added to event with id {}", eventId);
        return "redirect:/event/events/" + eventId;
    }

    @PostMapping("/{eventId}/setResponsiblePerson")
    public String setResponsiblePerson(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.setResponsibleUserToEvent(eventId, userId);
        log.atInfo().log("Responsible user was added to event with id {}", eventId);
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/{eventId}/attend")
    public String markAttendance(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.markAttendance(eventId, userId);
        log.atInfo().log("Event with id {} was marked as attended by user", eventId);
        return "redirect:/event/events/" + eventId;
    }

    @PostMapping("/{eventId}/unattend")
    public String unmarkAttendance(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.unmarkAttendance(eventId, userId);
        log.atInfo().log("Event with id {} was unmarked as attended", eventId);
        return "redirect:/event/events/" + eventId;
    }

    @GetMapping("/search")
    public String searchEvents(@RequestParam("keyword") String keyword, Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.searchEvents(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        log.atInfo().log("Found {} events by keyword {}", events.size(), keyword);
        return "/event/events/search-results";
    }

    @PostMapping("/{eventId}/cancel")
    public String cancelEvent(@PathVariable Long eventId) {
        blackCreekEventService.cancelEvent(eventId);
        log.atInfo().log("Event with id {} was canceled", eventId);
        return "redirect:/event/events/" + eventId;
    }

    @PostMapping("/{eventId}/feedback")
    public String addFeedback(@PathVariable Long eventId, @RequestParam Long userId, @RequestParam String comments, @RequestParam int rating) {
        blackCreekEventService.addFeedback(eventId, userId, comments, rating);
        return "redirect:/events/" + eventId;
    }

    @GetMapping("/{eventId}/photos")
    public String showPhotoUploadForm(@PathVariable Long eventId, Model model) {
        BlackCreekEvent event = blackCreekEventService.findEventById(eventId);
        model.addAttribute("event", event);
        return "photoUploadForm";
    }

    @PostMapping("/{eventId}/photos")
    public String uploadPhoto(@PathVariable Long eventId, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                blackCreekEventService.addPhoto(eventId, file);
                log.atInfo().log("Photo was successfully added");
                redirectAttributes.addFlashAttribute("message", "Photo uploaded successfully");
            } catch (IOException e) {
                log.atError().log("Some exception occurred: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("message", "Failed to upload photo");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Invalid event or file");
        }
        return "redirect:/event/events/" + eventId + "/photos";
    }
}
