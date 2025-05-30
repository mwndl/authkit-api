package com.authkit.backend.infrastructure.auth.passkey.dto;

import lombok.Data;

@Data
public class PasskeyRegistrationFinishRequest {
    private String credentialId;
    private String publicKey;
    private String deviceName;
    private String deviceType;
} 