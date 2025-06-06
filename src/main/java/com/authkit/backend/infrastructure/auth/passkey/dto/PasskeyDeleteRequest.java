package com.authkit.backend.infrastructure.auth.passkey.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class PasskeyDeleteRequest {
    private UUID passkeyId;
    private String password;
}
