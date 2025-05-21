package com.authkit.backend.features.v1.auth.twofactor.dto;

import com.authkit.backend.domain.enums.TwoFactorMethod;
import lombok.Data;

@Data
public class VerifyTwoFactorRequest {
    private TwoFactorMethod method;
    private String code;
}