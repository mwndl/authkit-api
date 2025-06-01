package com.authkit.backend.infrastructure.auth.passkey.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasskeyVerificationFinishRequest {
    @NotBlank(message = "Credential ID is required")
    private String credentialId;
    
    @NotBlank(message = "Signature is required")
    private String signature;
    
    @NotBlank(message = "Client data JSON is required")
    private String clientDataJSON;
    
    @NotBlank(message = "Authenticator data is required")
    private String authenticatorData;
} 