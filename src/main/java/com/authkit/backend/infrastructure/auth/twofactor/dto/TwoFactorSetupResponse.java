package com.authkit.backend.infrastructure.auth.twofactor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorSetupResponse {
    private String secret;
    private String qrCodeUrl;
    private String pendingToken;
} 