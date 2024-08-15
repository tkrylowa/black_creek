package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;
import ru.spring.tkrylova.blackcreek.servce.EmailService;
import ru.spring.tkrylova.blackcreek.servce.EventNotificationScheduler;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class NotificationServiceTest {
    @InjectMocks
    private EventNotificationScheduler eventNotificationScheduler;

    @Mock
    private EmailService emailService;
    @Mock
    private BlackCreekUserService blackCreekUserService;
    @Mock
    private BlackCreekEventService blackCreekEventService;

    private BlackCreekEvent event1;
    private BlackCreekEvent event2;
    private BlackCreekUser user1;
    private BlackCreekUser user2;

    @BeforeEach
    void setUp() {
        event1 = new BlackCreekEvent();
        event1.setEventName("Event 1");
        event1.setEventStartDate(LocalDate.now().plusDays(1));
        event1.setEventEndDate(LocalDate.now().plusDays(3));
        event2 = new BlackCreekEvent();
        event2.setEventStartDate(LocalDate.now().plusDays(1));
        event2.setEventEndDate(LocalDate.now().plusDays(3));
        event2.setEventName("Event 2");

        user1 = new BlackCreekUser();
        user1.setUserId(1L);
        user1.setLogin("user1");
        user1.setEmail("user1@example.com");

        user2 = new BlackCreekUser();
        user2.setLogin("user2");
        user2.setEmail("user2@example.com");

        event1.setUsers(Set.of(user1, user2));
        event1.setResponsibleUserId(1L);
    }

    @Test
    void sendUpcomingEventNotifications_ThrowResourceNotFoundException_WhenResponsibleUserNotFound() {
        LocalDate now = LocalDate.now();
        LocalDate upcoming = now.plusDays(1);
        List<BlackCreekEvent> upcomingEvents = Arrays.asList(event1, event2);
        when(blackCreekEventService.findEventsBetween(now, upcoming)).thenReturn(upcomingEvents);
        Exception exceptionEndNull = assertThrows(ResourceNotFoundException.class, () -> eventNotificationScheduler.sendUpcomingEventNotifications());
        assertEquals("User with id 1 not found!", exceptionEndNull.getMessage());
    }

    @Test
    void sendUpcomingEventNotifications_Success() {
        Long respId = 1L;
        LocalDate now = LocalDate.now();
        LocalDate upcoming = now.plusDays(1);
        event1.setResponsibleUserId(respId);
        event2.setResponsibleUserId(respId);
        List<BlackCreekEvent> upcomingEvents = Arrays.asList(event1, event2);
        when(blackCreekEventService.findEventsBetween(now, upcoming)).thenReturn(upcomingEvents);
        when(blackCreekUserService.findUserById(respId)).thenReturn(user1);
        eventNotificationScheduler.sendUpcomingEventNotifications();
    }

    @Test
    void sendUpcomingEventNotifications_NoEvents() {
        LocalDate now = LocalDate.now();
        LocalDate upcoming = now.plusDays(1);
        when(blackCreekEventService.findEventsBetween(now, upcoming)).thenReturn(Collections.emptyList());

        eventNotificationScheduler.sendUpcomingEventNotifications();
    }

    @Test
    void notifyUsersOfUpcomingEvent_Success() {
        BlackCreekUser responsibleUser = new BlackCreekUser();
        responsibleUser.setLogin("responsibleUser");
        responsibleUser.setEmail("responsible@example.com");
        when(blackCreekUserService.findUserById(event1.getResponsibleUserId())).thenReturn(responsibleUser);
        eventNotificationScheduler.notifyUsersOfUpcomingEvent(event1);

        verify(emailService).sendEmail(eq(user1.getEmail()), anyString(), contains("Dear " + user1.getLogin()));
        verify(emailService).sendEmail(eq(user2.getEmail()), anyString(), contains("Dear " + user2.getLogin()));
        verify(emailService).sendEmail(eq(responsibleUser.getEmail()), anyString(), contains("Dear " + responsibleUser.getLogin()));
    }

    @Test
    void notifyUsersOfUpcomingEvent_NullEvent() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> eventNotificationScheduler.notifyUsersOfUpcomingEvent(null));
        assertEquals("Event must not be null", thrown.getMessage());
    }

    @Test
    void notifyUsersOfUpcomingEvent_EventNoUsers() {
        event1.setUsers(null);
        event1.setResponsibleUserId(user1.getUserId());
        when(blackCreekUserService.findUserById(user1.getUserId())).thenReturn(user1);
        eventNotificationScheduler.notifyUsersOfUpcomingEvent(event1);
        verify(emailService, times(1)).sendEmail(
                eq("user1@example.com"),
                eq("You are Responsible for an Upcoming Event: Event 1"),
                contains("This is a reminder that you are responsible for the upcoming event \"Event 1\"")
        );
        verify(blackCreekUserService, times(1)).findUserById(1L);
    }

    @Test
    void notifyUsersOfUpcomingEvent_EventNoResponsibleUsers() {
        event1.setUsers(Set.of(user1));
        event1.setResponsibleUserId(null);

        eventNotificationScheduler.notifyUsersOfUpcomingEvent(event1);
        verify(emailService, times(1)).sendEmail(
                eq("user1@example.com"),
                eq("Upcoming Event: 2024-08-16"),
                contains("""
                        Dear user1,

                        This is a reminder for the upcoming event "Event 1\"""")
        );
        verify(blackCreekUserService, times(0)).findUserById(user1.getUserId());
    }

    @Test
    void notifyUsersOfUpcomingEvent_EventNoResponsibleUsersNoUsers() {
        event1.setUsers(new HashSet<>());
        event1.setResponsibleUserId(null);

        eventNotificationScheduler.notifyUsersOfUpcomingEvent(event1);

        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void notifyUsersOfUpcomingEvent_NoUsers_ShouldNotSendEmail() {
        BlackCreekUser responsibleUser = new BlackCreekUser();
        responsibleUser.setUserId(2L);
        responsibleUser.setLogin("responsibleUser");

        when(blackCreekUserService.findUserById(2L)).thenReturn(responsibleUser);

        event1.setUsers(new HashSet<>());
        event1.setResponsibleUserId(2L);

        eventNotificationScheduler.notifyUsersOfUpcomingEvent(event1);

        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
        verify(blackCreekUserService, times(1)).findUserById(2L);
    }
}
