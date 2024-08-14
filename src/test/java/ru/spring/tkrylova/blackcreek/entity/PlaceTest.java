package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlaceTest {

    @Test
    void place_NoArgsConstructor() {
        Place place = new Place();
        assertNotNull(place, "No-args constructor should create an instance of Place");
    }

    @Test
    void place_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Place place = new Place(1L, "PlaceName", "Location", now, "user1", now, "user2", true);

        assertNotNull(place, "All-args constructor should create an instance of Place");
        assertEquals(1L, place.getPlaceId(), "placeId should be set correctly");
        assertEquals("PlaceName", place.getPlaceName(), "placeName should be set correctly");
        assertEquals("Location", place.getLocation(), "location should be set correctly");
        assertEquals(now, place.getCreatedAt(), "createdAt should be set correctly");
        assertEquals("user1", place.getCreatedBy(), "createdBy should be set correctly");
        assertEquals(now, place.getUpdatedAt(), "updatedAt should be set correctly");
        assertEquals("user2", place.getUpdatedBy(), "updatedBy should be set correctly");
        assertTrue(place.isActive(), "isActive should be set correctly");
    }

    @Test
    void place_GettersAndSetters() {
        Place place = new Place();
        LocalDateTime now = LocalDateTime.now();

        place.setPlaceId(2L);
        place.setPlaceName("NewPlaceName");
        place.setLocation("NewLocation");
        place.setCreatedAt(now);
        place.setCreatedBy("newUser1");
        place.setUpdatedAt(now);
        place.setUpdatedBy("newUser2");
        place.setActive(false);

        assertEquals(2L, place.getPlaceId(), "getPlaceId should return the correct value");
        assertEquals("NewPlaceName", place.getPlaceName(), "getPlaceName should return the correct value");
        assertEquals("NewLocation", place.getLocation(), "getLocation should return the correct value");
        assertEquals(now, place.getCreatedAt(), "getCreatedAt should return the correct value");
        assertEquals("newUser1", place.getCreatedBy(), "getCreatedBy should return the correct value");
        assertEquals(now, place.getUpdatedAt(), "getUpdatedAt should return the correct value");
        assertEquals("newUser2", place.getUpdatedBy(), "getUpdatedBy should return the correct value");
        assertFalse(place.isActive(), "isActive should return the correct value");
    }

    @Test
    void place_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Place validPlace = new Place(1L, "PlaceName", "A valid location", LocalDateTime.now(), "user1", LocalDateTime.now(), "user2", true);
        Set<ConstraintViolation<Place>> violations = validator.validate(validPlace);
        assertTrue(violations.isEmpty(), "Valid entity should not have validation errors");

        Place invalidPlace = new Place(1L, "Pl", "Loc", LocalDateTime.now(), "user1", LocalDateTime.now(), "user2", true);
        violations = validator.validate(invalidPlace);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(2, violations.size(), "There should be two validation errors");

        for (ConstraintViolation<Place> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            if (propertyPath.equals("placeName")) {
                assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                        .containsAnyOf("size must be between 5 and 100", "размер должен находиться в диапазоне от 5 до 100");
            } else if (propertyPath.equals("location")) {
                assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                        .containsAnyOf("size must be between 10 and 2147483647", "размер должен находиться в диапазоне от 10 до 2147483647");
            }
        }
    }

    @Test
    void place_IsActiveColumn() throws NoSuchFieldException {
        Class<Place> entityClass = Place.class;

        Field field = entityClass.getDeclaredField("isActive");
        assertTrue(field.isAnnotationPresent(Column.class), "isActive field should be annotated with @Column");

        Column columnAnnotation = field.getAnnotation(Column.class);
        assertEquals("is_active", columnAnnotation.name(), "Column name for isActive should be 'is_active'");
        assertEquals("BOOLEAN NOT NULL DEFAULT TRUE", columnAnnotation.columnDefinition(), "Column definition should match");
        assertFalse(columnAnnotation.insertable(), "isActive column should not be insertable");
    }

    @Test
    void place_TableName() {
        Class<Place> entityClass = Place.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "Place class should be annotated with @Table");

        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("places", tableAnnotation.name(), "Table name should be 'places'");
    }
}
