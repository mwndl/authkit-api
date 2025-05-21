package com.authkit.backend.features.v1.auth.twofactor.dto;

import com.authkit.backend.domain.enums.TwoFactorMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TwoFactorStatusResponse {
    private boolean enabled;
    private List<TwoFactorMethod> enabledMethods;
    private TwoFactorMethod preferredMethod;
}