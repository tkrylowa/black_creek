package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.repository.FeedbackRepository;
import ru.spring.tkrylova.blackcreek.servce.FeedbackService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FeedbackServiceTest {
    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    void getFeedbackByEventId_ShouldReturnFeedbackList() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(1L);
        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(2L);
        Feedback feedback1 = new Feedback(1L, event, user, "Feedback 1", 5, LocalDateTime.now());
        Feedback feedback2 = new Feedback(2L, event, user, "Feedback 2", 4, LocalDateTime.now());

        when(feedbackRepository.findByEventId(event.getEventId())).thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> feedbackList = feedbackService.getFeedbackByEventId(event.getEventId());

        assertNotNull(feedbackList, "The returned feedback list should not be null");
        assertEquals(2, feedbackList.size(), "The size of the feedback list should be 2");
        assertEquals(feedback1, feedbackList.get(0), "The first feedback should match");
        assertEquals(feedback2, feedbackList.get(1), "The second feedback should match");

        verify(feedbackRepository, times(1)).findByEventId(event.getEventId());
    }

    @Test
    void getFeedbackByEventId_ShouldReturnEmptyListWhenNoFeedback() {
        Long eventId = 1L;

        when(feedbackRepository.findByEventId(eventId)).thenReturn(new ArrayList<>());

        List<Feedback> feedbackList = feedbackService.getFeedbackByEventId(eventId);

        assertNotNull(feedbackList, "The returned feedback list should not be null");
        assertTrue(feedbackList.isEmpty(), "The feedback list should be empty when no feedback is found");

        verify(feedbackRepository, times(1)).findByEventId(eventId);
    }

    @Test
    void saveFeedback_ShouldInvokeRepositorySave() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventId(1L);
        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(2L);
        Feedback feedback = new Feedback(1L, event, user, "Great event!", 5, LocalDateTime.now());

        feedbackService.saveFeedback(feedback);

        verify(feedbackRepository, times(1)).save(feedback);
    }

    @Test
    void getFeedbackByEventId_ShouldThrowException_WhenEventIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.getFeedbackByEventId(null)
        );
        assertEquals("Feedback is null or illegal!", exception.getMessage());

        verify(feedbackRepository, never()).findByEventId(null);
    }

    @Test
    void testGetFeedbackByEventId_ShouldThrowException_WhenEventIdIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.getFeedbackByEventId(-1L)
        );
        assertEquals("Feedback is null or illegal!", exception.getMessage());

        verify(feedbackRepository, never()).findByEventId(-1L);
    }

    @Test
    void saveFeedback_ShouldThrowException_WhenFeedbackIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.saveFeedback(null)
        );
        assertEquals("Feedback is null!", exception.getMessage());
    }

}
