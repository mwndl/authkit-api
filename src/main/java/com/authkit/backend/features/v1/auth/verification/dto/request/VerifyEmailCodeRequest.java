package com.authkit.backend.features.v1.auth.verification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyEmailCodeRequest {
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[A-Z0-9]{6}$", message = "Verification code must be 6 alphanumeric characters")
    private String code;
} 