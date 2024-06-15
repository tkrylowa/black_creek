package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_types")
public class UserTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private int typeId;

    @NotNull
    @Size(min = 5, max = 25)
    @Column(name = "user_type_name",
            nullable = false,
            unique = true)
    private String userTypeName;

    @Column(name = "is_active",
            columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE",
            insertable = false)
    private boolean isActive;
}
