package com.authkit.backend.features.v1.auth.twofactor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorSetupResponse {
    private String secret;
    private String qrCodeUrl;
    private String pendingToken;
} 