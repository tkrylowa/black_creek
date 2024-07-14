package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.Booking;
import ru.spring.tkrylova.blackcreek.entity.BookingStatus;

import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByEventId(Long eventId);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatus(BookingStatus bookingStatus);
}
