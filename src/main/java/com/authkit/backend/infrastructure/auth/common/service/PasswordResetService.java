package com.authkit.backend.infrastructure.auth.common.service;

import com.authkit.backend.domain.model.PasswordResetToken;
import com.authkit.backend.domain.repository.auth.common.PasswordResetTokenRepository;
import com.authkit.backend.infrastructure.utils.EmailServiceHelper;
import com.authkit.backend.infrastructure.utils.ResetLinkBuilderHelper;
import com.authkit.backend.infrastructure.utils.ValidationServiceHelper;
import com.authkit.backend.infrastructure.utils.audit.Audited;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final ResetLinkBuilderHelper resetLinkBuilderHelper;
    private final EmailServiceHelper emailService;
    private final ValidationServiceHelper validationService;

    @Audited(action = "REQUEST_PASSWORD_RESET", entityType = "USER")
    @Transactional
    public void handleForgotPassword(String email) {
        // Invalidate any existing unused tokens for this email
        tokenRepository.findByEmailAndUsedFalse(email)
            .ifPresent(token -> {
                token.setUsed(true);
                tokenRepository.save(token);
            });

        PasswordResetToken token = createToken(email);
        String resetLink = resetLinkBuilderHelper.buildResetPasswordLink(token.getToken());
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    @Transactional
    public PasswordResetToken createToken(String email) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setEmail(email);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        return tokenRepository.save(token);
    }

    @Transactional
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_TOKEN));

        if (resetToken.isUsed())
            throw new ApiException(ApiErrorCode.ALREADY_USED_TOKEN);

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ApiException(ApiErrorCode.EXPIRED_TOKEN);

        return resetToken;
    }

    @Audited(action = "RESET_PASSWORD", entityType = "USER")
    @Transactional
    public void markTokenAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }

    public void validateNewPassword(String password) {
        validationService.validatePassword(password);
    }
}
