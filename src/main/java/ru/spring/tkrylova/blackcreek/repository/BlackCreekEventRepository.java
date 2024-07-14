package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;

public interface BlackCreekEventRepository extends JpaRepository<BlackCreekEvent, Long> {
}
