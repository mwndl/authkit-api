package com.authkit.backend.infrastructure.auth.passkey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasskeyVerificationResponse {
    private String challenge;
    private String rpId;
    private String allowCredentials;
} 