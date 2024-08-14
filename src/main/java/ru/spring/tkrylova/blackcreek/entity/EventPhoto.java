package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "photo")
public class EventPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id")
    private Long id;
    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne(targetEntity = BlackCreekEvent.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private BlackCreekEvent event;
}
