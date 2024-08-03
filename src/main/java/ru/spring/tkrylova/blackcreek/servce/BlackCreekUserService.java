package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.UserRole;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.repository.UserRoleRepository;

import java.util.List;

@Service
public class BlackCreekUserService {
    private final BlackCreekUserRepository blackCreekUserRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public BlackCreekUserService(BlackCreekUserRepository blackCreekUserRepository, UserRoleRepository userRoleRepository) {
        this.blackCreekUserRepository = blackCreekUserRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public void saveUser(BlackCreekUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserRole userRole = userRoleRepository.findByName("ROLE_REGISTERED_USER");
        blackCreekUserRepository.save(user);
    }

    public BlackCreekUser updateUser(BlackCreekUser user) {
        BlackCreekUser existingUser = blackCreekUserRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getEmail());
        if (!user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setUserRole(user.getUserRole());
        return blackCreekUserRepository.save(existingUser);
    }

    public List<BlackCreekUser> findAllUsers() {
        return blackCreekUserRepository.findAll();
    }

    public BlackCreekUser findUserById(Long id) {
        return blackCreekUserRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
