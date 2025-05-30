package com.authkit.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcType;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcType(org.hibernate.type.descriptor.jdbc.CharJdbcType.class)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean readStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(UUID userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.readStatus = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.readStatus = true;
    }

    public boolean isUnread() {
        return !this.readStatus;
    }
} 