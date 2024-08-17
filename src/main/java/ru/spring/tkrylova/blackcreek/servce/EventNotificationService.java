package ru.spring.tkrylova.blackcreek.servce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;

import java.util.List;

@Component
public class EventNotificationService {
    private static final Logger log = LoggerFactory.getLogger(EventNotificationScheduler.class);

    @Autowired
    private EmailService emailService;

    public void notifyAttendeesWhenCloseEvent(BlackCreekEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        String subject = "Event Cancellation: " + event.getEventName();
        String start = event.getEventStartDate() == null ? "some day" : event.getEventStartDate().toString();
        String message = "Dear Attendee,\n\nWe regret to inform you that the event '" + event.getEventName() +
                "' scheduled for " + start +
                " has been cancelled.\n\nWe apologize for any inconvenience caused.\n\nBest regards,\nMedieval Park Team";
        if (event.getAttendees() != null && !event.getAttendees().isEmpty())
            for (BlackCreekUser attendee : event.getAttendees()) {
                emailService.sendEmail(attendee.getEmail(), subject, message);
                log.info("Send message: {}", message);
            }
    }

    public void notifyUserOfAddedToEvent(BlackCreekUser user, BlackCreekEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        String subject = "You have been added to an event: " + event.getEventName();
        String start = event.getEventStartDate() == null ? "some day in future" : event.getEventStartDate().toString();
        String end = event.getEventEndDate() == null ? "some day in future" : event.getEventEndDate().toString();
        String body = "Dear " + user.getLogin() + ",\n\nYou have been added to the event \"" + event.getEventName() + "\". It will start on " +
                start + " and end on " + end + ".\n\nBest regards,\nYour Medieval Park Team";
        emailService.sendEmail(user.getEmail(), subject, body);
        log.info("Send message: {}", body);
    }

    public void notifyUsersOfNewEvent(BlackCreekEvent event, List<BlackCreekUser> users) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        for (BlackCreekUser user : users) {
            String subject = "New Event: " + event.getEventName();
            String start = event.getEventStartDate() == null ? "some day in future" : event.getEventStartDate().toString();
            String end = event.getEventEndDate() == null ? "some day in future" : event.getEventEndDate().toString();
            String body = "Dear " + user.getLogin() + ",\n\nA new event \"" + event.getEventName() + "\" has been created. It will start on " +
                    start + " and end on " + end + ".\n\nBest regards,\nYour Medieval Park Team";
            emailService.sendEmail(user.getEmail(), subject, body);
            log.info("Send message: {}", body);
        }
    }
}
