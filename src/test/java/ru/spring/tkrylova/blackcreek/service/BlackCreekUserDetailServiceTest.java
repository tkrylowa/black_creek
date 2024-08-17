package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.entity.UserRole;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserDetailService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BlackCreekUserDetailServiceTest {
    @Mock
    private BlackCreekUserRepository blackCreekUserRepository;

    @InjectMocks
    private BlackCreekUserDetailService blackCreekUserDetailService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenLoginIsValid() {
        String login = "validLogin";
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin(login);
        user.setPassword("password");
        UserRole role = new UserRole();
        role.setRoleName("ROLE_USER");
        user.setUserRole(role);
        when(blackCreekUserRepository.findByLogin(login)).thenReturn(Optional.of(user));

        UserDetails userDetails = blackCreekUserDetailService.loadUserByUsername(login);

        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(login, userDetails.getUsername(), "UserDetails username should match login");
        assertEquals("password", userDetails.getPassword(), "UserDetails password should match user's password");
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")),
                "UserDetails should have the correct authority");
        verify(blackCreekUserRepository, times(1)).findByLogin(login);
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        String login = "invalidLogin";
        when(blackCreekUserRepository.findByLogin(login)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                blackCreekUserDetailService.loadUserByUsername(login)
        );
        assertEquals("User not found", exception.getMessage());
        verify(blackCreekUserRepository, times(1)).findByLogin(login);
    }

    @Test
    void getUserByLogin_ShouldThrowIllegalArgumentException_WhenLoginIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                blackCreekUserDetailService.getUserByLogin(null)
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage());
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void getUserByLogin_ShouldThrowIllegalArgumentException_WhenLoginIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                blackCreekUserDetailService.getUserByLogin("")
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage());
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void getUserByLogin_ShouldThrowIllegalArgumentException_WhenLoginIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                blackCreekUserDetailService.getUserByLogin("   ")
        );
        assertEquals("Login must not be null, empty or blank", exception.getMessage());
        verify(blackCreekUserRepository, never()).findByLogin(anyString());
    }

    @Test
    void getUserByLogin_ShouldReturnUser_WhenUserExists() {
        String login = "validLogin";
        BlackCreekUser user = new BlackCreekUser();
        when(blackCreekUserRepository.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<BlackCreekUser> result = blackCreekUserDetailService.getUserByLogin(login);

        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(user, result.get(), "Result should match the user");
        verify(blackCreekUserRepository, times(1)).findByLogin(login);
    }

    @Test
    void getUserByLogin_ShouldReturnEmpty_WhenUserDoesNotExist() {
        String login = "invalidLogin";
        when(blackCreekUserRepository.findByLogin(login)).thenReturn(Optional.empty());

        Optional<BlackCreekUser> result = blackCreekUserDetailService.getUserByLogin(login);

        assertFalse(result.isPresent(), "Result should be empty");
        verify(blackCreekUserRepository, times(1)).findByLogin(login);
    }
}
