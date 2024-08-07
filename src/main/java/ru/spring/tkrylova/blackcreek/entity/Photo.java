package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id")
    private Long id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_path")
    private String filePath;

    @ManyToOne(targetEntity = BlackCreekEvent.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private BlackCreekEvent event;
}
