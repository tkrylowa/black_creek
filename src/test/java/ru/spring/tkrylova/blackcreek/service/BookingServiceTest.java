package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.repository.BookingsRepository;
import ru.spring.tkrylova.blackcreek.servce.BookingService;

@SpringBootTest
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingsRepository bookingsRepository;


    @Test
    void getBookingsForEvent_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> bookingService.getBookingsForEvent(-1L)
        );
    }

    @Test
    void getBookingsForUser_ThrowsRuntimeException_NotPositiveId() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> bookingService.getBookingsForUser(-1L)
        );
    }
}
