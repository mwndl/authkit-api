package com.authkit.backend.features.v1.auth.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokensResponse {
    private String accessToken;
    private Date accessTokenExpiresAt;
    private String refreshToken;
    private Date refreshTokenExpiresAt;
}

