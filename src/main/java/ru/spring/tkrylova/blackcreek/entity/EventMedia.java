package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "event_media")
public class EventMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_media_id")
    private Long eventMediaId;

    @NotNull
    @Size(min = 5, max = 25)
    @Column(name = "event_media",
            nullable = false)
    private String eventMediaPath;

    @ManyToOne(targetEntity = BlackCreekEvent.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Long eventId;
}
