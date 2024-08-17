package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;
import ru.spring.tkrylova.blackcreek.servce.EventNotificationService;
import ru.spring.tkrylova.blackcreek.servce.FeedbackService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BlackCreekEventServiceTest {
    @InjectMocks
    private BlackCreekEventService blackCreekEventService;
    @Mock
    private BlackCreekUserService blackCreekUserService;
    @Mock
    private BlackCreekEventRepository blackCreekEventRepository;
    @Mock
    private EventNotificationService eventNotificationService;
    @Mock
    private FeedbackService feedbackService;

    private BlackCreekUser blackCreekUser;
    private BlackCreekEvent blackCreekEvent;
    private BlackCreekEvent blackCreekEvent2;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    public void setUp() {
        blackCreekEvent = new BlackCreekEvent();
        blackCreekEvent.setEventId(1L);
        blackCreekEvent.setEventCapacity(10);
        blackCreekEvent.setAttendees(new HashSet<>());
        blackCreekEvent.setEventName("Medieval Fair");

        blackCreekUser = new BlackCreekUser();
        blackCreekUser.setUserId(1L);
        blackCreekUser.setLogin("john_doe");
        blackCreekEvent.getAttendees().add(blackCreekUser);

        blackCreekEvent2 = new BlackCreekEvent();

        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
    }

    @Test
    void getAllEvents_Success() {
        List<BlackCreekEvent> events = Arrays.asList(blackCreekEvent, blackCreekEvent2);

        when(blackCreekEventRepository.findAll()).thenReturn(events);

        List<BlackCreekEvent> result = blackCreekEventService.getAllEvents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(blackCreekEvent));
        assertTrue(result.contains(blackCreekEvent2));
        verify(blackCreekEventRepository, times(1)).findAll();
    }

    @Test
    void getAllEvents_NoEvents() {
        when(blackCreekEventRepository.findAll()).thenReturn(Collections.emptyList());

        List<BlackCreekEvent> result = blackCreekEventService.getAllEvents();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(blackCreekEventRepository, times(1)).findAll();
    }

    @Test
    void saveEvent_Success() {
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);

        BlackCreekEvent result = blackCreekEventService.saveEvent(blackCreekEvent);

        assertNotNull(result);
        assertEquals(blackCreekEvent, result);
        verify(blackCreekEventRepository, times(1)).save(blackCreekEvent);
    }

    @Test
    void saveEvent_ShouldThrowException_WhenNullEvent() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                blackCreekEventService.saveEvent(null));

        assertEquals("Event must not be null", exception.getMessage());
        verify(blackCreekEventRepository, times(0)).save(any(BlackCreekEvent.class));
    }

    @Test
    void findEventsBetween_Success() {
        List<BlackCreekEvent> events = Arrays.asList(blackCreekEvent, blackCreekEvent2);

        when(blackCreekEventRepository.findByEventStartDateBetween(startDate, endDate)).thenReturn(events);

        List<BlackCreekEvent> result = blackCreekEventService.findEventsBetween(startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(blackCreekEvent));
        assertTrue(result.contains(blackCreekEvent2));
        verify(blackCreekEventRepository, times(1)).findByEventStartDateBetween(startDate, endDate);
    }

    @Test
    void findEventsBetween_ShouldThrowException_WhenNoEvents() {
        when(blackCreekEventRepository.findByEventStartDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());

        List<BlackCreekEvent> result = blackCreekEventService.findEventsBetween(startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(blackCreekEventRepository, times(1)).findByEventStartDateBetween(startDate, endDate);
    }

    @Test
    void findEventsBetween_ShouldThrowException_WhenInvalidDates() {
        LocalDate invalidEndDate = LocalDate.of(2023, 12, 31);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> blackCreekEventService.findEventsBetween(startDate, invalidEndDate));

        assertEquals("Start date must be before or equal to end date", exception.getMessage());
        verify(blackCreekEventRepository, times(0)).findByEventStartDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void findEventsBetween_ShouldThrowException_WhenNullDates() {
        Exception exceptionStartNull = assertThrows(IllegalArgumentException.class, () -> blackCreekEventService.findEventsBetween(null, endDate));

        assertEquals("Start date and end date must not be null", exceptionStartNull.getMessage());

        Exception exceptionEndNull = assertThrows(IllegalArgumentException.class, () -> blackCreekEventService.findEventsBetween(startDate, null));

        assertEquals("Start date and end date must not be null", exceptionEndNull.getMessage());

        verify(blackCreekEventRepository, times(0)).findByEventStartDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void addUserToEvent_ThrowsResourceNotFoundException_NotPositiveId() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(-1L, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(1L, -1L)
        );
    }

    @Test
    void setResponsibleUserToEvent_ThrowsResourceNotFoundException_NotPositiveId() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.setResponsibleUserToEvent(-1L, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.setResponsibleUserToEvent(1L, -1L)
        );
    }

    @Test
    void addUserToEvent_ThrowsResourceNotFoundException_EventIdNull() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(null, 1L)
        );
        assertEquals("Event id is null", exception.getMessage(),
                "Exception message should be 'Event id is null'");

        verify(blackCreekEventRepository, never()).findById(any());
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void addUserToEvent_ThrowsResourceNotFoundException_UserIdNull() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(1L, null)
        );
        assertEquals("User id is null", exception.getMessage(),
                "Exception message should be 'User id is null'");
        verify(blackCreekEventRepository, never()).findById(any());
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void addUserToEvent_ThrowsResourceNotFoundException_EventIdNegative() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(-1L, 1L)
        );
        assertEquals("Event id is invalid", exception.getMessage(),
                "Exception message should be 'Event id is invalid'");

        verify(blackCreekEventRepository, never()).findById(any());
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void addUserToEvent_ThrowsResourceNotFoundException_UserIdNegative() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(1L, -1L)
        );
        assertEquals("User id is invalid", exception.getMessage(),
                "Exception message should be 'User id is invalid'");
        verify(blackCreekEventRepository, never()).findById(any());
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void addUserToEvent_ShouldAddUser_WhenEventIdAndUserIdAreValid() {
        Long eventId = 1L;
        Long userId = 1L;

        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(eventId);
        event.setEventCapacity(10);
        event.setAttendees(new HashSet<>());

        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(userId);

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(blackCreekUserService.findUserById(userId)).thenReturn(user);
        when(blackCreekEventRepository.save(event)).thenReturn(event);

        BlackCreekEvent updatedEvent = blackCreekEventService.addUserToEvent(eventId, userId);

        assertNotNull(updatedEvent, "The updated event should not be null");
        assertTrue(updatedEvent.getUsers().contains(user), "The user should be added to the event");
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, times(1)).findUserById(userId);
        verify(blackCreekEventRepository, times(1)).save(event);
    }

    @Test
    void addUserToEvent_ShouldThrowException_WhenEventIsAtFullCapacity() {
        Long eventId = 1L;
        Long userId = 1L;

        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(eventId);
        event.setEventCapacity(1);
        Set<BlackCreekUser> attendees = new HashSet<>();
        attendees.add(new BlackCreekUser());
        event.setAttendees(attendees);

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(event));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                blackCreekEventService.addUserToEvent(eventId, userId)
        );
        assertEquals("Event has reached its maximum capacity.", exception.getMessage(), "Exception message should be 'Event has reached its maximum capacity.'");
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void addUserToEvent_ShouldThrowException_WhenEventNotFound() {
        Long eventId = 1L;
        Long userId = 1L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.addUserToEvent(eventId, userId)
        );
        assertEquals("Event not found", exception.getMessage(), "Exception message should be 'Event not found'");
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void addUserToEvent_ShouldThrowException_WhenUserNotFound() {
        Long eventId = 1L;
        Long userId = 1L;

        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(eventId);
        event.setEventCapacity(10);
        event.setAttendees(new HashSet<>());

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(blackCreekUserService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.addUserToEvent(eventId, userId)
        );
        assertEquals("User not found", exception.getMessage(), "Exception message should be 'Event not found'");
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, times(1)).findUserById(userId);
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void setResponsibleUserToEvent_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.setResponsibleUserToEvent(null, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.setResponsibleUserToEvent(1L, null)
        );
    }

    @Test
    void setCostToEvent_ShouldSetCost_WhenEventIdAndCostAreValid() {
        Long eventId = 1L;
        Double cost = 50.0;
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(eventId);

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(blackCreekEventRepository.save(event)).thenReturn(event);

        BlackCreekEvent updatedEvent = blackCreekEventService.setCostToEvent(eventId, cost);

        assertNotNull(updatedEvent, "The updated event should not be null");
        assertEquals(cost, updatedEvent.getCost(), "The event cost should be updated correctly");
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekEventRepository, times(1)).save(event);
    }

    @Test
    void setCostToEvent_ShouldThrowException_WhenEventIdIsNull() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.setCostToEvent(null, 50.0)
        );
        assertEquals("Event id is null", exception.getMessage(), "Exception message should be 'Event ID must be a positive number'");
        verify(blackCreekEventRepository, never()).findById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void setCostToEvent_ShouldThrowException_WhenEventIdIsNegative() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.setCostToEvent(-1L, 50.0)
        );
        assertEquals("Event id is invalid", exception.getMessage(), "Exception message should be 'Event ID must be a positive number'");
        verify(blackCreekEventRepository, never()).findById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void setCostToEvent_ShouldThrowException_WhenEventNotFound() {
        Long eventId = 1L;
        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.setCostToEvent(eventId, 50.00)
        );
        assertEquals("Event not found", exception.getMessage());
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void setCostToEvent_ShouldSetCostToNull_WhenCostIsNull() {
        Long eventId = 1L;
        Double cost = null;
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(eventId);

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(blackCreekEventRepository.save(event)).thenReturn(event);

        BlackCreekEvent updatedEvent = blackCreekEventService.setCostToEvent(eventId, cost);

        assertNotNull(updatedEvent, "The updated event should not be null");
        assertNull(updatedEvent.getCost(), "The event cost should be set to null");
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekEventRepository, times(1)).save(event);
    }

    @Test
    void addAttendeeToEvent_ThrowsResourceNotFoundException_NotPositiveId() {
        Long invalidId = -1L;
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addAttendeeToEvent(invalidId, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addAttendeeToEvent(1L, invalidId)
        );
    }

    @Test
    void addAttendeeToEvent_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addAttendeeToEvent(null, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addAttendeeToEvent(1L, null)
        );
    }

    @Test
    void addAttendeeToEvent_Success() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserService.findUserById(userId)).thenReturn(blackCreekUser);
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);

        BlackCreekEvent result = blackCreekEventService.addAttendeeToEvent(eventId, userId);

        assertNotNull(result);
        assertTrue(result.getAttendees().contains(blackCreekUser));
    }

    @Test
    void addAttendeeToEvent_ShouldThrowException_WhenEventNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(blackCreekUserService.findUserById(userId)).thenReturn(blackCreekUser);

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.addAttendeeToEvent(eventId, userId));
    }

    @Test
    void addAttendeeToEvent_ShouldThrowException_WhenUserNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.addAttendeeToEvent(eventId, userId));
    }

    @Test
    void addAttendeeToEvent_ShouldThrowException_WhenEventIsAtFullCapacity() {
        Long eventId = 1L;
        Long userId = 1L;

        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(eventId);
        event.setEventCapacity(1);
        event.setAttendees(new HashSet<>());
        event.getAttendees().add(new BlackCreekUser());

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(event));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                blackCreekEventService.addAttendeeToEvent(eventId, userId)
        );
        assertEquals("Event has reached its maximum capacity.", exception.getMessage());
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, never()).findUserById(any());
        verify(blackCreekEventRepository, never()).save(any());
    }

    @Test
    void removeAttendeeFromEvent_ThrowsResourceNotFoundException_NotPositiveId() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.removeAttendeeFromEvent(-1L, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.removeAttendeeFromEvent(1L, -1L)
        );
    }

    @Test
    void removeAttendeeFromEvent_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.removeAttendeeFromEvent(null, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.removeAttendeeFromEvent(1L, null)
        );
    }

    @Test
    void removeAttendeeFromEvent_Success() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserService.findUserById(userId)).thenReturn(blackCreekUser);
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);

        BlackCreekEvent result = blackCreekEventService.removeAttendeeFromEvent(eventId, userId);

        assertNotNull(result);
        assertFalse(result.getAttendees().contains(blackCreekUser));
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, times(1)).findUserById(userId);
        verify(blackCreekEventRepository, times(1)).save(result);
    }

    @Test
    void removeAttendeeFromEvent_ShouldThrowException_WhenEventNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(blackCreekUserService.findUserById(userId)).thenReturn(blackCreekUser);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.removeAttendeeFromEvent(eventId, userId));

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void removeAttendeeFromEventUser_ShouldThrowException_WhenNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.removeAttendeeFromEvent(eventId, userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void isUserAttending_ThrowsResourceNotFoundException_NotPositiveId() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.isUserAttending(-1L, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.isUserAttending(1L, -1L)
        );
    }

    @Test
    void isUserAttending_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.isUserAttending(null, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.isUserAttending(1L, null)
        );
    }

    @Test
    void searchEventByEventNameOrEventDescription_PositiveCase() {
        when(blackCreekEventRepository.searchEventByEventNameOrEventDescription("Medieval"))
                .thenReturn(Collections.singletonList(blackCreekEvent));

        List<BlackCreekEvent> events = blackCreekEventService.searchEvents("Medieval");

        assertThat(events).isNotNull();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).getEventName()).isEqualTo("Medieval Fair");
    }

    @Test
    void cancelEvent_ShouldCancelEventAndNotifyAttendees() {
        blackCreekEvent.setEventStartDate(LocalDate.now());
        blackCreekUser.setEmail("user1@example.com");
        blackCreekEvent.setAttendees(Set.of(blackCreekUser));
        when(blackCreekEventRepository.findById(1L)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);
        BlackCreekEvent cancelledEvent = blackCreekEventService.cancelEvent(1L);
        assertThat(cancelledEvent.isCancelled()).isTrue();
        assertThat(cancelledEvent.getEventId()).isEqualTo(1L);
        verify(blackCreekEventRepository).save(cancelledEvent);
        verify(blackCreekEventRepository, times(1)).save(cancelledEvent);
        verify(eventNotificationService, times(1))
                .notifyAttendeesWhenCloseEvent(blackCreekEvent);
    }

    @Test
    void cancelEvent_ShouldThrowException_WhenEventIdIsNull() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.cancelEvent(null));
        assertEquals("Event id is null", exception.getMessage());
    }

    @Test
    void cancelEvent_ShouldThrowException_WhenEventIdIsNegative() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.cancelEvent(-1L));
        assertEquals("Event id is invalid", exception.getMessage());
    }

    @Test
    void cancelEvent_ThrowResourceNotFoundException_WhenEventNotFound() {
        Long eventId = 1L;
        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.cancelEvent(eventId));
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void addFeedback_ThrowsResourceNotFoundException_NotPositiveId() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addFeedback(-1L, 1L, "some comment", 1)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addFeedback(1L, -1L, "some comment", 1)
        );
    }

    @Test
    void addFeedback_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addFeedback(null, 1L, "some comment", 1)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addFeedback(1L, null, "some comment", 1)
        );
    }

    @Test
    void setResponsibleUserToEvent_PositiveCase() {
        Long eventId = 1L;
        Long userId = 1L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserService.findUserById(userId)).thenReturn(blackCreekUser);

        blackCreekEventService.setResponsibleUserToEvent(eventId, userId);

        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserService, times(1)).findUserById(userId);
        verify(blackCreekEventRepository, times(1)).save(blackCreekEvent);

        assertEquals(blackCreekUser.getUserId(), blackCreekEvent.getResponsibleUserId());
    }

    @Test
    void setResponsibleUserToEvent_ThrowResourceNotFoundException_WhenEventNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(blackCreekUserService.findUserById(userId)).thenReturn(blackCreekUser);

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(eventId, userId));
    }

    @Test
    void setResponsibleUserToEvent_ShouldThrowException_WhenUserNotFound() {
        Long eventId = 1L;
        Long userId = 1L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(eventId, userId));
        assertNull(blackCreekEvent.getResponsibleUserId());
    }

    @Test
    void setResponsibleUserToEvent_ShouldThrowException_WhenInvalidIds() {
        Long invalidId = -1L;

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(invalidId, 2L));
        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(1L, invalidId));
    }

    @Test
    void createEvent_ShouldSaveEventAndNotifyUsers() {
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);
        when(blackCreekUserService.findAllUsers()).thenReturn(Collections.singletonList(blackCreekUser));

        BlackCreekEvent result = blackCreekEventService.createEvent(blackCreekEvent);

        assertNotNull(result);
        assertEquals(blackCreekEvent.getEventName(), result.getEventName());
        assertEquals(blackCreekEvent, result);

        verify(blackCreekEventRepository).save(blackCreekEvent);
        verify(eventNotificationService).notifyUsersOfNewEvent(eq(blackCreekEvent), eq(Collections.singletonList(blackCreekUser)));
    }

    @Test
    void createEvent_ShouldHandleEventSaveFailure() {
        when(blackCreekEventRepository.save(blackCreekEvent)).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                blackCreekEventService.createEvent(blackCreekEvent)
        );
        assertEquals("Database error", thrown.getMessage());

        verify(blackCreekEventRepository).save(blackCreekEvent);
        verify(eventNotificationService, never()).notifyUsersOfNewEvent(any(), any());
    }

    @Test
    void createEvent_ShouldHandleNoUsers() {
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);
        when(blackCreekUserService.findAllUsers()).thenReturn(List.of());

        BlackCreekEvent result = blackCreekEventService.createEvent(blackCreekEvent);

        assertNotNull(result);
        assertEquals(blackCreekEvent.getEventName(), result.getEventName());
        assertEquals(blackCreekEvent, result);

        verify(blackCreekEventRepository).save(blackCreekEvent);
        verify(eventNotificationService).notifyUsersOfNewEvent(eq(blackCreekEvent), eq(List.of()));
    }

    @Test
    void createEvent_ShouldHandleNullEvent() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                blackCreekEventService.createEvent(null)
        );
        assertEquals("Event must not be null", thrown.getMessage());

        verify(blackCreekEventRepository, never()).save(any());
        verify(eventNotificationService, never()).notifyUsersOfNewEvent(any(), any());
    }

    @Test
    void isUserAttending_ShouldReturnTrueWhenUserIsAttending() {
        Long eventId = 1L;
        Long userId = 123L;
        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(userId);

        Set<BlackCreekUser> attendees = new HashSet<>();
        attendees.add(user);

        BlackCreekEvent event = new BlackCreekEvent();
        event.setAttendees(attendees);

        when(blackCreekEventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event));

        boolean result = blackCreekEventService.isUserAttending(eventId, userId);

        assertTrue(result);
    }

    @Test
    void isUserAttending_ShouldReturnFalseWhenUserIsNotAttending() {
        Long eventId = 1L;
        Long userId = 123L;
        Long otherUserId = 456L;
        BlackCreekUser otherUser = new BlackCreekUser();
        otherUser.setUserId(otherUserId);

        Set<BlackCreekUser> attendees = new HashSet<>();
        attendees.add(otherUser);

        BlackCreekEvent event = new BlackCreekEvent();
        event.setAttendees(attendees);

        when(blackCreekEventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event));

        boolean result = blackCreekEventService.isUserAttending(eventId, userId);

        assertFalse(result);
    }

    @Test
    void isUserAttending_ShouldThrowExceptionForInvalidEventId() {
        Long invalidEventId = -1L;
        Long userId = 123L;

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.isUserAttending(invalidEventId, userId)
        );

        assertEquals("Event id is invalid", exception.getMessage());
        verify(blackCreekEventRepository, never()).findById(anyLong());
    }

    @Test
    void isUserAttending_ShouldThrowExceptionForInvalidUserId() {
        Long eventId = 1L;
        Long invalidUserId = -1L;

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.isUserAttending(eventId, invalidUserId)
        );

        assertEquals("User id is invalid", exception.getMessage());
        verify(blackCreekEventRepository, never()).findById(anyLong());
    }

    @Test
    void isUserAttending_ShouldThrowExceptionForNullEventId() {
        Long userId = 123L;

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.isUserAttending(null, userId)
        );

        assertEquals("Event id is null", exception.getMessage());
        verify(blackCreekEventRepository, never()).findById(anyLong());
    }

    @Test
    void isUserAttending_ShouldThrowExceptionForNullUserId() {
        Long eventId = 1L;

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.isUserAttending(eventId, null)
        );

        assertEquals("User id is null", exception.getMessage());
        verify(blackCreekEventRepository, never()).findById(anyLong());
    }

    @Test
    void isUserAttending_ShouldReturnFalseWhenEventNotFound() {
        Long eventId = 1L;
        Long userId = 123L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.isUserAttending(eventId, userId)
        );
        assertEquals("Event not found", exception.getMessage(), "Exception message should be 'Event not found'");
    }

    @Test
    void getFeedbackForEvent_ValidEventId_ShouldReturnFeedbackList() {
        Long eventId = 1L;
        Feedback feedback = new Feedback();
        List<Feedback> expectedFeedback = Collections.singletonList(feedback);
        when(feedbackService.getFeedbackByEventId(eventId)).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = blackCreekEventService.getFeedbackForEvent(eventId);

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackService, times(1)).getFeedbackByEventId(eventId);
    }

    @Test
    void getFeedbackForEvent_NullEventId_ShouldThrowException() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.getFeedbackForEvent(null)
        );
        assertEquals("Event id is null", exception.getMessage());
        verify(feedbackService, never()).getFeedbackByEventId(anyLong());
    }

    @Test
    void getFeedbackForEvent_NegativeEventId_ShouldThrowException() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                blackCreekEventService.getFeedbackForEvent(-1L)
        );
        assertEquals("Event id is invalid", exception.getMessage());
        verify(feedbackService, never()).getFeedbackByEventId(anyLong());
    }

    @Test
    void getFeedbackForEvent_ShouldReturnEmptyListWhenNoFeedback() {
        Long eventId = 1L;
        when(feedbackService.getFeedbackByEventId(eventId)).thenReturn(Collections.emptyList());

        List<Feedback> actualFeedback = blackCreekEventService.getFeedbackForEvent(eventId);

        assertTrue(actualFeedback.isEmpty());
        verify(feedbackService).getFeedbackByEventId(eventId);
    }
}
