package com.authkit.backend.features.v1.auth.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean twoFactorRequired;
    private String pendingToken;
    private TokensResponse auth;
}