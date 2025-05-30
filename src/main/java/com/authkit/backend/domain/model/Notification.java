package com.authkit.backend.domain.model;

import com.authkit.backend.domain.enums.NotificationCode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
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
    @JdbcType(CharJdbcType.class)
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationCode code;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> params;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean readStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(UUID userId, NotificationCode code, Map<String, Object> params, String type) {
        this.userId = userId;
        this.code = code;
        this.params = params;
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