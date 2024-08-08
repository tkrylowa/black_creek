package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.RoleType;
import ru.spring.tkrylova.blackcreek.entity.UserRole;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
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

    public BlackCreekUser saveUser(BlackCreekUser user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));
        UserRole userRole = userRoleRepository.findByRoleName(RoleType.ROLE_REGISTERED_USER.name());
        if (userRole == null) {
            userRole = new UserRole();
            userRole.setRoleName(RoleType.ROLE_REGISTERED_USER.name());
            userRoleRepository.save(userRole);
        }
        user.setUserRole(userRole);
        return blackCreekUserRepository.save(user);
    }

    public BlackCreekUser updateUser(BlackCreekUser user) {
        BlackCreekUser existingUser = blackCreekUserRepository.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
        return blackCreekUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public BlackCreekUser findUserByLogin(String login) {
        return blackCreekUserRepository.findByLogin(login).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isLoginTaken(String login) {
        return blackCreekUserRepository.findByLogin(login).isPresent();
    }
}
