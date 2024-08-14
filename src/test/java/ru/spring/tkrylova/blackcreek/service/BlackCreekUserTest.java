package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.RoleType;
import ru.spring.tkrylova.blackcreek.entity.UserRole;
import ru.spring.tkrylova.blackcreek.execption.ResourceNotFoundException;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.repository.UserRoleRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BlackCreekUserTest {
    @InjectMocks
    private BlackCreekUserService userService;

    @Mock
    private BlackCreekUserRepository blackCreekUserRepository;
    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private BlackCreekUser user1;
    private BlackCreekUser user2;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        user1 = new BlackCreekUser();
        user1.setUserId(1L);
        user1.setPassword("plainPassword");
        user1.setConfirmPassword("plainPassword");
        user2 = new BlackCreekUser();
        userRole = new UserRole();
        userRole.setRoleName(RoleType.ROLE_REGISTERED_USER.name());
    }

    @Test
    void saveUser_Success() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRoleRepository.findByRoleName(RoleType.ROLE_REGISTERED_USER.name())).thenReturn(null);
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(userRole);
        when(blackCreekUserRepository.save(any(BlackCreekUser.class))).thenReturn(user1);
        BlackCreekUser result = userService.saveUser(user1);
        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("encodedPassword", result.getConfirmPassword());
        assertEquals(userRole.getRoleId(), result.getUserRole().getRoleId());
        assertEquals(userRole.getRoleName(), result.getUserRole().getRoleName());
    }

    @Test
    void saveUser_WithExistingRole() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRoleRepository.findByRoleName(RoleType.ROLE_REGISTERED_USER.name())).thenReturn(userRole);
        when(blackCreekUserRepository.save(any(BlackCreekUser.class))).thenReturn(user1);
        BlackCreekUser result = userService.saveUser(user1);
        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("encodedPassword", result.getConfirmPassword());
        assertEquals(userRole, result.getUserRole());
    }

    @Test
    void saveUser_NullUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));
        assertEquals("User must not be null", exception.getMessage());
    }

    @Test
    void findAllUsers_Success() {
        List<BlackCreekUser> users = Arrays.asList(user1, user2);
        when(blackCreekUserRepository.findAll()).thenReturn(users);
        List<BlackCreekUser> result = userService.findAllUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    void findAllUsers_NoUsers() {
        when(blackCreekUserRepository.findAll()).thenReturn(Collections.emptyList());
        List<BlackCreekUser> result = userService.findAllUsers();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findUserById_Success() {
        when(blackCreekUserRepository.findById(1L)).thenReturn(Optional.of(user1));
        BlackCreekUser result = userService.findUserById(1L);
        assertNotNull(result);
        assertEquals(user1, result);
    }

    @Test
    void findUserById_NotFound() {
        when(blackCreekUserRepository.findById(-1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findUserById(-1L));
    }

    @Test
    void findUserById_InvalidId() {
        Long invalidId = -1L;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.findUserById(invalidId));
        assertEquals("ID must be a positive number", exception.getMessage());
    }

    @Test
    void findByLogin_Success() {
        String validLogin = "validLogin";
        when(blackCreekUserRepository.findByLogin(validLogin)).thenReturn(Optional.of(user1));
        BlackCreekUser result = userService.findUserByLogin(validLogin);
        assertNotNull(result);
        assertEquals(user1, result);
    }

    @Test
    void findByLogin_NotFound() {
        String invalidLogin = "invalidLogin";
        when(blackCreekUserRepository.findByLogin(invalidLogin)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.findUserByLogin(invalidLogin));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findByLogin_NullLogin() {
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.findUserByLogin(null));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findByLogin_EmptyLogin() {
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.findUserByLogin(""));
        assertEquals("User not found", exception.getMessage());
    }
}
