package ru.spring.tkrylova.blackcreek.servce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.entity.Photo;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.repository.FeedbackRepository;
import ru.spring.tkrylova.blackcreek.repository.PhotoRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlackCreekEventService {
    private static final Logger log = LoggerFactory.getLogger(BlackCreekEventService.class);
    private final BlackCreekEventRepository blackCreekEventRepository;
    private final BlackCreekUserRepository blackCreekUserRepository;
    private final EmailService emailService;
    private final FeedbackRepository feedbackRepository;
    private final PhotoRepository photoRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public BlackCreekEventService(BlackCreekEventRepository blackCreekEventRepository, BlackCreekUserRepository blackCreekUserRepository, EmailService emailService, FeedbackRepository feedbackRepository, PhotoRepository photoRepository) {
        this.blackCreekEventRepository = blackCreekEventRepository;
        this.blackCreekUserRepository = blackCreekUserRepository;
        this.emailService = emailService;
        this.feedbackRepository = feedbackRepository;
        this.photoRepository = photoRepository;
    }

    public List<BlackCreekEvent> getAllEvents() {
        return blackCreekEventRepository.findAll();
    }

    public BlackCreekEvent findEventById(Long id) {
        return blackCreekEventRepository.findById(id).orElse(null);
    }

    public BlackCreekEvent saveEvent(BlackCreekEvent event) {
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent setResponsibleUserToEvent(Long eventId, Long userId) {
        BlackCreekEvent event = findEventById(eventId);
        event.setResponsibleUserId(userId);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent setCostToEvent(Long eventId, Double cost) {
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        event.setCost(cost);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent addUserToEvent(Long eventId, Long userId) {
        verifyIdNull(userId, "User");
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
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
        verifyIdNull(userId, "User");
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        event.getAttendees().add(user);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent unmarkAttendance(Long eventId, Long userId) {
        verifyIdNull(userId, "User");
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        event.getAttendees().remove(user);
        return blackCreekEventRepository.save(event);
    }

    public boolean isUserAttending(Long eventId, Long userId) {
        verifyIdNull(userId, "User");
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return event.getAttendees().stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    public List<BlackCreekEvent> searchEvents(String keyword) {
        return blackCreekEventRepository.searchBlackCreekEventByEventNameOrEventDescription(keyword);
    }

    public BlackCreekEvent cancelEvent(Long eventId) {
        BlackCreekEvent event = findEventById(eventId);
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

    public Feedback addFeedback(Long eventId, Long userId, String comments, int rating) {
        verifyIdNull(userId, "User");
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        BlackCreekUser user = blackCreekUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Feedback feedback = Feedback.builder()
                .event(event)
                .user(user)
                .comments(comments)
                .rating(rating)
                .build();
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackForEvent(Long eventId) {
        return feedbackRepository.findByEventId(eventId);
    }

    public void addPhoto(Long eventId, MultipartFile file) throws IOException {
        verifyIdNull(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(uploadDir + File.separator + fileName);
            Files.copy(file.getInputStream(), path);
            Photo photo = Photo.builder()
                    .fileName(fileName)
                    .filePath("/uploads/" + fileName)
                    .event(event)
                    .build();
            photoRepository.save(photo);
        }
    }

    private void verifyIdNull(Long id, String typeOfId) {
        if (id == null) {
            log.atError().log("{} not found", typeOfId);
            throw new RuntimeException("Id not found");
        }
    }
}
