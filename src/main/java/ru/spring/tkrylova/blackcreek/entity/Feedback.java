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
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private BlackCreekEvent event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BlackCreekUser user;

    @Column(name = "comments")
    private String comments;

    @Column(name = "rating")
    private int rating;

    @CreatedDate
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;
}
