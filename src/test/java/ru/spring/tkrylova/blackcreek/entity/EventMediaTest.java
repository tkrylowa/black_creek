package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventMediaTest {
    @Test
    void eventTypes_DefaultConstructor() {
        EventMedia eventMedia = new EventMedia();
        assertThat(eventMedia).isNotNull();
        assertThat(eventMedia.getEventMediaId()).isNull();
        assertThat(eventMedia.getEventMediaPath()).isNull();
        assertThat(eventMedia.getEventId()).isNull();
    }

    @Test
    void eventTypes_ParameterizedConstructor() {
        EventMedia parameterizedEvenTypes = new EventMedia(1L, "media/path", 2L);
        assertThat(parameterizedEvenTypes.getEventMediaId()).isEqualTo(1L);
        assertThat(parameterizedEvenTypes.getEventMediaPath()).isEqualTo("media/path");
        assertThat(parameterizedEvenTypes.getEventId()).isEqualTo(2L);
    }

    @Test
    void eventTypes_GettersAndSetters() {
        EventMedia evenTypes = new EventMedia();
        evenTypes.setEventMediaId(2L);
        evenTypes.setEventMediaPath("new/media/path");
        evenTypes.setEventId(3L);

        assertThat(evenTypes.getEventMediaId()).isEqualTo(2L);
        assertThat(evenTypes.getEventMediaPath()).isEqualTo("new/media/path");
        assertThat(evenTypes.getEventId()).isEqualTo(3L);
    }

    @Test
    void eventTypes_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        EventMedia validEventMedia = new EventMedia(1L, "media/path", 2L);
        Set<ConstraintViolation<EventMedia>> violations = validator.validate(validEventMedia);
        assertTrue(violations.isEmpty(), "Valid entity should not have validation errors");

        EventMedia invalidEventMedia = new EventMedia(1L, "a", 2L);
        violations = validator.validate(invalidEventMedia);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(1, violations.size(), "There should be one validation error");
        ConstraintViolation<EventMedia> violation = violations.iterator().next();
        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                .containsAnyOf("size must be between 5 and 25", "размер должен находиться в диапазоне от 5 до 25");
    }

    @Test
    void userTypes_TableName() {
        Class<EventMedia> entityClass = EventMedia.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "UserTypes class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("event_media", tableAnnotation.name(), "Table name should be 'event_media'");
    }
}
