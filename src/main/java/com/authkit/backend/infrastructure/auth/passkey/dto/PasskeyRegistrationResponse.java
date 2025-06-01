package com.authkit.backend.infrastructure.auth.passkey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasskeyRegistrationResponse {
    private String challenge;
    private String rpId;
    private String rpName;
    private String userId;
    private String userName;
    private String userDisplayName;
} 