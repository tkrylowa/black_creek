package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "event_types")
public class EventTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_type_id")
    private int eventTypeId;

    @NotNull
    @Size(min = 5, max = 25)
    @Column(name = "event_type_name",
            nullable = false,
            unique = true)
    private String eventTypeName;

    @Column(name = "is_active",
            columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE",
            insertable = false)
    private boolean isActive;
}
