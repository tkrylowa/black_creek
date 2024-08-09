package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.EventPhoto;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.repository.FeedbackRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BlackCreekEventService {
    private final BlackCreekEventRepository blackCreekEventRepository;
    private final BlackCreekUserRepository blackCreekUserRepository;
    private final EmailService emailService;
    private final FeedbackRepository feedbackRepository;
    private final EventPhotoService eventPhotoService;

    @Value("${file.upload-dir}")
    private String photoDir;

    public BlackCreekEventService(BlackCreekEventRepository blackCreekEventRepository, BlackCreekUserRepository blackCreekUserRepository, EmailService emailService, FeedbackRepository feedbackRepository, EventPhotoService eventPhotoService) {
        this.blackCreekEventRepository = blackCreekEventRepository;
        this.blackCreekUserRepository = blackCreekUserRepository;
        this.emailService = emailService;
        this.feedbackRepository = feedbackRepository;
        this.eventPhotoService = eventPhotoService;
    }

    public List<BlackCreekEvent> getAllEvents() {
        return blackCreekEventRepository.findAll();
    }

    public BlackCreekEvent findEventById(Long eventId) {
        verifyIdNotNullAndPositive(eventId, "Event");
        return blackCreekEventRepository.findById(eventId).orElse(null);
    }

    public BlackCreekEvent saveEvent(BlackCreekEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent setResponsibleUserToEvent(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Event not found");
        }
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        event.setResponsibleUserId(user.getUserId());
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent setCostToEvent(Long eventId, Double cost) {
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        event.setCost(cost);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent addUserToEvent(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        BlackCreekUser user = blackCreekUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
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

    public List<BlackCreekEvent> findEventsBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        return blackCreekEventRepository.findByEventStartDateBetween(start, end);
    }

    public BlackCreekEvent addAttendeeToEvent(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        event.getAttendees().add(user);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent removeAttendeeFromEvent(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        BlackCreekUser user = blackCreekUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        event.getAttendees().remove(user);
        return blackCreekEventRepository.save(event);
    }

    public boolean isUserAttending(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return event.getAttendees().stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    public List<BlackCreekEvent> searchEvents(String keyword) {
        return blackCreekEventRepository.searchEventByEventNameOrEventDescription(keyword);
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
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        BlackCreekUser user = blackCreekUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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

    public void addPhotoToEvent(Long eventId, MultipartFile file) throws IOException {
        BlackCreekEvent event = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(photoDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        String fileUrl = "/uploaded-photos/" + fileName;
        EventPhoto eventPhoto = new EventPhoto();
        eventPhoto.setPhotoUrl(fileUrl);
        eventPhoto.setEvent(event);
        eventPhotoService.save(eventPhoto);
        event.getEventPhotos().add(eventPhoto);
        blackCreekEventRepository.save(event);
    }

    public void updateEvent(Long eventId, BlackCreekEvent updatedEvent) {
        BlackCreekEvent existingEvent = blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        existingEvent.setEventName(updatedEvent.getEventName());
        existingEvent.setEventStartDate(updatedEvent.getEventStartDate());
        existingEvent.setEventEndDate(updatedEvent.getEventEndDate());
        existingEvent.setEventDescription(updatedEvent.getEventDescription());
        existingEvent.setEventCapacity(updatedEvent.getEventCapacity());
        existingEvent.setFree(updatedEvent.isFree());
        existingEvent.setCancelled(updatedEvent.isCancelled());
        existingEvent.setCost(updatedEvent.getCost());
        existingEvent.setResponsibleUserId(updatedEvent.getResponsibleUserId());

        blackCreekEventRepository.save(existingEvent);
    }

    private void verifyIdNotNullAndPositive(Long id, String typeOfId) {
        if (id == null) {
            throw new ResourceNotFoundException(String.format("%s id is null", typeOfId));
        }
        if (id < 0) {
            throw new ResourceNotFoundException(String.format("%s id is invalid", typeOfId));
        }
    }
}
