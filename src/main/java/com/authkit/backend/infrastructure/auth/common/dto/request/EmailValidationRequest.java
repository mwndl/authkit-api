package com.authkit.backend.infrastructure.auth.common.dto.request;

import lombok.Data;

@Data
public class EmailValidationRequest {
    private String email;
}