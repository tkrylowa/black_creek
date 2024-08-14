package ru.spring.tkrylova.blackcreek.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.Table;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTypesTest {
    @Test
    void userTypes_NoArgsConstructor() {
        UserTypes userTypes = new UserTypes();
        assertNotNull(userTypes, "No-args constructor should create an instance of UserTypes");
    }

    @Test
    void userTypes_AllArgsConstructor() {
        UserTypes userTypes = new UserTypes(1L, "Admin", true);
        assertNotNull(userTypes, "All-args constructor should create an instance of UserTypes");
        assertEquals(1L, userTypes.getTypeId(), "typeId should be set correctly");
        assertEquals("Admin", userTypes.getUserTypeName(), "userTypeName should be set correctly");
        assertTrue(userTypes.isActive(), "isActive should be set correctly");
    }

    @Test
    void userTypes_GettersAndSetters() {
        UserTypes userTypes = new UserTypes();
        userTypes.setTypeId(2L);
        userTypes.setUserTypeName("User");
        userTypes.setActive(false);

        assertEquals(2L, userTypes.getTypeId(), "getTypeId should return the correct value");
        assertEquals("User", userTypes.getUserTypeName(), "getUserTypeName should return the correct value");
        assertFalse(userTypes.isActive(), "isActive should return the correct value");
    }

    @Test
    void userTypes_DefaultValues() {
        UserTypes userTypes = new UserTypes();
        assertTrue(userTypes.isActive(), "Default value for isActive should be true");
    }

    @Test
    void userTypes_TableName() {
        Class<UserTypes> entityClass = UserTypes.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "UserTypes class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("user_types", tableAnnotation.name(), "Table name should be 'user_types'");
    }
}
