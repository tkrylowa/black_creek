package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private Feedback feedback;

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

        feedback = new Feedback();
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

    @Test
    void feedbackEntity_GettersAndSetters() {
        feedback.setComments("Updated comments");
        feedback.setRating(4);

        assertThat(feedback.getComments()).isEqualTo("Updated comments");
        assertThat(feedback.getRating()).isEqualTo(4);
    }

    @Test
    void feedbackEntity_AllArgsConstructor() {
        feedback = new Feedback(1L, new BlackCreekEvent(), new BlackCreekUser(), "comment", 2, LocalDateTime.now());
        assertThat(feedback).isNotNull();
        assertThat(feedback.getComments()).isEqualTo("comment");
        assertThat(feedback.getRating()).isEqualTo(2);
        assertThat(feedback.getFeedbackId()).isEqualTo(1L);
        assertThat(feedback.getCreatedAt()).isNotNull();
        assertThat(feedback.getUser()).isNotNull();
        assertThat(feedback.getEvent()).isNotNull();
    }

    @Test
    void feedbackEntity_tableName() {
        Class<Feedback> entityClass = Feedback.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "Feedback class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("feedbacks", tableAnnotation.name(), "Table name should be 'feedbacks'");
    }
}
