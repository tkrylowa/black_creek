package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spring.tkrylova.blackcreek.entity.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}