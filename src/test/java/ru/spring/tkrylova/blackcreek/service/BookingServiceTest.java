package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.Booking;
import ru.spring.tkrylova.blackcreek.entity.BookingStatus;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.BookingsRepository;
import ru.spring.tkrylova.blackcreek.servce.BookingService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingsRepository bookingsRepository;

    @Test
    void getBookingsForEvent_ShouldReturnBookings_WhenEventIdIsValid() {
        Long eventId = 1L;
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        when(bookingsRepository.findByEventId(eventId)).thenReturn(Arrays.asList(booking1, booking2));

        List<Booking> bookings = bookingService.getBookingsForEvent(eventId);

        assertNotNull(bookings, "The bookings list should not be null");
        assertEquals(2, bookings.size(), "The bookings list size should be 2");
        verify(bookingsRepository, times(1)).findByEventId(eventId);
    }

    @Test
    void getBookingsForEvent_ShouldThrowException_WhenEventIdIsNull() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingsForEvent(null)
        );
        assertEquals("Event not found", exception.getMessage());
        verify(bookingsRepository, never()).findByEventId(null);
    }

    @Test
    void getBookingsForEvent_ShouldThrowException_WhenEventIdIsNegative() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingsForEvent(-1L)
        );
        assertEquals("Event not found", exception.getMessage());
        verify(bookingsRepository, never()).findByEventId(-1L);
    }

    @Test
    void getBookingsForUser_ShouldReturnBookings_WhenUserIdIsValid() {
        Long userId = 1L;
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        when(bookingsRepository.findByUserId(userId)).thenReturn(Arrays.asList(booking1, booking2));

        List<Booking> bookings = bookingService.getBookingsForUser(userId);

        assertNotNull(bookings, "The bookings list should not be null");
        assertEquals(2, bookings.size(), "The bookings list size should be 2");
        verify(bookingsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getBookingsForUser_ShouldThrowException_WhenUserIdIsNull() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingsForUser(null)
        );
        assertEquals("User not found", exception.getMessage());
        verify(bookingsRepository, never()).findByUserId(null);
    }

    @Test
    void getBookingsForUser_ShouldThrowException_WhenUserIdIsNegative() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingsForUser(-1L)
        );
        assertEquals("User not found", exception.getMessage());
        verify(bookingsRepository, never()).findByUserId(-1L);
    }

    @Test
    void getBookingsForStatus_ShouldReturnBookings_WhenStatusIsValid() {
        BookingStatus status = BookingStatus.CREATED;
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        when(bookingsRepository.findByStatus(status)).thenReturn(Arrays.asList(booking1, booking2));

        List<Booking> bookings = bookingService.getBookingsForStatus(status);

        assertNotNull(bookings, "The bookings list should not be null");
        assertEquals(2, bookings.size(), "The bookings list size should be 2");
        verify(bookingsRepository, times(1)).findByStatus(status);
    }

    @Test
    void registerUserForEvent_ShouldSaveBooking_WhenBookingIsValid() {
        Booking booking = new Booking();
        when(bookingsRepository.save(booking)).thenReturn(booking);

        Booking savedBooking = bookingService.registerUserForEvent(booking);

        assertNotNull(savedBooking, "The saved booking should not be null");
        assertEquals(booking, savedBooking, "The saved booking should match the input booking");
        verify(bookingsRepository, times(1)).save(booking);
    }

    @Test
    void registerUserForEvent_ShouldThrowException_WhenBookingIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.registerUserForEvent(null)
        );
        assertEquals("Booking is null", exception.getMessage());
        verify(bookingsRepository, never()).save(any());
    }
}
