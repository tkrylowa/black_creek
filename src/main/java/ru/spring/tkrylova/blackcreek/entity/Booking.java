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
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(targetEntity = BlackCreekEvent.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Long eventId;

    @ManyToOne(targetEntity = BlackCreekUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Long userId;

    @CreatedDate
    @Column(name = "booking_date",
            updatable = false)
    private LocalDateTime bookingDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false)
    private TimeSlot timeSlot;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

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
