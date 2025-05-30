package com.authkit.backend.infrastructure.auth.twofactor.dto;

import com.authkit.backend.domain.enums.TwoFactorMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFactorSetupRequest {
    @NotNull
    private TwoFactorMethod method;
} 