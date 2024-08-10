package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;

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
    private BlackCreekUserRepository blackCreekUserRepository;
    @Mock
    private BlackCreekEventRepository blackCreekEventRepository;

    private BlackCreekUser blackCreekUser;
    private BlackCreekEvent blackCreekEvent;
    private BlackCreekEvent blackCreekEvent2;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    public void setUp() {
        blackCreekEvent = new BlackCreekEvent();
        blackCreekEvent.setEventId(1L);
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
    void saveEvent_NullEvent() {
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
    void indEventsBetween_NoEvents() {
        when(blackCreekEventRepository.findByEventStartDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());

        List<BlackCreekEvent> result = blackCreekEventService.findEventsBetween(startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(blackCreekEventRepository, times(1)).findByEventStartDateBetween(startDate, endDate);
    }

    @Test
    void indEventsBetween_InvalidDates() {
        LocalDate invalidEndDate = LocalDate.of(2023, 12, 31);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> blackCreekEventService.findEventsBetween(startDate, invalidEndDate));

        assertEquals("Start date must be before or equal to end date", exception.getMessage());
        verify(blackCreekEventRepository, times(0)).findByEventStartDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testFindEventsBetweenNullDates() {
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
    void addUserToEvent_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(null, 1L)
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.addUserToEvent(1L, null)
        );
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
    void setCostToEvent_ThrowsResourceNotFoundException_NotPositiveId() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.setCostToEvent(-1L, 12.5)
        );
    }

    @Test
    void setCostToEvent_ThrowsResourceNotFoundException_IdNull() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> blackCreekEventService.setCostToEvent(null, 12.5)
        );
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
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(blackCreekUser));
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);

        BlackCreekEvent result = blackCreekEventService.addAttendeeToEvent(eventId, userId);

        assertNotNull(result);
        assertTrue(result.getAttendees().contains(blackCreekUser));
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(1)).save(result);
    }

    @Test
    void addAttendeeToEvent_EventNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(blackCreekUser));

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.addAttendeeToEvent(eventId, userId));

        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(0)).save(any(BlackCreekEvent.class));
    }

    @Test
    void addAttendeeToEvent_UserNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.addAttendeeToEvent(eventId, userId));

        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(0)).save(any(BlackCreekEvent.class));
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
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(blackCreekUser));
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);

        BlackCreekEvent result = blackCreekEventService.removeAttendeeFromEvent(eventId, userId);

        assertNotNull(result);
        assertFalse(result.getAttendees().contains(blackCreekUser));
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(1)).save(result);
    }

    @Test
    void removeAttendeeFromEvent_EventNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(blackCreekUser));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.removeAttendeeFromEvent(eventId, userId));

        assertEquals("Event not found", exception.getMessage());
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(0)).save(any(BlackCreekEvent.class));
    }

    @Test
    void testRemoveAttendeeFromEventUserNotFound() {
        Long eventId = 1L;
        Long userId = 2L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.removeAttendeeFromEvent(eventId, userId));

        assertEquals("User not found", exception.getMessage());
        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(0)).save(any(BlackCreekEvent.class));
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
    public void searchEventByEventNameOrEventDescription_PositiveCase() {
        when(blackCreekEventRepository.searchEventByEventNameOrEventDescription("Medieval"))
                .thenReturn(Collections.singletonList(blackCreekEvent));

        List<BlackCreekEvent> events = blackCreekEventService.searchEvents("Medieval");

        assertThat(events).isNotNull();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).getEventName()).isEqualTo("Medieval Fair");
    }

    @Test
    public void cancelEvent_PositiveCase() {
        when(blackCreekEventRepository.findById(1L)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);
        BlackCreekEvent cancelledEvent = blackCreekEventService.cancelEvent(1L);
        assertThat(cancelledEvent.isCancelled()).isTrue();
        assertThat(cancelledEvent.getEventId()).isEqualTo(1L);
        verify(blackCreekEventRepository).save(cancelledEvent);
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
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(blackCreekUser));

        blackCreekEventService.setResponsibleUserToEvent(eventId, userId);

        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(1)).save(blackCreekEvent);

        assertEquals(blackCreekUser.getUserId(), blackCreekEvent.getResponsibleUserId());
    }

    @Test
    void setResponsibleUserToEvent_EventNotFound() {
        Long eventId = 1L;
        Long userId = 1L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(blackCreekUser));

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(eventId, userId));

        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(0)).save(blackCreekEvent);

        assertEquals(blackCreekUser.getUserId(), blackCreekEvent.getResponsibleUserId());
    }

    @Test
    void setResponsibleUserToEvent_UserNotFound() {
        Long eventId = 1L;
        Long userId = 1L;

        when(blackCreekEventRepository.findById(eventId)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(eventId, userId));

        verify(blackCreekEventRepository, times(1)).findById(eventId);
        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(blackCreekEventRepository, times(0)).save(any(BlackCreekEvent.class));

        assertNull(blackCreekEvent.getResponsibleUserId());
    }

    @Test
    void setResponsibleUserToEvent_InvalidIds() {
        Long invalidId = -1L;

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(invalidId, 2L));

        assertThrows(ResourceNotFoundException.class, () -> blackCreekEventService.setResponsibleUserToEvent(1L, invalidId));
    }

    @Test
    void addAttendeeToEvent_ShouldThrowException_WhenCapacityReached() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(3L);
        event.setAttendees(new HashSet<>());
        event.setEventName("event with capacity");
        event.setEventCapacity(2);

        BlackCreekUser user1 = new BlackCreekUser();
        user1.setLogin("user1");
        user1.setUserId(2L);
        BlackCreekUser user2 = new BlackCreekUser();
        user1.setLogin("user2");
        user1.setUserId(3L);
        BlackCreekUser user3 = new BlackCreekUser();
        user1.setLogin("user3");
        user1.setUserId(4L);

        event.getAttendees().add(user1);
        event.getAttendees().add(user2);

        when(blackCreekEventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(blackCreekUserRepository.findById(user3.getUserId())).thenReturn(Optional.of(user3));
        assertThrows(IllegalStateException.class, () -> blackCreekEventService.addAttendeeToEvent(event.getEventId(), user3.getUserId()));
    }
}
