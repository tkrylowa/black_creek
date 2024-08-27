package ru.spring.tkrylova.blackcreek.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.EventPhotoRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;
import ru.spring.tkrylova.blackcreek.servce.FeedbackService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/events")
public class BlackCreekEventController {
    private final BlackCreekEventService blackCreekEventService;
    private final BlackCreekUserService blackCreekUserService;
    private final FeedbackService feedbackService;

    public BlackCreekEventController(BlackCreekEventService blackCreekEventService, BlackCreekUserService blackCreekUserService, FeedbackService feedbackService, EventPhotoRepository eventPhotoService) {
        this.blackCreekEventService = blackCreekEventService;
        this.blackCreekUserService = blackCreekUserService;
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String getAllEvents(Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.getAllEvents();
        log.info("Found {} events", events.size());
        model.addAttribute("events", events);
        return "event/events";
    }

    @GetMapping("/add")
    public String showEventForm(Model model) {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setFree(true);
        model.addAttribute("event", event);
        return "event/event_form";
    }

    @PostMapping("/add")
    public String createEvent(@ModelAttribute("event") @Valid BlackCreekEvent event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.error("Error occurred! {}", result.getAllErrors());
            model.addAttribute("error", result.getFieldErrors());
            return "event/event_form";
        }
        BlackCreekEvent savedEvent = blackCreekEventService.createEvent(event);
        log.info("New event with id {} was successfully created", savedEvent.getEventId());
        model.addAttribute("reloadScript", "<script>setTimeout(function(){ window.location.reload(); }, 1000);</script>");
        return "redirect:/events";
    }

    @GetMapping("/{eventId}")
    public String viewEvent(@PathVariable Long eventId, Model model) {
        BlackCreekEvent event = blackCreekEventService.findEventById(eventId);
        List<Feedback> feedbacks = feedbackService.getFeedbackByEventId(eventId);
        log.info("Event with id {} was found", eventId);
        model.addAttribute("event", event);
        model.addAttribute("users", blackCreekUserService.findAllUsers());
        model.addAttribute("comments", feedbacks);
        return "event/view";
    }

    @PostMapping("/{eventId}/addUser")
    public String addUserToEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        blackCreekEventService.addUserToEvent(eventId, userId);
        log.info("User was added to event with id {}", eventId);
        return "redirect:/events";
    }

    @PostMapping("/{eventId}/setResponsiblePerson")
    public String setResponsiblePerson(@PathVariable Long eventId, @RequestParam Long userId, RedirectAttributes redirectAttributes) {
        try {
            blackCreekEventService.setResponsibleUserToEvent(eventId, userId);
            log.info("Responsible user was added to event with id {}", eventId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered for the event.");
        } catch (IllegalStateException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/events";
    }

    @PostMapping("/{eventId}/attend")
    public String markAttendance(@PathVariable Long eventId, Principal principal, RedirectAttributes redirectAttributes) {
        BlackCreekUser blackCreekUser = blackCreekUserService.findUserByLogin(principal.getName());
        try {
            blackCreekEventService.addAttendeeToEvent(eventId, blackCreekUser.getUserId());
            log.info("Event with id {} was marked as attended by current user", eventId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered for the event.");
        } catch (IllegalStateException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/events";
    }

    @PostMapping("/{eventId}/unattend")
    public String unmarkAttendance(@PathVariable Long eventId, Principal principal) {
        BlackCreekUser blackCreekUser = blackCreekUserService.findUserByLogin(principal.getName());
        blackCreekEventService.removeAttendeeFromEvent(eventId, blackCreekUser.getUserId());
        log.info("Event with id {} was unmarked as attended by current user", eventId);
        return "redirect:/events";
    }

    @GetMapping("/search")
    public String searchEvents(@RequestParam("keyword") String keyword, Model model) {
        List<BlackCreekEvent> events = blackCreekEventService.searchEvents(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        log.info("Found {} events by keyword {}", events.size(), keyword);
        return "/event/search-results";
    }

    @PostMapping("/{eventId}/cancel")
    public String cancelEvent(@PathVariable Long eventId) {
        blackCreekEventService.cancelEvent(eventId);
        log.info("Event with id {} was canceled", eventId);
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/{eventId}/feedback")
    public String addFeedback(@PathVariable Long eventId, Principal principal, @RequestParam String comments, @RequestParam int rating, Model model) {
        BlackCreekUser blackCreekUser = blackCreekUserService.findUserByLogin(principal.getName());
        blackCreekEventService.addFeedback(eventId, blackCreekUser.getUserId(), comments, rating);
        model.addAttribute("reloadScript", "<script>setTimeout(function(){ window.location.reload(); }, 1000);</script>");
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/{eventId}/eventPhotos")
    public String uploadPhoto(@PathVariable Long eventId, @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {
        BlackCreekEvent event = blackCreekEventService.findEventById(eventId);
        if (event != null && !photo.isEmpty()) {
            try {
                blackCreekEventService.addPhotoToEvent(event.getEventId(), photo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            redirectAttributes.addFlashAttribute("successMessage", "EventPhoto uploaded successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload photo!");
        }
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/{eventId}/update")
    public String updateEvent(@PathVariable("eventId") Long eventId,
                              @ModelAttribute("event") BlackCreekEvent event,
                              RedirectAttributes redirectAttributes) {
        try {
            blackCreekEventService.updateEvent(eventId, event);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the event. Please try again.");
        }
        return "redirect:/event/" + eventId + "/view";
    }
}