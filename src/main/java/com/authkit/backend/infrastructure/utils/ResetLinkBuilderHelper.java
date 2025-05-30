package com.authkit.backend.infrastructure.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResetLinkBuilderHelper {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public String buildResetPasswordLink(String token) {
        return String.format("%s/reset-password?token=%s", frontendUrl, token);
    }
}

