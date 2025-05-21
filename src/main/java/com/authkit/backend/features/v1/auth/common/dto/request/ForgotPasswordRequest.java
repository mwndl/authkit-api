package com.authkit.backend.features.v1.auth.common.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}