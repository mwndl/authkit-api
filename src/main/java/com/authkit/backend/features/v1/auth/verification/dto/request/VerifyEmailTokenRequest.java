package com.authkit.backend.features.v1.auth.verification.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailTokenRequest {
    @NotBlank(message = "Verification token is required")
    private String token;
} 