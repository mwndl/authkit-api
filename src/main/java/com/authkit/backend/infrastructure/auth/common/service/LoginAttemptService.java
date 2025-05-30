package com.authkit.backend.infrastructure.auth.common.service;

import com.authkit.backend.domain.model.LoginAttempt;
import com.authkit.backend.domain.repository.auth.common.LoginAttemptRepository;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    @Transactional(noRollbackFor = ApiException.class)
    public void handleFailedLoginAttempt(LoginAttempt loginAttempt) {
        loginAttempt.setAttemptCount(loginAttempt.getAttemptCount() + 1);
        loginAttempt.setLastAttemptAt(LocalDateTime.now());

        int waitSeconds = calculateWaitTime(loginAttempt.getAttemptCount());
        if (waitSeconds > 0)
            loginAttempt.setBlockedUntil(LocalDateTime.now().plusSeconds(waitSeconds));

        loginAttemptRepository.save(loginAttempt);

        throw new ApiException(
                ApiErrorCode.INVALID_CREDENTIALS,
                Map.of("Retry-After", String.valueOf(waitSeconds))
        );
    }

    private LoginAttempt createNewLoginAttempt(String ip, String userAgent, String email) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setIp(ip);
        attempt.setUserAgent(userAgent);
        attempt.setEmail(email);
        attempt.setAttemptCount(0);
        attempt.setLastAttemptAt(LocalDateTime.now());
        attempt.setBlockedUntil(null);
        return loginAttemptRepository.save(attempt);
    }

    public LoginAttempt getOrCreateLoginAttempt(String ip, String userAgent, String email) {
        return loginAttemptRepository
                .findByIpAndUserAgentAndEmail(ip, userAgent, email)
                .orElseGet(() -> createNewLoginAttempt(ip, userAgent, email));
    }

    @Transactional
    public void clearLoginAttempts(String ip, String email) {
        loginAttemptRepository.deleteAllByIpAndEmail(ip, email);
    }

    int calculateWaitTime(int attempts) {
        return switch (attempts) {
            case 1, 2 -> 0;
            case 3 -> 5; // 5s
            case 4 -> 15; // 15s
            case 5 -> 60; // 1min
            case 6 -> 300; // 5mins
            case 7 -> 3600; // 1h
            case 8 -> 86400; // 1 day
            default -> 86400;
        };
    }
}
