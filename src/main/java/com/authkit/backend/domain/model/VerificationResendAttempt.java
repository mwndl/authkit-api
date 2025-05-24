package com.authkit.backend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "verification_resend_attempts")
public class VerificationResendAttempt {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcType(org.hibernate.type.descriptor.jdbc.CharJdbcType.class)
    @Column(updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int attemptCount;

    @Column(name = "last_attempt_at", nullable = false)
    private LocalDateTime lastAttemptAt;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastAttemptAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 