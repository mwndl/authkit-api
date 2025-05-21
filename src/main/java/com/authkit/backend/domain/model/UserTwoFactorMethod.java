package com.authkit.backend.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.authkit.backend.domain.enums.TwoFactorMethod;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_two_factor_methods", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "method"})
})
@Data
public class UserTwoFactorMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TwoFactorMethod method;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "secret")
    private String secret;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
