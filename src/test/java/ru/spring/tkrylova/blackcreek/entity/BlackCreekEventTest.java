package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class BlackCreekEventTest {
    @Autowired
    private BlackCreekEventRepository eventRepository;

    @BeforeEach
    public void setup() {
        eventRepository.deleteAllInBatch();
    }

    @Test
    void blackCreekEvent_CanSaveAndFindById() {
        BlackCreekEvent event = createEvent();
        BlackCreekEvent savedEvent = eventRepository.save(event);

        Optional<BlackCreekEvent> foundEvent = eventRepository.findById(savedEvent.getEventId());
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getEventName()).isEqualTo(event.getEventName());
    }

    @Test
    void blackCreekEvent_Relationships() {
        BlackCreekEvent event = createEvent();
        eventRepository.save(event);

        BlackCreekEvent foundEvent = eventRepository.findById(event.getEventId()).orElseThrow(RuntimeException::new);
        assertThat(foundEvent.getUsers()).isNotNull();
        assertThat(foundEvent.getAttendees()).isNotNull();
        assertThat(foundEvent.getFeedbacks()).isNotNull();
        assertThat(foundEvent.getEventPhotos()).isNotNull();
    }

    @Test
    void blackCreekEvent_ManyToManyRelationships() throws NoSuchFieldException {
        Class<BlackCreekEvent> entityClass = BlackCreekEvent.class;

        Field fieldUsers = entityClass.getDeclaredField("users");
        assertTrue(fieldUsers.isAnnotationPresent(ManyToMany.class), "users field should be annotated with @ManyToMany");
        assertTrue(fieldUsers.isAnnotationPresent(JoinTable.class), "users field should be annotated with @JoinTable");

        JoinTable joinTableAnnotationUsers = fieldUsers.getAnnotation(JoinTable.class);
        assertEquals("event_users", joinTableAnnotationUsers.name(), "Join table name for users should be 'event_users'");

        Field fieldAttendees = entityClass.getDeclaredField("attendees");
        assertTrue(fieldAttendees.isAnnotationPresent(ManyToMany.class), "attendees field should be annotated with @ManyToMany");
        assertTrue(fieldAttendees.isAnnotationPresent(JoinTable.class), "attendees field should be annotated with @JoinTable");

        JoinTable joinTableAnnotationAttendees = fieldAttendees.getAnnotation(JoinTable.class);
        assertEquals("event_attendance", joinTableAnnotationAttendees.name(), "Join table name for attendees should be 'event_attendance'");
    }

    @Test
    void blackCreekEvent_OneToManyRelationships() throws NoSuchFieldException {
        Class<BlackCreekEvent> entityClass = BlackCreekEvent.class;

        Field fieldFeedbacks = entityClass.getDeclaredField("feedbacks");
        assertTrue(fieldFeedbacks.isAnnotationPresent(OneToMany.class), "feedbacks field should be annotated with @OneToMany");
        assertEquals("event", fieldFeedbacks.getAnnotation(OneToMany.class).mappedBy(), "MappedBy value for feedbacks should be 'event'");

        Field fieldEventPhotos = entityClass.getDeclaredField("eventPhotos");
        assertTrue(fieldEventPhotos.isAnnotationPresent(OneToMany.class), "eventPhotos field should be annotated with @OneToMany");
        assertEquals("event", fieldEventPhotos.getAnnotation(OneToMany.class).mappedBy(), "MappedBy value for eventPhotos should be 'event'");
    }

    @Test
    void blackCreekEvent_FieldsDefaults() {
        BlackCreekEvent event = createEvent();
        BlackCreekEvent savedEvent = eventRepository.save(event);

        assertThat(savedEvent.isFree()).isTrue();
        assertThat(savedEvent.isCancelled()).isFalse();
    }

    private BlackCreekEvent createEvent() {
        BlackCreekEvent event = new BlackCreekEvent();
        event.setEventName("Test Event");
        event.setEventStartDate(LocalDate.now().plusDays(10));
        event.setEventEndDate(LocalDate.now().plusDays(11));
        event.setEventDescription("Test Event Description");
        event.setEventCapacity(100);
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy("testUser");
        event.setUpdatedBy("testUser");
        event.setCost(50.0);

        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(1L);
        event.getUsers().add(user);
        event.getAttendees().add(user);

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(1L);
        event.getFeedbacks().add(feedback);

        EventPhoto photo = new EventPhoto();
        photo.setId(1L);
        event.getEventPhotos().add(photo);

        return event;
    }

    @Test
    void blackCreekEvent_ConstructorsAndGettersSetters() {
        BlackCreekEvent event = new BlackCreekEvent();
        LocalDateTime now = LocalDateTime.now();

        event.setEventId(1L);
        event.setEventName("Event Name");
        event.setEventStartDate(LocalDate.now());
        event.setEventEndDate(LocalDate.now().plusDays(1));
        event.setEventDescription("Event Description");
        event.setEventCapacity(100);
        event.setEventTypeId(2L);
        event.setResponsibleUserId(3L);
        event.setFree(false);
        event.setCancelled(true);
        event.setCost(150.0);
        event.setCreatedAt(now);
        event.setCreatedBy("admin");
        event.setUpdatedAt(now);
        event.setUpdatedBy("admin_updated");

        assertEquals(1L, event.getEventId(), "getEventId should return the correct value");
        assertEquals("Event Name", event.getEventName(), "getEventName should return the correct value");
        assertEquals(LocalDate.now(), event.getEventStartDate(), "getEventStartDate should return the correct value");
        assertEquals(LocalDate.now().plusDays(1), event.getEventEndDate(), "getEventEndDate should return the correct value");
        assertEquals("Event Description", event.getEventDescription(), "getEventDescription should return the correct value");
        assertEquals(100, event.getEventCapacity(), "getEventCapacity should return the correct value");
        assertEquals(2L, event.getEventTypeId(), "getEventTypeId should return the correct value");
        assertEquals(3L, event.getResponsibleUserId(), "getResponsibleUserId should return the correct value");
        assertFalse(event.isFree(), "getFree should return the correct value");
        assertTrue(event.isCancelled(), "getCancelled should return the correct value");
        assertEquals(150.0, event.getCost(), "getCost should return the correct value");
        assertEquals(now, event.getCreatedAt(), "getCreatedAt should return the correct value");
        assertEquals("admin", event.getCreatedBy(), "getCreatedBy should return the correct value");
        assertEquals(now, event.getUpdatedAt(), "getUpdatedAt should return the correct value");
        assertEquals("admin_updated", event.getUpdatedBy(), "getUpdatedBy should return the correct value");
    }

    @Test
    void blackCreekEvent_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        BlackCreekEvent validEvent = new BlackCreekEvent(1L, "Valid Event",
                LocalDate.now(), LocalDate.now().plusDays(1), "Description",
                100, 2L, 3L, true, false,
                200.0, new HashSet<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>(),
                LocalDateTime.now(), "admin", LocalDateTime.now(), "admin_updated");
        Set<ConstraintViolation<BlackCreekEvent>> violations = validator.validate(validEvent);
        assertTrue(violations.isEmpty(), "Valid entity should have no validation errors");

        BlackCreekEvent invalidEvent = new BlackCreekEvent(null, "Event",
                LocalDate.now().minusDays(1), LocalDate.now().minusDays(2),
                "Short", 1, null, null,
                true, false, -100.0, new HashSet<>(), new HashSet<>(),
                new ArrayList<>(), new ArrayList<>(), LocalDateTime.now(), null,
                LocalDateTime.now(), null);
        violations = validator.validate(invalidEvent);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(5, violations.size(), "There should be five validation errors");

        for (ConstraintViolation<BlackCreekEvent> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            switch (propertyPath) {
                case "eventName" ->
                        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("size must be between 5 and 25", "размер должен находиться в диапазоне от 5 до 25");
                case "eventStartDate" ->
                        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("must be a future or present date", "должно содержать сегодняшнее число или дату, которая еще не наступила");
                case "eventEndDate" ->
                        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("Data end should be in future", "должно содержать дату, которая еще не наступила");
                case "eventCapacity" ->
                        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("must be greater than or equal to 2", "должно быть не меньше 2");
                case "cost" ->
                        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("must be greater than or equal to 0", "должно быть больше или равно 0");
            }
        }
    }

    @Test
    void blackCreekEvent_tableName() {
        Class<BlackCreekEvent> entityClass = BlackCreekEvent.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "BlackCreekEvent class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("events", tableAnnotation.name(), "Table name should be 'events'");
    }
}
