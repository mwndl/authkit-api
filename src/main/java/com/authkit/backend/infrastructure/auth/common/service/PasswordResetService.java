package com.authkit.backend.infrastructure.auth.common.service;

import com.authkit.backend.domain.model.PasswordResetToken;
import com.authkit.backend.domain.repository.auth.common.PasswordResetTokenRepository;
import com.authkit.backend.infrastructure.utils.EmailServiceHelper;
import com.authkit.backend.infrastructure.utils.ResetLinkBuilderHelper;
import com.authkit.backend.infrastructure.utils.audit.Audited;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final ResetLinkBuilderHelper resetLinkBuilderHelper;
    private final EmailServiceHelper emailService;

    @Audited(action = "REQUEST_PASSWORD_RESET", entityType = "USER")
    public void handleForgotPassword(String email) {
        PasswordResetToken token = createToken(email);
        String resetLink = resetLinkBuilderHelper.buildResetPasswordLink(token.getToken());

        String message = "Click the link to reset your password: " + resetLink;
        emailService.sendEmail(email, "Password Reset", message);
    }

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
