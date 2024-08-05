package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

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

    @ManyToOne
    @JoinColumn(name = "event_id")
    private BlackCreekEvent event;
}
