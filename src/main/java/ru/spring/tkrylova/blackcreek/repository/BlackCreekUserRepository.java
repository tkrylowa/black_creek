package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;

import java.util.Optional;

public interface BlackCreekUserRepository extends JpaRepository<BlackCreekUser, Long> {
    Optional<BlackCreekUser> findByLogin(String login);
    boolean existsByLogin(String login);
    Optional<BlackCreekUser> findByUsername(String username);
}
