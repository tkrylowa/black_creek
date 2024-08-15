package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingTest {
    @Test
    void booking_NoArgsConstructor() {
        Booking booking = new Booking();
        assertNotNull(booking, "No-args constructor should create an instance of Booking");
    }

    @Test
    void booking_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking(1L, 2L, 3L, now, TimeSlot.MORNING, BookingStatus.CREATED, now, "user1", now, "user2");

        assertNotNull(booking, "All-args constructor should create an instance of Booking");
        assertEquals(1L, booking.getBookingId(), "bookingId should be set correctly");
        assertEquals(2L, booking.getEventId(), "eventId should be set correctly");
        assertEquals(3L, booking.getUserId(), "userId should be set correctly");
        assertEquals(now, booking.getBookingDate(), "bookingDate should be set correctly");
        assertEquals(TimeSlot.MORNING, booking.getTimeSlot(), "timeSlot should be set correctly");
        assertEquals(BookingStatus.CREATED, booking.getStatus(), "status should be set correctly");
        assertEquals(now, booking.getCreatedAt(), "createdAt should be set correctly");
        assertEquals("user1", booking.getCreatedBy(), "createdBy should be set correctly");
        assertEquals(now, booking.getUpdatedAt(), "updatedAt should be set correctly");
        assertEquals("user2", booking.getUpdatedBy(), "updatedBy should be set correctly");
    }

    @Test
    void booking_GettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setEventId(2L);
        booking.setUserId(3L);
        booking.setBookingDate(now);
        booking.setTimeSlot(TimeSlot.MORNING);
        booking.setStatus(BookingStatus.CREATED);
        booking.setCreatedAt(now);
        booking.setCreatedBy("user1");
        booking.setUpdatedAt(now);
        booking.setUpdatedBy("user2");

        assertNotNull(booking, "All-args constructor should create an instance of Booking");
        assertEquals(1L, booking.getBookingId(), "bookingId should be set correctly");
        assertEquals(2L, booking.getEventId(), "eventId should be set correctly");
        assertEquals(3L, booking.getUserId(), "userId should be set correctly");
        assertEquals(now, booking.getBookingDate(), "bookingDate should be set correctly");
        assertEquals(TimeSlot.MORNING, booking.getTimeSlot(), "timeSlot should be set correctly");
        assertEquals(BookingStatus.CREATED, booking.getStatus(), "status should be set correctly");
        assertEquals(now, booking.getCreatedAt(), "createdAt should be set correctly");
        assertEquals("user1", booking.getCreatedBy(), "createdBy should be set correctly");
        assertEquals(now, booking.getUpdatedAt(), "updatedAt should be set correctly");
        assertEquals("user2", booking.getUpdatedBy(), "updatedBy should be set correctly");
    }

    @Test
    void booking_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Booking validBooking = new Booking(1L, 2L, 3L, LocalDateTime.now(), TimeSlot.MORNING, BookingStatus.PAID, LocalDateTime.now(), "user1", LocalDateTime.now(), "user2");
        Set<ConstraintViolation<Booking>> violations = validator.validate(validBooking);
        assertTrue(violations.isEmpty(), "Valid entity should have no validation errors");

        Booking invalidBooking = new Booking(1L, 2L, 3L, LocalDateTime.now(), null, null, LocalDateTime.now(), "user1", LocalDateTime.now(), "user2");
        violations = validator.validate(invalidBooking);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(2, violations.size(), "There should be two validation errors");

        for (ConstraintViolation<Booking> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            if (propertyPath.equals("timeSlot")) {
                assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                        .containsAnyOf("may not be null", "не должно равняться null");
            } else if (propertyPath.equals("status")) {
                assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                        .containsAnyOf("may not be null", "не должно равняться null");
            }
        }
    }

    @Test
    void booking_TableName() {
        Class<Booking> entityClass = Booking.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "Booking class should be annotated with @Table");

        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("bookings", tableAnnotation.name(), "Table name should be 'bookings'");
    }
}
