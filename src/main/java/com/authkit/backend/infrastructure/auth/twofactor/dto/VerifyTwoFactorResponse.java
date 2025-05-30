package com.authkit.backend.infrastructure.auth.twofactor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyTwoFactorResponse {
    private boolean success;
    private String message;
}
