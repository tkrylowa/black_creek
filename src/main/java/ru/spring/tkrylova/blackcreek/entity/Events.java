package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @NotNull
    @Size(min = 5, max = 25)
    @Column(name = "event_name",
            nullable = false)
    private String eventName;

    @FutureOrPresent
    @Column(name = "event_start_date")
    private LocalDateTime eventStartDate;

    @Future
    @Column(name = "event_end_date")
    private LocalDateTime eventEndDate;

    @Size(min = 10)
    @Column(name = "event_description")
    private String eventDescription;

    @NotNull
    @Size(min = 2)
    @Column(name = "event_capacity",
            nullable = false)
    private Integer eventCapacity;

    @ManyToOne(targetEntity = EventTypes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    private Integer event_type_id;

    @ManyToOne(targetEntity = BlackCreekUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Integer responsibleUserId;

    @Column(name = "is_free",
            insertable = false,
            columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isFree;

    @PositiveOrZero
    @Column(name = "cost")
    private Double cost;

    @CreatedDate
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by",
            updatable = false,
            columnDefinition = "VARCHAR(50) NOT NULL DEFAULT current_user")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at",
            insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
