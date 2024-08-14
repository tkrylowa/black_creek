package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserRoleTest {
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        userRole = new UserRole();
        userRole.setRoleId(1L);
        userRole.setRoleName("ADMIN");
    }

    @Test
    void userRole_DefaultConstructor() {
        UserRole defaultUserRole = new UserRole();
        assertThat(defaultUserRole).isNotNull();
        assertThat(defaultUserRole.getRoleId()).isNull();
        assertThat(defaultUserRole.getRoleName()).isNull();
    }

    @Test
    void userRole_ParameterizedConstructor() {
        UserRole parameterizedUserRole = new UserRole(1L, "ADMIN");
        assertThat(parameterizedUserRole.getRoleId()).isEqualTo(1L);
        assertThat(parameterizedUserRole.getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void userRole_GettersAndSetters() {
        userRole.setRoleId(2L);
        userRole.setRoleName("USER");

        assertThat(userRole.getRoleId()).isEqualTo(2L);
        assertThat(userRole.getRoleName()).isEqualTo("USER");
    }

    @Test
    void userRole_ToString() {
        String expectedString = "UserRole(roleId=1, roleName=ADMIN)";
        assertThat(userRole.toString()).isEqualTo(expectedString);
    }

    @Test
    void userRole_tableName() {
        Class<UserRole> entityClass = UserRole.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "UserRole class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("user_role", tableAnnotation.name(), "Table name should be 'user_role'");
    }
}
