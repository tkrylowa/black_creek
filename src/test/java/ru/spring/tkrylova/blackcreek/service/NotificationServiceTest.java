package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
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
        event2 = new BlackCreekEvent();
        event2.setEventName("Event 2");

        user1 = new BlackCreekUser();
        user1.setLogin("user1");
        user1.setEmail("user1@example.com");

        user2 = new BlackCreekUser();
        user2.setLogin("user2");
        user2.setEmail("user2@example.com");

        event1.setUsers(Set.of(user1, user2));
        event1.setResponsibleUserId(1L);
    }

    @Test
    void sendUpcomingEventNotifications_Success() {
        LocalDate now = LocalDate.now();
        LocalDate upcoming = now.plusDays(1);
        List<BlackCreekEvent> upcomingEvents = Arrays.asList(event1, event2);
        when(blackCreekEventService.findEventsBetween(now, upcoming)).thenReturn(upcomingEvents);

        eventNotificationScheduler.sendUpcomingEventNotifications();

        verify(blackCreekEventService, times(1)).findEventsBetween(now, upcoming);
        verify(eventNotificationScheduler, times(1)).notifyUsersOfUpcomingEvent(event1);
        verify(eventNotificationScheduler, times(1)).notifyUsersOfUpcomingEvent(event2);
    }

    @Test
    void sendUpcomingEventNotifications_NoEvents() {
        LocalDate now = LocalDate.now();
        LocalDate upcoming = now.plusDays(1);
        when(blackCreekEventService.findEventsBetween(now, upcoming)).thenReturn(Collections.emptyList());

        eventNotificationScheduler.sendUpcomingEventNotifications();

        verify(blackCreekEventService, times(1)).findEventsBetween(now, upcoming);
        verify(eventNotificationScheduler, times(0)).notifyUsersOfUpcomingEvent(any(BlackCreekEvent.class));
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
        event1.setUsers(new HashSet<>());
        eventNotificationScheduler.notifyUsersOfUpcomingEvent(event1);

        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
        verify(blackCreekUserService, times(1)).findUserById(event1.getResponsibleUserId());
    }
}
