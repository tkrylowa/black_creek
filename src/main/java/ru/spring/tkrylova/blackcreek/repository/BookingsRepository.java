package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.spring.tkrylova.blackcreek.entity.Booking;
import ru.spring.tkrylova.blackcreek.entity.BookingStatus;

import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM bookings f WHERE event_id = :eventId")
    List<Booking> findByEventId(Long eventId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings f WHERE user_id = :userId")
    List<Booking> findByUserId(Long userId);

    List<Booking> findByStatus(BookingStatus bookingStatus);
}
