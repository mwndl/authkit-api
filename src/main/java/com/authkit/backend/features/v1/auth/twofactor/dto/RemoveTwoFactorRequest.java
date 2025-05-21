package com.authkit.backend.features.v1.auth.twofactor.dto;

import com.authkit.backend.domain.enums.TwoFactorMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveTwoFactorRequest {
    @NotBlank
    private String password;

    @NotNull
    private TwoFactorMethod method;
}
