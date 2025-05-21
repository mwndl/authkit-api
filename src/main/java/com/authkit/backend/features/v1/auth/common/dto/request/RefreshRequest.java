package com.authkit.backend.features.v1.auth.common.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotNull(message = "Refresh token must not be null")
    private String refreshToken;

}
