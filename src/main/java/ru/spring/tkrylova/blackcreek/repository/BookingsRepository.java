package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.Booking;

public interface BookingsRepository extends JpaRepository<Booking, Long> {
}
