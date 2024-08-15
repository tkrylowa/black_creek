package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BlackCreekUserTest {

    @Test
    void blackCreekUser_NoArgsConstructor() {
        BlackCreekUser user = new BlackCreekUser();
        assertNotNull(user, "No-args constructor should create a non-null object");
    }

    @Test
    void blackCreekUser_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        BlackCreekUser userWithArgs = new BlackCreekUser(
                1L, 2L, "test@example.com", "testUser", "First", "Last",
                "Password@123", "Password@123", new UserRole(), "noToken", "some_value",
                false, new HashSet<>(), new ArrayList<>(),
                now, "admin",
                now, "admin_updated"
        );

        assertEquals(1L, userWithArgs.getUserId(), "getUserId should return the correct value");
        assertEquals(2L, userWithArgs.getTypeId(), "getTypeId should return the correct value");
        assertEquals("test@example.com", userWithArgs.getEmail(), "getEmail should return the correct value");
        assertEquals("testUser", userWithArgs.getLogin(), "getLogin should return the correct value");
        assertEquals("First", userWithArgs.getFirstName(), "getFirstName should return the correct value");
        assertEquals("Last", userWithArgs.getLastName(), "getLastName should return the correct value");
        assertEquals("Password@123", userWithArgs.getPassword(), "getPassword should return the correct value");
        assertEquals("Password@123", userWithArgs.getConfirmPassword(), "getConfirmPassword should return the correct value");
        assertFalse(userWithArgs.isLoggedIn(), "isLoggedIn should return false");
        assertNotNull(userWithArgs.getUserRole(), "getUserRole should return not null value value");
        assertEquals("noToken", userWithArgs.getUserToken(), "getUserToken should return the correct value");
        assertEquals("some_value", userWithArgs.getTokenExpiration(), "getTypeId should return the correct value");
        assertEquals(now, userWithArgs.getCreatedAt(), "getCreatedAt should return the correct value");
        assertEquals(now, userWithArgs.getUpdatedAt(), "getCreatedAt should return the correct value");
        assertEquals("admin", userWithArgs.getCreatedBy(), "getCreatedBy should return the correct value");
        assertEquals("admin_updated", userWithArgs.getUpdatedBy(), "getUpdatedBy should return the correct value");
        assertNotNull(userWithArgs.getAttendedEvents(), "getAttendedEvents should return not null value value");
        assertNotNull(userWithArgs.getFeedbacks(), "getFeedbacks should return not null value value");
    }

    @Test
    void blackCreekUser_GettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        BlackCreekUser user = new BlackCreekUser();
        user.setUserId(1L);
        user.setTypeId(2L);
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("Password@123");
        user.setConfirmPassword("Password@123");
        user.setLoggedIn(true);
        user.setUserRole(new UserRole(1L, "Some name"));
        user.setUserToken("noToken");
        user.setTokenExpiration("some_value");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setCreatedBy("admin");
        user.setUpdatedBy("admin_updated");
        user.setFeedbacks(new ArrayList<>());
        user.setAttendedEvents(new HashSet<>());

        assertEquals(1L, user.getUserId(), "getUserId should return the correct value");
        assertEquals(2L, user.getTypeId(), "getTypeId should return the correct value");
        assertEquals("test@example.com", user.getEmail(), "getEmail should return the correct value");
        assertEquals("testUser", user.getLogin(), "getLogin should return the correct value");
        assertEquals("First", user.getFirstName(), "getFirstName should return the correct value");
        assertEquals("Last", user.getLastName(), "getLastName should return the correct value");
        assertEquals("Password@123", user.getPassword(), "getPassword should return the correct value");
        assertEquals("Password@123", user.getConfirmPassword(), "getConfirmPassword should return the correct value");
        assertTrue(user.isLoggedIn(), "isLoggedIn should return true");
        assertNotNull(user.getUserRole(), "getUserRole should return not null value value");
        assertEquals("noToken", user.getUserToken(), "getUserToken should return the correct value");
        assertEquals("some_value", user.getTokenExpiration(), "getTypeId should return the correct value");
        assertEquals(now, user.getCreatedAt(), "getCreatedAt should return the correct value");
        assertEquals(now, user.getUpdatedAt(), "getCreatedAt should return the correct value");
        assertEquals("admin", user.getCreatedBy(), "getCreatedBy should return the correct value");
        assertEquals("admin_updated", user.getUpdatedBy(), "getUpdatedBy should return the correct value");
        assertNotNull(user.getAttendedEvents(), "getAttendedEvents should return not null value value");
        assertNotNull(user.getFeedbacks(), "getFeedbacks should return not null value value");
    }

    @Test
    void blackCreekUser_DefaultValues() {
        BlackCreekUser user = new BlackCreekUser();
        assertFalse(user.isLoggedIn(), "Default value for loggedIn should be false");
        assertNull(user.getUserToken(), "Default value for userToken should be null");
        assertNull(user.getTokenExpiration(), "Default value for tokenExpiration should be null");
    }

    @Test
    void blackCreekUser_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        BlackCreekUser validUser = new BlackCreekUser(1L, 2L, "valid@example.com", "validlogin", "John", "Doe", "ValidPass1!", "ValidPass1!", null, null, null, false, new HashSet<>(), new ArrayList<>(), LocalDateTime.now(), "current_user", LocalDateTime.now(), "current_user");
        Set<ConstraintViolation<BlackCreekUser>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty(), "Valid entity should have no validation errors");

        BlackCreekUser invalidUser = new BlackCreekUser(null, null, "invalid", "log", "J", "D", "", null, null, null, null, false, new HashSet<>(), new ArrayList<>(), LocalDateTime.now(), null, LocalDateTime.now(), null);
        violations = validator.validate(invalidUser);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(6, violations.size(), "There should be seven validation errors");
        SoftAssertions sa = new SoftAssertions();

        for (ConstraintViolation<BlackCreekUser> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            switch (propertyPath) {
                case "email" ->
                        sa.assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("Email should be valid");
                case "login" ->
                        sa.assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("Login must be between 6 and 20 characters", "Login must be between 6 and 20 characters");
                case "firstName", "lastName" ->
                        sa.assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("size must be between 2 and 2147483647", "размер должен находиться в диапазоне от 2 до 2147483647");
                case "password" ->
                        sa.assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit");
                case "confirmPassword" ->
                        sa.assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                                .containsAnyOf("must not be null", "не должно равняться null");
            }
        }
        sa.assertAll();
    }

    @Test
    void blackCreekUser_TableName() {
        Class<BlackCreekUser> entityClass = BlackCreekUser.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "BlackCreekUser class should be annotated with @Table");

        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("users", tableAnnotation.name(), "Table name should be 'users'");
    }
}
