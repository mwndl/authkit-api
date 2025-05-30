package com.authkit.backend.infrastructure.auth.twofactor.dto;

import lombok.Data;

@Data
public class TwoFactorVerificationRequest {
    private String pendingToken;
    private String code;
}
