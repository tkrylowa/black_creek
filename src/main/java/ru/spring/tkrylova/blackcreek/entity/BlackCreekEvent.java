package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "events")
public class BlackCreekEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @NotNull
    @Size(min = 5, max = 25)
    @Column(name = "event_name",
            nullable = false)
    private String eventName;

    @FutureOrPresent
    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    @Future
    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    @Size(min = 10)
    @Column(name = "event_description")
    private String eventDescription;

    @NotNull
    @Min(2)
    @Column(name = "event_capacity",
            nullable = false)
//    how many people can be attended on event
    private Integer eventCapacity;

    @ManyToOne(targetEntity = EventTypes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    private Long eventTypeId;

    @ManyToOne(targetEntity = BlackCreekUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Long responsibleUserId;

    @Column(name = "is_free",
            insertable = false,
            columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isFree;

    @Column(name = "is_cancelled",
            insertable = false,
            columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isCancelled;

    @PositiveOrZero
    @Column(name = "cost")
//    cost of attend
    private Double cost;

    @ManyToMany
    @JoinTable(
            name = "event_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<BlackCreekUser> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "event_attendance",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<BlackCreekUser> attendees = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by",
            updatable = false,
            columnDefinition = "VARCHAR(50) DEFAULT 'current_user'")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at",
            insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
