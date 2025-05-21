package com.authkit.backend.features.v1.auth.twofactor.dto;

import lombok.Data;

@Data
public class TwoFactorVerificationRequest {
    private String pendingToken;
    private String code;
}
