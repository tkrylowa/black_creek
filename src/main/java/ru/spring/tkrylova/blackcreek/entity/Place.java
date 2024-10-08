package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    @NotNull
    @Size(min = 5, max = 100)
    @Column(name = "place_name",
            nullable = false)
    private String placeName;

    @Size(min = 10)
    @Column(name = "location")
    private String location;

    @CreatedDate
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by",
            updatable = false,
            columnDefinition = "VARCHAR(50) NOT NULL DEFAULT 'current_user'")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at",
            insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_active",
            columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE",
            insertable = false)
    private boolean isActive = true;
}