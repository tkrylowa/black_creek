package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class BlackCreekUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(targetEntity = UserTypes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Long typeId;

    @NotNull
    @Email
    @Column(name = "email",
            nullable = false,
            unique = true)
    private String email;

    @NotBlank(message = "Login is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$",
            message = "login must be of 6 to 12 length with no special characters")
    @Column(name = "login",
            nullable = false,
            unique = true)
    private String login;

    @NotNull
    @Size(min = 2)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 2)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Pattern(regexp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])){8,16}$",
            message = "password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit")
    @Size(min = 8, max = 16)
    @Column(name = "password",
            nullable = false,
            unique = true)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole userRole;

    @Column(name = "user_token",
            insertable = false)
    private String userToken;

    @Column(name = "token_expiration",
            insertable = false)
    private String tokenExpiration;

    @Column(name = "logged_in",
            insertable = false,
            columnDefinition = "BOOLEAN DEFAULT false")
    private boolean loggedIn;

    @ManyToMany(mappedBy = "attendees")
    private Set<BlackCreekEvent> attendedEvents = new HashSet<>();

    @OneToMany(mappedBy = "users")
    private Set<Feedback> feedbacks = new HashSet<>();

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

    @Column(name = "updated_by",
            updatable = false,
            columnDefinition = "VARCHAR(50) NOT NULL DEFAULT current_user")
    private String updatedBy;
}
