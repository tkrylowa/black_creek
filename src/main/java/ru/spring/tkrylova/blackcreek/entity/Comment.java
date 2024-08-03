package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(targetEntity = BlackCreekEvent.class, fetch = FetchType.LAZY)
    private BlackCreekEvent event;

    @ManyToOne(targetEntity = BlackCreekUser.class, fetch = FetchType.LAZY)
    private BlackCreekUser user;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;
}
