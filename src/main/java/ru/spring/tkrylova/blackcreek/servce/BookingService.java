package ru.spring.tkrylova.blackcreek.servce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.Booking;
import ru.spring.tkrylova.blackcreek.entity.BookingStatus;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.BookingsRepository;

import java.util.List;

@Slf4j
@Service
public class BookingService {
    private final BookingsRepository bookingsRepository;

    public BookingService(BookingsRepository bookingsRepository) {
        this.bookingsRepository = bookingsRepository;
    }

    public List<Booking> getBookingsForEvent(Long eventId) {
        if (eventId == null || eventId < 0) {
            throw new ResourceNotFoundException("Event not found");
        }
        return bookingsRepository.findByEventId(eventId);
    }

    public List<Booking> getBookingsForUser(Long userId) {
        if (userId == null || userId < 0) {
            throw new ResourceNotFoundException("User not found");
        }
        return bookingsRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsForStatus(BookingStatus bookingStatus) {
        return bookingsRepository.findByStatus(bookingStatus);
    }

    public Booking registerUserForEvent(Booking registration) {
        return bookingsRepository.save(registration);
    }
}
