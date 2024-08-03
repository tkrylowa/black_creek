package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlackCreekEventService {
    private final BlackCreekEventRepository blackCreekEventRepository;
    private final BlackCreekUserRepository blackCreekUserRepository;
    private final  EmailService emailService;

    public BlackCreekEventService(BlackCreekEventRepository blackCreekEventRepository, BlackCreekUserRepository blackCreekUserRepository, EmailService emailService) {
        this.blackCreekEventRepository = blackCreekEventRepository;
        this.blackCreekUserRepository = blackCreekUserRepository;
        this.emailService = emailService;
    }

    public List<BlackCreekEvent> getAllEvents() {
        return blackCreekEventRepository.findAll();
    }

    public BlackCreekEvent getEventById(Long id) {
        return blackCreekEventRepository.findById(id).orElse(null);
    }

    public void saveEvent(BlackCreekEvent event) {
        blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent setResponsibleUserToEvent(Long eventId, Long userId) {
        BlackCreekEvent event = getEventById(eventId);
        event.setResponsibleUserId(userId);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent setCostToEvent(Long eventId, Double cost) {
        if (eventId == null) {
            throw new RuntimeException("Event not found");
        }
        BlackCreekEvent event = getEventById(eventId);
        event.setCost(cost);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent addUserToEvent(Long eventId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("User not found");
        }
        if (eventId == null) {
            throw new RuntimeException("Event not found");
        }
        BlackCreekEvent event = getEventById(eventId);
        BlackCreekUser user = blackCreekUserRepository.findById(userId).orElseThrow();
        event.getUsers().add(user);
        BlackCreekEvent updatedEvent = blackCreekEventRepository.save(event);
        notifyUserOfAddedToEvent(user, event);
        return updatedEvent;
    }

    private void notifyUsersOfNewEvent(BlackCreekEvent event) {
        for (BlackCreekUser user : blackCreekUserRepository.findAll()) {
            String subject = "New Event: " + event.getEventName();
            String body = "Dear " + user.getLogin() + ",\n\nA new event \"" + event.getEventName() + "\" has been created. It will start on " +
                    event.getEventStartDate() + " and end on " + event.getEventEndDate() + ".\n\nBest regards,\nYour Medieval Park Team";
            emailService.sendEmail(user.getEmail(), subject, body);
        }
    }

    private void notifyUserOfAddedToEvent(BlackCreekUser user, BlackCreekEvent event) {
        String subject = "You have been added to an event: " + event.getEventName();
        String body = "Dear " + user.getLogin() + ",\n\nYou have been added to the event \"" + event.getEventName() + "\". It will start on " +
                event.getEventStartDate() + " and end on " + event.getEventEndDate() + ".\n\nBest regards,\nYour Medieval Park Team";
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public List<BlackCreekEvent> findEventsBetween(LocalDateTime start, LocalDateTime end) {
        return blackCreekEventRepository.findByEventStartDateBetween(start, end);
    }

    public BlackCreekEvent markAttendance(Long eventId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("User not found");
        }
        if (eventId == null) {
            throw new RuntimeException("Event not found");
        }
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        event.getAttendees().add(user);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent unmarkAttendance(Long eventId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("User not found");
        }
        if (eventId == null) {
            throw new RuntimeException("Event not found");
        }
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        event.getAttendees().remove(user);
        return blackCreekEventRepository.save(event);
    }

    public boolean isUserAttending(Long eventId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("User not found");
        }
        if (eventId == null) {
            throw new RuntimeException("Event not found");
        }
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return event.getAttendees().stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    public List<BlackCreekEvent> searchEvents(String keyword) {
        return blackCreekEventRepository.searchBlackCreekEventByEventNameOrEventDescription(keyword);
    }

    public BlackCreekEvent cancelEvent(Long eventId) {
        BlackCreekEvent event = getEventById(eventId);
        event.setCancelled(true);
        BlackCreekEvent cancelledEvent = blackCreekEventRepository.save(event);
        notifyAttendees(cancelledEvent);
        return cancelledEvent;
    }

    private void notifyAttendees(BlackCreekEvent event) {
        String subject = "Event Cancellation: " + event.getEventName();
        String message = "Dear Attendee,\n\nWe regret to inform you that the event '" + event.getEventName() +
                "' scheduled for " + event.getEventStartDate() +
                " has been cancelled.\n\nWe apologize for any inconvenience caused.\n\nBest regards,\nMedieval Park Team";

        for (BlackCreekUser attendee : event.getAttendees()) {
            emailService.sendEmail(attendee.getEmail(), subject, message);
        }
    }

}
