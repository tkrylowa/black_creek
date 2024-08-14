package ru.spring.tkrylova.blackcreek.entity.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.EventPhoto;
import ru.spring.tkrylova.blackcreek.entity.Feedback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BlackCreekEventTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidBlackCreekEvent_thenNoViolations() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(1));
        event.setEventEndDate(LocalDate.now().plusDays(2));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(100);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEventNameTooShort_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Fair");
        event.setEventStartDate(LocalDate.now().plusDays(1));
        event.setEventEndDate(LocalDate.now().plusDays(2));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(100);
        event.setFree(true);
        event.setCost(0.0);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setCreatedAt(LocalDateTime.now());

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);
        ConstraintViolation<BlackCreekEvent> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("eventName");
        assertThat(violation.getMessage()).containsAnyOf("size must be between 5 and 25", "размер должен находиться в диапазоне от 5 до 25");
    }

    @Test
    void whenEventStartDateInPast_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().minusDays(1));
        event.setEventEndDate(LocalDate.now().plusDays(2));
        event.setEventDescription("A fun fair with medieval events.");
        event.setFree(true);
        event.setEventCapacity(100);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).containsAnyOf("must be a date in the present or in the future", "должно содержать сегодняшнее число или дату, которая еще не наступила");
    }

    @Test
    void whenEventEndDateBeforeStartDate_thenNoViolations() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(2));
        event.setEventEndDate(LocalDate.now().plusDays(1));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(100);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEventCapacityNegative_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(1));
        event.setEventEndDate(LocalDate.now().plusDays(2));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(-1);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);
        ConstraintViolation<BlackCreekEvent> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("eventCapacity");
        assertThat(violation.getMessage()).containsAnyOf("must be greater than or equal to 2", "должно быть не меньше 2");
    }

    @Test
    void whenEventCapacityTooLow_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(1));
        event.setEventEndDate(LocalDate.now().plusDays(2));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(1);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);
        ConstraintViolation<BlackCreekEvent> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("eventCapacity");
        assertThat(violation.getMessage()).containsAnyOf("must be greater than or equal to 2", "должно быть не меньше 2");
    }

    @Test
    void whenEventDescriptionTooShort_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(10));
        event.setEventEndDate(LocalDate.now().plusDays(12));
        event.setEventDescription("Festival");
        event.setEventCapacity(50);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);

        ConstraintViolation<BlackCreekEvent> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("eventDescription");
        assertThat(violation.getMessage()).containsAnyOf("size must be at least 10", "размер должен находиться в диапазоне от 10 до 2147483647");
    }

    @Test
    void whenEventIsFreeAndCostIsPositive_thenNoViolations() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(10));
        event.setEventEndDate(LocalDate.now().plusDays(12));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(50);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(20.0);

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEventCostNegative_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(10));
        event.setEventEndDate(LocalDate.now().plusDays(12));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(50);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(-10.0);

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);

        ConstraintViolation<BlackCreekEvent> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("cost");
        assertThat(violation.getMessage()).containsAnyOf("must be greater than or equal to 0", "должно быть больше или равно 0");
    }

    @Test
    void whenIsFreeDefault_thenTrue() {
        BlackCreekEvent event = new BlackCreekEvent();
        assertThat(event.isFree()).isTrue();
    }

    @Test
    void whenIsCancelledDefault_thenFalse() {
        BlackCreekEvent event = new BlackCreekEvent();
        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void whenCreatedAtIsNotSet_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Medieval Fair");
        event.setEventStartDate(LocalDate.now().plusDays(10));
        event.setEventEndDate(LocalDate.now().plusDays(12));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(50);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEventNameNull_thenViolation() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventStartDate(LocalDate.now().plusDays(1));
        event.setEventEndDate(LocalDate.now().plusDays(2));
        event.setEventDescription("A fun fair with medieval events.");
        event.setEventCapacity(100);
        event.setEventTypeId(1L);
        event.setResponsibleUserId(1L);
        event.setFree(true);
        event.setCost(0.0);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");

        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(event);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).containsAnyOf("must not be null", "не должно равняться null");
    }

    @Test
    void whenEventPhotosInitialized_thenEmptyList() {
        BlackCreekEvent event = new BlackCreekEvent();
        assertThat(event.getEventPhotos()).isNotNull();
        assertThat(event.getEventPhotos()).isEmpty();
    }

    @Test
    void whenEventUsersInitialized_thenEmptySet() {
        BlackCreekEvent event = new BlackCreekEvent();
        assertThat(event.getUsers()).isNotNull();
        assertThat(event.getUsers()).isEmpty();
    }

    @Test
    void whenEventAttendeesInitialized_thenEmptySet() {
        BlackCreekEvent event = new BlackCreekEvent();
        assertThat(event.getAttendees()).isNotNull();
        assertThat(event.getAttendees()).isEmpty();
    }

    @Test
    void whenFeedbacksInitialized_thenEmptyList() {
        BlackCreekEvent event = new BlackCreekEvent();
        assertThat(event.getFeedbacks()).isNotNull();
        assertThat(event.getFeedbacks()).isEmpty();
    }

    @Test
    void whenBlackCreekEventInitialized_thenDefaultsAreCorrect() {
        BlackCreekEvent event = new BlackCreekEvent();

        assertThat(event.isFree()).isTrue();
        assertThat(event.isCancelled()).isFalse();
        assertThat(event.getUsers()).isNotNull();
        assertThat(event.getUsers()).isEmpty();
        assertThat(event.getAttendees()).isNotNull();
        assertThat(event.getAttendees()).isEmpty();
        assertThat(event.getFeedbacks()).isNotNull();
        assertThat(event.getFeedbacks()).isEmpty();
        assertThat(event.getEventPhotos()).isNotNull();
        assertThat(event.getEventPhotos()).isEmpty();
    }

    @Test
    void whenSettingEventFields_thenFieldsAreSetCorrectly() {
        BlackCreekEvent event = new BlackCreekEvent();

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(12);
        LocalDateTime createdAt = LocalDateTime.now();
        Double cost = 20.0;
        Long eventTypeId = 1L;
        Long responsibleUserId = 2L;
        Integer capacity = 100;

        event.setEventName("Music Festival");
        event.setEventStartDate(startDate);
        event.setEventEndDate(endDate);
        event.setEventDescription("An amazing music festival.");
        event.setEventCapacity(capacity);
        event.setEventTypeId(eventTypeId);
        event.setResponsibleUserId(responsibleUserId);
        event.setFree(false);
        event.setCancelled(true);
        event.setCost(cost);
        event.setCreatedAt(createdAt);
        event.setCreatedBy("testUser");
        event.setUpdatedBy("updateUser");

        assertThat(event.getEventName()).isEqualTo("Music Festival");
        assertThat(event.getEventStartDate()).isEqualTo(startDate);
        assertThat(event.getEventEndDate()).isEqualTo(endDate);
        assertThat(event.getEventDescription()).isEqualTo("An amazing music festival.");
        assertThat(event.getEventCapacity()).isEqualTo(capacity);
        assertThat(event.getEventTypeId()).isEqualTo(eventTypeId);
        assertThat(event.getResponsibleUserId()).isEqualTo(responsibleUserId);
        assertThat(event.isFree()).isFalse();
        assertThat(event.isCancelled()).isTrue();
        assertThat(event.getCost()).isEqualTo(cost);
        assertThat(event.getCreatedAt()).isEqualTo(createdAt);
        assertThat(event.getCreatedBy()).isEqualTo("testUser");
        assertThat(event.getUpdatedBy()).isEqualTo("updateUser");
    }

    @Test
    void whenAddingUsersAndAttendees_thenTheyAreAddedCorrectly() {
        BlackCreekEvent event = new BlackCreekEvent();

        BlackCreekUser user1 = new BlackCreekUser();
        BlackCreekUser user2 = new BlackCreekUser();

        Set<BlackCreekUser> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        event.setUsers(users);
        event.setAttendees(users);

        assertThat(event.getUsers()).containsExactlyInAnyOrder(user1, user2);
        assertThat(event.getAttendees()).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void whenAddingFeedbacks_thenTheyAreAddedCorrectly() {
        BlackCreekEvent event = new BlackCreekEvent();

        Feedback feedback1 = new Feedback();
        Feedback feedback2 = new Feedback();

        List<Feedback> feedbacks = List.of(feedback1, feedback2);
        event.setFeedbacks(feedbacks);

        assertThat(event.getFeedbacks()).containsExactlyInAnyOrder(feedback1, feedback2);
    }

    @Test
    void whenAddingEventPhotos_thenTheyAreAddedCorrectly() {
        BlackCreekEvent event = new BlackCreekEvent();

        EventPhoto photo1 = new EventPhoto();
        EventPhoto photo2 = new EventPhoto();

        List<EventPhoto> eventPhotos = List.of(photo1, photo2);
        event.setEventPhotos(eventPhotos);

        assertThat(event.getEventPhotos()).containsExactlyInAnyOrder(photo1, photo2);
    }

    @Test
    void whenSettingDates_thenDatesAreSetCorrectly() {
        BlackCreekEvent event = new BlackCreekEvent();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusDays(1);

        event.setCreatedAt(createdAt);
        event.setUpdatedAt(updatedAt);

        assertThat(event.getCreatedAt()).isEqualTo(createdAt);
        assertThat(event.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
