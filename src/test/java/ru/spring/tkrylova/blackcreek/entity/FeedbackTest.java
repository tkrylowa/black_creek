package ru.spring.tkrylova.blackcreek.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.repository.FeedbackRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FeedbackTest {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private BlackCreekEventRepository blackCreekEventRepository;

    @Autowired
    private BlackCreekUserRepository blackCreekUserRepository;

    private BlackCreekEvent event;
    private BlackCreekUser user;

    @BeforeEach
    public void setup() {
        blackCreekEventRepository.deleteAllInBatch();
        event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(5));
        event.setEventEndDate(LocalDate.now().plusDays(7));
        event.setEventCapacity(50);
        event = blackCreekEventRepository.save(event);

        blackCreekUserRepository.deleteAllInBatch();
        user = new BlackCreekUser();
        user.setLogin(randomUUID().toString());
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail(randomUUID() + "@mail.com");
        user.setPassword("Password123!");
        user.setConfirmPassword("Password123!");
        user = blackCreekUserRepository.save(user);
    }

    @Test
    public void feedbackEntity_Fields() {
        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setComments("Amazing event!");
        feedback.setRating(5);
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        assertThat(savedFeedback.getFeedbackId()).isNotNull();
        assertThat(savedFeedback.getEvent()).isEqualTo(event);
        assertThat(savedFeedback.getUser()).isEqualTo(user);
        assertThat(savedFeedback.getComments()).isEqualTo("Amazing event!");
        assertThat(savedFeedback.getRating()).isEqualTo(5);
        assertThat(savedFeedback.getCreatedAt()).isNotNull();
    }

    @Test
    public void feedbackEntity_Relationships() {
        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setComments("Good organization.");
        feedback.setRating(4);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        Feedback retrievedFeedback = feedbackRepository.findById(savedFeedback.getFeedbackId()).orElse(null);

        assertThat(retrievedFeedback).isNotNull();
        assertThat(retrievedFeedback.getEvent()).isEqualTo(event);
        assertThat(retrievedFeedback.getUser()).isEqualTo(user);
    }

    @Test
    public void feedbackEntity_Persistence() {
        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setComments("Fantastic medieval experience!");
        feedback.setRating(5);
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        Feedback persistedFeedback = feedbackRepository.findById(savedFeedback.getFeedbackId()).orElse(null);

        assertThat(persistedFeedback).isNotNull();
        assertThat(persistedFeedback.getFeedbackId()).isEqualTo(savedFeedback.getFeedbackId());
        assertThat(persistedFeedback.getComments()).isEqualTo("Fantastic medieval experience!");
    }
}
