package com.authkit.backend.features.v1.auth.verification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationResponse {
    private int retryAfter;
} 