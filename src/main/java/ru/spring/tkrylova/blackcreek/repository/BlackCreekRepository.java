package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;

import java.util.Optional;

public interface BlackCreekRepository extends JpaRepository<BlackCreekUser, Long> {
    Optional<BlackCreekUser> findByLogin(String login);
    boolean existsByLogin(String login);
}
