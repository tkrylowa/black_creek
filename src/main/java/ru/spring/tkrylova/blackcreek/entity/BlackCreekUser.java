package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

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
    @Email(message = "Email should be valid")
    @Column(name = "email",
            nullable = false,
            unique = true)
    private String email;

    @NotBlank(message = "Login is required")
    @Size(min = 6, message = "Login must be between 6 and 20 characters")
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
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=]).{8,}$",
            message = "password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit")
    @Column(name = "password",
            nullable = false)
    private String password;

    @NotNull
    @Column(name = "confirm_password",
            nullable = false)
    private String confirmPassword;

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

    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbacks = new ArrayList<>();

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

    @Column(name = "updated_by",
            updatable = false,
            columnDefinition = "VARCHAR(50) DEFAULT 'current_user'")
    private String updatedBy;
}
