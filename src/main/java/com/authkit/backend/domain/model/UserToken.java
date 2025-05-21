package com.authkit.backend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_tokens")
public class UserToken {

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
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(name = "access_token_expiration", nullable = false)
    private Date accessTokenExpiration;

    @Column(name = "refresh_token_expiration", nullable = false)
    private Date refreshTokenExpiration;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "device_ip")
    private String deviceIp;
}
