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
        when(blackCreekUserRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.findUserById(1L)
        );
        assertEquals("User not found", exception.getMessage(), "Exception message should be 'User not found'");
        verify(blackCreekUserRepository, times(1)).findById(1L);
    }

    @Test
    void findUserById_InvalidId() {
        Long invalidId = -1L;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.findUserById(invalidId));
        assertEquals("ID must be a positive number", exception.getMessage());
    }

    @Test
    void findUserById_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.findUserById(null)
        );
        assertEquals("ID must be a positive number", exception.getMessage(), "Exception message should be 'ID must be a positive number'");
        verify(blackCreekUserRepository, never()).findById(anyLong());
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
        verify(blackCreekUserRepository, times(1)).findByLogin(invalidLogin);
    }

    @Test
    void findByLogin_ShouldThrowException_WhenNullLogin() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.findUserByLogin(null));
        assertEquals("Login must not be null, empty or blank", exception.getMessage());
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void findByLogin_ShouldThrowException_WhenEmptyLogin() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.findUserByLogin(""));
        assertEquals("Login must not be null, empty or blank", exception.getMessage());
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void findUserByLogin_ShouldThrowException_WhenLoginIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.findUserByLogin("   ")
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage(), "Exception message should be 'Login must not be null, empty or blank'");
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser_WhenUserExists() {
        Long userId = 1L;
        String login = "updatedLogin";
        String email = "updatedEmail@example.com";
        String password = "NewPassword123!";
        String encodedPassword = "EncodedPassword123!";
        UserRole userRole = new UserRole();
        userRole.setRoleName("ROLE_ADMIN");

        BlackCreekUser userToUpdate = new BlackCreekUser();
        userToUpdate.setUserId(userId);
        userToUpdate.setLogin(login);
        userToUpdate.setEmail(email);
        userToUpdate.setPassword(password);
        userToUpdate.setUserRole(userRole);

        BlackCreekUser existingUser = new BlackCreekUser();
        existingUser.setUserId(userId);
        existingUser.setLogin("oldLogin");
        existingUser.setEmail("oldEmail@example.com");
        existingUser.setPassword("OldPassword123!");
        existingUser.setUserRole(new UserRole());

        when(blackCreekUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(blackCreekUserRepository.save(existingUser)).thenReturn(existingUser);

        BlackCreekUser updatedUser = userService.updateUser(userToUpdate);

        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals(login, updatedUser.getLogin(), "Login should be updated");
        assertEquals(email, updatedUser.getEmail(), "Email should be updated");
        assertEquals(encodedPassword, updatedUser.getPassword(), "Password should be encoded and updated");
        assertEquals(userRole, updatedUser.getUserRole(), "User role should be updated");

        verify(blackCreekUserRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(password);
        verify(blackCreekUserRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_ShouldThrowIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(null)
        );
        assertEquals("User must not be null", exception.getMessage(), "Exception message should be 'User must not be null'");
        verify(blackCreekUserRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).encode(anyString());
        verify(blackCreekUserRepository, never()).save(any(BlackCreekUser.class));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser_WhenUserExistsAndPasswordIsEmpty() {
        BlackCreekUser existingUser = new BlackCreekUser();
        existingUser.setUserId(1L);
        existingUser.setLogin("oldLogin");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldPassword");
        UserRole oldRole = new UserRole();
        oldRole.setRoleName("ROLE_OLD");
        existingUser.setUserRole(oldRole);

        BlackCreekUser updatedUser = new BlackCreekUser();
        updatedUser.setUserId(1L);
        updatedUser.setLogin("newLogin");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("");
        UserRole newRole = new UserRole();
        newRole.setRoleName("ROLE_NEW");
        updatedUser.setUserRole(newRole);

        when(blackCreekUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(blackCreekUserRepository.save(any(BlackCreekUser.class))).thenReturn(existingUser);

        BlackCreekUser result = userService.updateUser(updatedUser);
        assertNotNull(result, "The result should not be null");
        assertEquals("newLogin", result.getLogin(), "The login should be updated");
        assertEquals("new@example.com", result.getEmail(), "The email should be updated");
        assertEquals("oldPassword", result.getPassword(), "The password should not be changed if it's empty");
        assertEquals("ROLE_NEW", result.getUserRole().getRoleName(), "The role should be updated");
        verify(blackCreekUserRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(blackCreekUserRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(null)
        );
        assertEquals("User must not be null", exception.getMessage(), "Exception message should be 'User must not be null'");
        verify(blackCreekUserRepository, never()).findById(anyLong());
        verify(blackCreekUserRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(1L);

        when(blackCreekUserRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(user)
        );
        assertEquals("User not found", exception.getMessage(), "Exception message should be 'User not found'");
        verify(blackCreekUserRepository, times(1)).findById(1L);
        verify(blackCreekUserRepository, never()).save(any(BlackCreekUser.class));
    }

    @Test
    void isLoginTaken_ShouldReturnTrue_WhenLoginIsTaken() {
        when(blackCreekUserRepository.findByLogin("takenLogin")).thenReturn(Optional.of(new BlackCreekUser()));

        boolean result = userService.isLoginTaken("takenLogin");

        assertTrue(result, "The login should be taken, so the result should be true");
        verify(blackCreekUserRepository, times(1)).findByLogin("takenLogin");
    }

    @Test
    void isLoginTaken_ShouldReturnFalse_WhenLoginIsNotTaken() {
        when(blackCreekUserRepository.findByLogin("freeLogin")).thenReturn(Optional.empty());
        boolean result = userService.isLoginTaken("freeLogin");
        assertFalse(result, "The login should not be taken, so the result should be false");
        verify(blackCreekUserRepository, times(1)).findByLogin("freeLogin");
    }

    @Test
    void isLoginTaken_ShouldThrowException_WhenLoginIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.isLoginTaken(null)
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage(), "Exception message should be 'Login must not be null, empty or blank'");
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void isLoginTaken_ShouldThrowException_WhenLoginIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.isLoginTaken("")
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage(), "Exception message should be 'Login must not be null, empty or blank'");
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void isLoginTaken_ShouldThrowException_WhenLoginIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.isLoginTaken("   ")
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage(), "Exception message should be 'Login must not be null, empty or blank'");
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }
}
