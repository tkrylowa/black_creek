package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DevelopmentPlanTest {

    @Test
    void developmentPlan_NoArgsConstructor() {
        DevelopmentPlan developmentPlan = new DevelopmentPlan();
        assertNotNull(developmentPlan, "No-args constructor should create an instance of DevelopmentPlan");
    }

    @Test
    void developmentPlan_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        DevelopmentPlan developmentPlan = new DevelopmentPlan(1L, "PlanName", "Description", now, "user1", now, "user2", true);
        assertNotNull(developmentPlan, "All-args constructor should create an instance of DevelopmentPlan");
        assertEquals(1L, developmentPlan.getDevelopmentPlanId(), "developmentPlanId should be set correctly");
        assertEquals("PlanName", developmentPlan.getDevelopmentPlanName(), "developmentPlanName should be set correctly");
        assertEquals("Description", developmentPlan.getDevelopmentPlanDescription(), "developmentPlanDescription should be set correctly");
        assertEquals(now, developmentPlan.getCreatedAt(), "createdAt should be set correctly");
        assertEquals("user1", developmentPlan.getCreatedBy(), "createdBy should be set correctly");
        assertEquals(now, developmentPlan.getUpdatedAt(), "updatedAt should be set correctly");
        assertEquals("user2", developmentPlan.getUpdatedBy(), "updatedBy should be set correctly");
        assertTrue(developmentPlan.isActive(), "isActive should be set correctly");
    }

    @Test
    void developmentPlan_GettersAndSetters() {
        DevelopmentPlan developmentPlan = new DevelopmentPlan();
        LocalDateTime now = LocalDateTime.now();

        developmentPlan.setDevelopmentPlanId(2L);
        developmentPlan.setDevelopmentPlanName("NewPlanName");
        developmentPlan.setDevelopmentPlanDescription("NewDescription");
        developmentPlan.setCreatedAt(now);
        developmentPlan.setCreatedBy("newUser1");
        developmentPlan.setUpdatedAt(now);
        developmentPlan.setUpdatedBy("newUser2");
        developmentPlan.setActive(false);

        assertEquals(2L, developmentPlan.getDevelopmentPlanId(), "getDevelopmentPlanId should return the correct value");
        assertEquals("NewPlanName", developmentPlan.getDevelopmentPlanName(), "getDevelopmentPlanName should return the correct value");
        assertEquals("NewDescription", developmentPlan.getDevelopmentPlanDescription(), "getDevelopmentPlanDescription should return the correct value");
        assertEquals(now, developmentPlan.getCreatedAt(), "getCreatedAt should return the correct value");
        assertEquals("newUser1", developmentPlan.getCreatedBy(), "getCreatedBy should return the correct value");
        assertEquals(now, developmentPlan.getUpdatedAt(), "getUpdatedAt should return the correct value");
        assertEquals("newUser2", developmentPlan.getUpdatedBy(), "getUpdatedBy should return the correct value");
        assertFalse(developmentPlan.isActive(), "isActive should return the correct value");
    }

    @Test
    void developmentPlan_ValidationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        DevelopmentPlan validPlan = new DevelopmentPlan(1L, "PlanName", "A valid description", LocalDateTime.now(), "user1", LocalDateTime.now(), "user2", true);
        Set<ConstraintViolation<DevelopmentPlan>> violations = validator.validate(validPlan);
        assertTrue(violations.isEmpty(), "Valid entity should not have validation errors");

        DevelopmentPlan invalidPlan = new DevelopmentPlan(1L, "Pl", "Short", LocalDateTime.now(), "user1", LocalDateTime.now(), "user2", true);
        violations = validator.validate(invalidPlan);
        assertFalse(violations.isEmpty(), "Invalid entity should have validation errors");
        assertEquals(2, violations.size(), "There should be two validation errors");

        for (ConstraintViolation<DevelopmentPlan> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            if (propertyPath.equals("developmentPlanName")) {
                assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                        .containsAnyOf("size must be between 5 and 25", "размер должен находиться в диапазоне от 5 до 25");
            } else if (propertyPath.equals("developmentPlanDescription")) {
                assertThat(violation.getMessage()).as("Validation message should indicate size constraint")
                        .containsAnyOf("size must be between 10 and 2147483647", "размер должен находиться в диапазоне от 10 до 2147483647");
            }
        }
    }

    @Test
    void developmentPlan_TableName() {
        Class<DevelopmentPlan> entityClass = DevelopmentPlan.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "DevelopmentPlan class should be annotated with @Table");

        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("development_plan", tableAnnotation.name(), "Table name should be 'development_plan'");
    }
}
