package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.Place;

public interface PlacesRepository extends JpaRepository<Place, Long> {
}
