package com.authkit.backend.features.v1.auth.common.service;

import com.authkit.backend.domain.model.PasswordResetToken;
import com.authkit.backend.features.v1.auth.common.repository.PasswordResetTokenRepository;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public PasswordResetToken createToken(String email) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setEmail(email);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        return tokenRepository.save(token);
    }

    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_TOKEN));

        if (resetToken.isUsed())
            throw new ApiException(ApiErrorCode.ALREADY_USED_TOKEN);

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ApiException(ApiErrorCode.EXPIRED_TOKEN);

        return resetToken;
    }

    public void markTokenAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }
}
