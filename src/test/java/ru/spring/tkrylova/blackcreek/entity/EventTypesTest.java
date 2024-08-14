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
public class EventTypesTest {
    @Test
    void eventTypes_DefaultConstructor() {
        EventTypes defaultEvenTypes = new EventTypes();
        assertThat(defaultEvenTypes).isNotNull();
        assertThat(defaultEvenTypes.getEventTypeId()).isNull();
        assertThat(defaultEvenTypes.getEventTypeName()).isNull();
        assertThat(defaultEvenTypes.isActive()).isTrue();
    }

    @Test
    void eventTypes_ParameterizedConstructor() {
        EventTypes parameterizedEvenTypes = new EventTypes(1L, "eventTypeName", false);
        assertThat(parameterizedEvenTypes.getEventTypeId()).isEqualTo(1L);
        assertThat(parameterizedEvenTypes.getEventTypeName()).isEqualTo("eventTypeName");
        assertThat(parameterizedEvenTypes.isActive()).isFalse();
    }

    @Test
    void eventTypes_DefaultIsActive() {
        EventTypes eventType = new EventTypes();
        assertTrue(eventType.isActive(), "Default value for isActive should be true");
    }

    @Test
    void eventTypes_GettersAndSetters() {
        EventTypes evenTypes = new EventTypes();
        evenTypes.setEventTypeId(2L);
        evenTypes.setEventTypeName("someTypeName");
        evenTypes.setActive(false);

        assertThat(evenTypes.getEventTypeId()).isEqualTo(2L);
        assertThat(evenTypes.getEventTypeName()).isEqualTo("someTypeName");
        assertThat(evenTypes.isActive()).isFalse();
    }

    @Test
    void eventTypes_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        EventTypes validEventType = new EventTypes(1L, "Seminar", true);
        Set<ConstraintViolation<EventTypes>> violations = validator.validate(validEventType);
        assertTrue(violations.isEmpty(), "Valid entity should not have validation errors");

        EventTypes invalidEventType = new EventTypes(1L, "a", true);
        violations = validator.validate(invalidEventType);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(1, violations.size(), "There should be one validation error");
        ConstraintViolation<EventTypes> violation = violations.iterator().next();
        assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                .containsAnyOf("size must be between 5 and 25", "размер должен находиться в диапазоне от 5 до 25");
    }

    @Test
    void eventTypes_TableName() {
        Class<EventTypes> entityClass = EventTypes.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "EventTypes class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("event_types", tableAnnotation.name(), "Table name should be 'event_types'");
    }
}
