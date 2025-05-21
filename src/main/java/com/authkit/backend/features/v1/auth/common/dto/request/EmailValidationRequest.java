package com.authkit.backend.features.v1.auth.common.dto.request;

import lombok.Data;

@Data
public class EmailValidationRequest {
    private String email;
}