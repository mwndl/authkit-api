package com.authkit.backend.infrastructure.auth.passkey.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasskeyRegistrationRequest {
    @NotBlank(message = "Device name is required")
    private String deviceName;
    
    @NotBlank(message = "Device type is required")
    private String deviceType;
} 