package com.authkit.backend.infrastructure.auth.common.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}