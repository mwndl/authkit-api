package com.authkit.backend.infrastructure.auth.verification.service;

import com.authkit.backend.domain.enums.UserStatus;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.VerificationToken;
import com.authkit.backend.domain.model.VerificationResendAttempt;
import com.authkit.backend.domain.repository.auth.verification.VerificationTokenRepository;
import com.authkit.backend.domain.repository.auth.verification.VerificationResendAttemptRepository;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.security.JwtService;
import com.authkit.backend.infrastructure.auth.common.dto.response.TokensResponse;
import com.authkit.backend.infrastructure.auth.common.service.AuthService;
import com.authkit.backend.infrastructure.utils.audit.Audited;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationResendAttemptRepository resendAttemptRepository;
    private final JwtService jwtService;
    private final AuthService authService;
    private final VerificationEmailService verificationEmailService;
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    // Retry-after durations in seconds
    private static final int[] RETRY_AFTER_DURATIONS = {
        0,      // First attempt: immediate
        30,     // Second attempt: 30 seconds
        60,     // Third attempt: 1 minute
        180,    // Fourth attempt: 3 minutes
        600,    // Fifth attempt: 10 minutes
        1800,   // Sixth attempt: 30 minutes
        3600,   // Seventh attempt: 1 hour
        21600   // Eighth attempt and beyond: 6 hours
    };

    @Audited(action = "SEND_VERIFICATION_EMAIL", entityType = "USER")
    public void sendVerificationEmail(User user) {
        verificationEmailService.sendVerificationEmail(user);
    }

    @Audited(action = "VERIFY_EMAIL", entityType = "USER")
    @Transactional
    public TokensResponse verifyEmail(String token, HttpServletRequest request) {
        String email = jwtService.extractUsername(token);
        if (email == null || jwtService.isTokenExpired(token)) {
            throw new ApiException(ApiErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new ApiException(ApiErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }

        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndUsedFalse(token)
            .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_TOKEN));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ApiErrorCode.EXPIRED_TOKEN);
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        // Clean up resend attempts when email is verified
        resendAttemptRepository.findByUser(user).ifPresent(resendAttemptRepository::delete);

        return authService.generateAndPersistTokens(user, request);
    }

    @Audited(action = "VERIFY_EMAIL_CODE", entityType = "USER")
    @Transactional
    public void verifyEmailWithCode(String email, String code) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new ApiException(ApiErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }

        VerificationToken verificationToken = verificationTokenRepository.findByUserAndUsedFalse(user)
            .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_TOKEN));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ApiErrorCode.EXPIRED_TOKEN);
        }

        if (!verificationToken.getCode().equals(code)) {
            throw new ApiException(ApiErrorCode.INVALID_2FA_CODE);
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        // Clean up resend attempts when email is verified
        resendAttemptRepository.findByUser(user).ifPresent(resendAttemptRepository::delete);
    }

    @Audited(action = "RESEND_VERIFICATION_EMAIL", entityType = "USER")
    @Transactional
    public Map<String, Object> resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new ApiException(ApiErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        LocalDateTime now = LocalDateTime.now();
        VerificationResendAttempt attempt = resendAttemptRepository.findByUser(user)
            .orElseGet(() -> {
                VerificationResendAttempt newAttempt = new VerificationResendAttempt();
                newAttempt.setUser(user);
                newAttempt.setAttemptCount(0);
                newAttempt.setLastAttemptAt(now);
                return newAttempt;
            });

        int retryAfterSeconds = getRetryAfterSeconds(attempt.getAttemptCount());
        LocalDateTime nextAttemptTime = attempt.getLastAttemptAt().plusSeconds(retryAfterSeconds);

        if (now.isBefore(nextAttemptTime)) {
            throw new ApiException(ApiErrorCode.TOO_MANY_REQUESTS, Map.of(
                "retryAfter", String.valueOf(Duration.between(now, nextAttemptTime).getSeconds())
            ));
        }

        // Invalidate any existing unused tokens
        verificationTokenRepository.findByUserAndUsedFalse(user)
            .ifPresent(token -> {
                token.setUsed(true);
                verificationTokenRepository.save(token);
            });

        attempt.setAttemptCount(attempt.getAttemptCount() + 1);
        attempt.setLastAttemptAt(now);
        resendAttemptRepository.save(attempt);

        verificationEmailService.sendVerificationEmail(user);

        return Map.of(
            "retryAfter", getRetryAfterSeconds(attempt.getAttemptCount())
        );
    }

    private int getRetryAfterSeconds(int attemptCount) {
        int index = Math.min(attemptCount, RETRY_AFTER_DURATIONS.length - 1);
        return RETRY_AFTER_DURATIONS[index];
    }
} 