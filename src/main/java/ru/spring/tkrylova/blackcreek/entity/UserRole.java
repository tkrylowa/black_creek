package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;
}
