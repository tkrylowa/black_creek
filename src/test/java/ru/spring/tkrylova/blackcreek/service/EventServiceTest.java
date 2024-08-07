package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekEventService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventServiceTest {
    @InjectMocks
    private BlackCreekEventService blackCreekEventService;
    @Mock
    private BlackCreekUserRepository blackCreekUserRepository;
    @Mock
    private BlackCreekEventRepository blackCreekEventRepository;

    private BlackCreekUser blackCreekUser;
    private BlackCreekEvent blackCreekEvent;

    @BeforeEach
    public void setUp() {
        blackCreekEvent = new BlackCreekEvent();
        blackCreekEvent.setEventId(1L);
        blackCreekEvent.setEventName("Medieval Fair");

        blackCreekUser = new BlackCreekUser();
        blackCreekUser.setUserId(1L);
        blackCreekUser.setLogin("john_doe");
    }

    @Test
    void addUserToEvent_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.addUserToEvent(-1L, 1L)
        );
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.addUserToEvent(1L, -1L)
        );
    }

    @Test
    void setResponsibleUserToEvent_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.setResponsibleUserToEvent(-1L, 1L)
        );
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.setResponsibleUserToEvent(1L, -1L)
        );
    }

    @Test
    void setCostToEvent_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.setCostToEvent(-1L, 12.5)
        );
    }

    @Test
    void markAttendance_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.markAttendance(-1L, "Non empty")
        );
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.markAttendance(1L, null)
        );
    }

    @Test
    void unmarkAttendance_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.unmarkAttendance(-1L, "Non empty")
        );
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.unmarkAttendance(1L, null)
        );
    }

    @Test
    void isUserAttending_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.isUserAttending(-1L, 1L)
        );
        Assertions.assertThrows(
                RuntimeException.class,
                () -> blackCreekEventService.isUserAttending(1L, -1L)
        );
    }

    @Test
    public void testSearchEvents() {
        when(blackCreekEventRepository.searchEventByEventNameOrEventDescription("Medieval"))
                .thenReturn(Collections.singletonList(blackCreekEvent));

        List<BlackCreekEvent> events = blackCreekEventService.searchEvents("Medieval");

        assertThat(events).isNotNull();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).getEventName()).isEqualTo("Medieval Fair");
    }

    @Test
    public void testCancelEvent() {
        when(blackCreekEventRepository.findById(1L)).thenReturn(Optional.of(blackCreekEvent));
        when(blackCreekEventRepository.save(blackCreekEvent)).thenReturn(blackCreekEvent);
        BlackCreekEvent cancelledEvent = blackCreekEventService.cancelEvent(1L);
        assertThat(cancelledEvent.isCancelled()).isTrue();
        assertThat(cancelledEvent.getEventId()).isEqualTo(1L);
        verify(blackCreekEventRepository).save(cancelledEvent);
    }
}
