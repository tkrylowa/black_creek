package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.servce.EmailService;
import ru.spring.tkrylova.blackcreek.servce.EventNotificationService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EventNotificationServiceTest {
    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventNotificationService eventNotificationService;

    @Test
    void notifyAttendeesWhenCloseEvent_ShouldSendEmailNullDate() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(null);
        BlackCreekUser user = new BlackCreekUser();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        event.setAttendees(Collections.singleton(user));

        eventNotificationService.notifyAttendeesWhenCloseEvent(event);

        verify(emailService).sendEmail(eq("user@example.com"),
                eq("Event Cancellation: Test Event"),
                contains("Dear Attendee,\n\nWe regret to inform you that the event 'Test Event' scheduled for some day has been cancelled.\n\nWe apologize for any inconvenience caused.\n\nBest regards,\nMedieval Park Team"));
    }

    @Test
    void notifyAttendeesWhenCloseEvent_ShouldSendEmailNotNullDate() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(LocalDate.now());
        BlackCreekUser user = new BlackCreekUser();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        event.setAttendees(Collections.singleton(user));

        eventNotificationService.notifyAttendeesWhenCloseEvent(event);

        verify(emailService).sendEmail(eq("user@example.com"),
                eq("Event Cancellation: Test Event"),
                contains("Dear Attendee,\n\nWe regret to inform you that the event 'Test Event' scheduled for " +
                        event.getEventStartDate() + " has been cancelled.\n\nWe apologize for any inconvenience caused." +
                        "\n\nBest regards,\nMedieval Park Team"));
    }

    @Test
    void notifyAttendeesWhenCloseEvent_ShouldNotSendEmailIfNoAttendees() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(null);
        event.setAttendees(new HashSet<>());

        eventNotificationService.notifyAttendeesWhenCloseEvent(event);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void notifyAttendeesWhenCloseEvent_ShouldNotSendEmailIfNullAttendees() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(null);
        event.setAttendees(null);

        eventNotificationService.notifyAttendeesWhenCloseEvent(event);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void notifyUserOfAddedToEvent_ShouldSendEmailNullDate() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(null);
        event.setEventEndDate(null);
        BlackCreekUser user = new BlackCreekUser();
        user.setEmail("user@example.com");
        user.setLogin("user1");

        eventNotificationService.notifyUserOfAddedToEvent(user, event);

        verify(emailService).sendEmail(eq("user@example.com"),
                eq("You have been added to an event: Test Event"),
                contains("Dear user1,\n\nYou have been added to the event \"Test Event\". It will start on some day in future and end on some day in future.\n\nBest regards,\nYour Medieval Park Team"));
    }

    @Test
    void notifyUserOfAddedToEvent_ShouldSendEmailNotNullDate() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(LocalDate.now());
        event.setEventEndDate(LocalDate.now().plusDays(4));
        BlackCreekUser user = new BlackCreekUser();
        user.setEmail("user@example.com");
        user.setLogin("user1");

        eventNotificationService.notifyUserOfAddedToEvent(user, event);

        verify(emailService).sendEmail(eq("user@example.com"),
                eq("You have been added to an event: Test Event"),
                contains("Dear user1,\n\nYou have been added to the event \"Test Event\". " +
                        "It will start on " + event.getEventStartDate() + " and end on " +
                        event.getEventEndDate() + ".\n\nBest regards,\nYour Medieval Park Team"));
    }

    @Test
    void notifyUsersOfNewEvent_ShouldSendEmailsToAllUsersNullDate() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(null);
        event.setEventEndDate(null);
        BlackCreekUser user1 = new BlackCreekUser();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        BlackCreekUser user2 = new BlackCreekUser();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        List<BlackCreekUser> users = List.of(user1, user2);

        eventNotificationService.notifyUsersOfNewEvent(event, users);

        verify(emailService).sendEmail(eq("user1@example.com"),
                eq("New Event: Test Event"),
                contains("Dear user1,\n\nA new event \"Test Event\" has been created. It will start on some day in future and end on some day in future.\n\nBest regards,\nYour Medieval Park Team"));
        verify(emailService).sendEmail(eq("user2@example.com"),
                eq("New Event: Test Event"),
                contains("Dear user2,\n\nA new event \"Test Event\" has been created. It will start on some day in future and end on some day in future.\n\nBest regards,\nYour Medieval Park Team"));
    }

    @Test
    void notifyUsersOfNewEvent_ShouldSendEmailsToAllUsersNotNullDate() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(LocalDate.now());
        event.setEventEndDate(LocalDate.now().plusDays(4));
        BlackCreekUser user1 = new BlackCreekUser();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        BlackCreekUser user2 = new BlackCreekUser();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        List<BlackCreekUser> users = List.of(user1, user2);

        eventNotificationService.notifyUsersOfNewEvent(event, users);

        verify(emailService).sendEmail(eq("user1@example.com"),
                eq("New Event: Test Event"),
                contains("Dear user1,\n\nA new event \"Test Event\" has been created. It will start on " +
                        event.getEventStartDate() + " and end on " + event.getEventEndDate() +
                        ".\n\nBest regards,\nYour Medieval Park Team"));
        verify(emailService).sendEmail(eq("user2@example.com"),
                eq("New Event: Test Event"),
                contains("Dear user2,\n\nA new event \"Test Event\" has been created. It will start on " +
                        event.getEventStartDate() + " and end on " + event.getEventEndDate()
                        + ".\n\nBest regards,\nYour Medieval Park Team"));
    }

    @Test
    void notifyAttendeesWhenCloseEvent_ShouldThrowExceptionForNullEvent() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                eventNotificationService.notifyAttendeesWhenCloseEvent(null)
        );
        assertEquals("Event must not be null", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void notifyUserOfAddedToEvent_ShouldThrowExceptionForNullEvent() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                eventNotificationService.notifyUserOfAddedToEvent(new BlackCreekUser(), null)
        );
        assertEquals("Event must not be null", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void notifyUserOfAddedToEvent_ShouldThrowExceptionForNullUser() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                eventNotificationService.notifyUserOfAddedToEvent(null, new BlackCreekEvent())
        );
        assertEquals("User must not be null", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void notifyUsersOfNewEvent_ShouldThrowExceptionForNullEvent() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                eventNotificationService.notifyUsersOfNewEvent(null, List.of(new BlackCreekUser()))
        );
        assertEquals("Event must not be null", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
