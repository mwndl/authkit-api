package com.authkit.backend.infrastructure.auth.passkey.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasskeyVerificationResponse {
    private boolean hasPasskey;
    private String challenge;
    private String rpId;
    private String allowCredentials;
} 