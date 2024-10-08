package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "development_plan")
public class DevelopmentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "development_plan_id")
    private Long developmentPlanId;

    @NotNull(message = "Development Plan Name is required")
    @Size(min = 5, max = 25, message = "Development Plan Name must be between 5 and 25 characters")
    @Column(name = "development_plan_name",
            nullable = false)
    private String developmentPlanName;

    @Size(min = 10, message = "Development Plan Description must be at least 10 characters long")
    @Column(name = "development_plan_description")
    private String developmentPlanDescription;

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
