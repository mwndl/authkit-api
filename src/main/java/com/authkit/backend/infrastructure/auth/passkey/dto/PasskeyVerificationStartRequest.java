package com.authkit.backend.infrastructure.auth.passkey.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasskeyVerificationStartRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
} 