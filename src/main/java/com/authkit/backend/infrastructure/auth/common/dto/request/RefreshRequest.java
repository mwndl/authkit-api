package com.authkit.backend.infrastructure.auth.common.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotNull(message = "Refresh token must not be null")
    private String refreshToken;

}
