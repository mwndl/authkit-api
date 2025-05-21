package com.authkit.backend.features.v1.auth.twofactor.dto;

import com.authkit.backend.domain.enums.TwoFactorMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnableTwoFactorResponse {
    private TwoFactorMethod method;
    private String secret;
    private String qrCodeUrl;
}
