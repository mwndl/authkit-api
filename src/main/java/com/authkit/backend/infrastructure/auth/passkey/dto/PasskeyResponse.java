package com.authkit.backend.infrastructure.auth.passkey.dto;

import com.authkit.backend.domain.model.Passkey;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PasskeyResponse {
    private UUID id;
    private String credentialId;
    private String deviceName;
    private String deviceType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private boolean enabled;

    public static PasskeyResponse fromEntity(Passkey passkey) {
        PasskeyResponse response = new PasskeyResponse();
        response.setId(passkey.getId());
        response.setCredentialId(passkey.getCredentialId());
        response.setDeviceName(passkey.getDeviceName());
        response.setDeviceType(passkey.getDeviceType());
        response.setCreatedAt(passkey.getCreatedAt());
        response.setLastUsedAt(passkey.getLastUsedAt());
        response.setEnabled(passkey.isEnabled());
        return response;
    }
} 