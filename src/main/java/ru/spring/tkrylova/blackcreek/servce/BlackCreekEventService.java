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
    private final BlackCreekUserService blackCreekUserService;
    private final EventNotificationService eventNotificationService;
    private final FeedbackService feedbackService;
    private final EventPhotoService eventPhotoService;

    @Value("${file.upload-dir}")
    private String photoDir;

    public BlackCreekEventService(BlackCreekEventRepository blackCreekEventRepository, BlackCreekUserService blackCreekUserService, EventNotificationService eventNotificationService, FeedbackService feedbackService, EventPhotoService eventPhotoService) {
        this.blackCreekEventRepository = blackCreekEventRepository;
        this.blackCreekUserService = blackCreekUserService;
        this.eventNotificationService = eventNotificationService;
        this.feedbackService = feedbackService;
        this.eventPhotoService = eventPhotoService;
    }

    public List<BlackCreekEvent> getAllEvents() {
        return blackCreekEventRepository.findAll();
    }

    public BlackCreekEvent findEventById(Long eventId) {
        verifyIdNotNullAndPositive(eventId, "Event");
        return blackCreekEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public BlackCreekEvent saveEvent(BlackCreekEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent createEvent(BlackCreekEvent event) {
        BlackCreekEvent savedEvent = saveEvent(event);
        eventNotificationService.notifyUsersOfNewEvent(savedEvent, blackCreekUserService.findAllUsers());
        return savedEvent;
    }

    public BlackCreekEvent setResponsibleUserToEvent(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        BlackCreekUser user = blackCreekUserService.findUserById(userId);
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
        if (event.getAttendees().size() >= event.getEventCapacity()) {
            throw new IllegalStateException("Event has reached its maximum capacity.");
        }
        BlackCreekUser user = blackCreekUserService.findUserById(userId);
        event.getUsers().add(user);
        BlackCreekEvent updatedEvent = blackCreekEventRepository.save(event);
        eventNotificationService.notifyUserOfAddedToEvent(user, event);
        return updatedEvent;
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
        BlackCreekEvent event = findEventById(eventId);
        if (event.getAttendees().size() >= event.getEventCapacity()) {
            throw new IllegalStateException("Event has reached its maximum capacity.");
        }
        BlackCreekUser user = blackCreekUserService.findUserById(userId);
        event.getAttendees().add(user);
        return blackCreekEventRepository.save(event);
    }

    public BlackCreekEvent removeAttendeeFromEvent(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        BlackCreekUser user = blackCreekUserService.findUserById(userId);
        event.getAttendees().remove(user);
        return blackCreekEventRepository.save(event);
    }

    public boolean isUserAttending(Long eventId, Long userId) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        return event.getAttendees().stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    public List<BlackCreekEvent> searchEvents(String keyword) {
        return blackCreekEventRepository.searchEventByEventNameOrEventDescription(keyword);
    }

    public BlackCreekEvent cancelEvent(Long eventId) {
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        event.setCancelled(true);
        BlackCreekEvent cancelledEvent = blackCreekEventRepository.save(event);
        eventNotificationService.notifyAttendeesWhenCloseEvent(cancelledEvent);
        return cancelledEvent;
    }

    public Feedback addFeedback(Long eventId, Long userId, String comments, int rating) {
        verifyIdNotNullAndPositive(userId, "User");
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
        BlackCreekUser user = blackCreekUserService.findUserById(userId);
        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setComments(comments);
        feedback.setRating(rating);
        return feedbackService.saveFeedback(feedback);
    }

    public List<Feedback> getFeedbackForEvent(Long eventId) {
        verifyIdNotNullAndPositive(eventId, "Event");
        return feedbackService.getFeedbackByEventId(eventId);
    }

    public void addPhotoToEvent(Long eventId, MultipartFile file) throws IOException {
        verifyIdNotNullAndPositive(eventId, "Event");
        BlackCreekEvent event = findEventById(eventId);
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
        verifyIdNotNullAndPositive(eventId, "Event");
        if (updatedEvent == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        BlackCreekEvent existingEvent = findEventById(eventId);
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
