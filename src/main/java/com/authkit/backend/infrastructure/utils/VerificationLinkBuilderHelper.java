package com.authkit.backend.infrastructure.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VerificationLinkBuilderHelper {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public String buildVerificationLink(String token) {
        return String.format("%s/verify/token?token=%s", frontendUrl, token);
    }
} 