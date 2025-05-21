package com.authkit.backend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "login_attempt",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "ip", "userAgent"})
)
@Data
public class LoginAttempt {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcType(org.hibernate.type.descriptor.jdbc.CharJdbcType.class)
    @Column(updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private LocalDateTime lastAttemptAt;

    private LocalDateTime blockedUntil;
}

