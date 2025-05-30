package com.authkit.backend.shared.utils;

import com.authkit.backend.domain.model.LoginAttempt;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.auth.common.UserTokenRepository;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.infrastructure.auth.common.dto.request.LoginRequest;
import com.authkit.backend.infrastructure.auth.common.service.LoginAttemptService;
import com.authkit.backend.domain.enums.UserStatus;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginUtil {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserTokenRepository userTokenRepository;
    private final LoginAttemptService loginAttemptService;

    public void authenticateUser(LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        String email = request.getEmail();

        LoginAttempt loginAttempt = loginAttemptService.getOrCreateLoginAttempt(ip, userAgent, email);

        if (loginAttempt.getBlockedUntil() != null && loginAttempt.getBlockedUntil().isAfter(LocalDateTime.now())) {
            long waitSeconds = Duration.between(LocalDateTime.now(), loginAttempt.getBlockedUntil()).getSeconds();
            throw new ApiException(ApiErrorCode.TOO_MANY_LOGIN_ATTEMPTS, Map.of("Retry-After", String.valueOf(waitSeconds)));
        }

        try {
            authenticateWithCredentials(request);
            loginAttemptService.clearLoginAttempts(ip, email);
        } catch (BadCredentialsException e) {
            loginAttemptService.handleFailedLoginAttempt(loginAttempt);
        }
    }

    private void authenticateWithCredentials(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
    }

    public User validateAndGetUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(ApiErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    public String generateAccessTokenForUser(User user) {
        UserDetails userDetails = buildUserDetails(user);
        userRepository.save(user);
        return jwtUtil.generateToken(userDetails);
    }

    public String generateRefreshTokenForUser(User user) {
        UserDetails userDetails = buildUserDetails(user);
        return jwtUtil.generateRefreshToken(userDetails);
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER")
                .accountLocked(
                        user.getStatus() == UserStatus.DEACTIVATION_REQUESTED || user.getStatus() == UserStatus.LOCKED
                )
                .build();
    }

    public void validateRefreshToken(String refreshToken, UserDetails userDetails) {
        if (!jwtUtil.isTokenValid(refreshToken, userDetails))
            throw new ApiException(ApiErrorCode.INVALID_REFRESH_TOKEN);
        if (jwtUtil.isTokenExpired(refreshToken))
            throw new ApiException(ApiErrorCode.EXPIRED_REFRESH_TOKEN);
        if (isRefreshTokenRevoked(refreshToken))
            throw new ApiException(ApiErrorCode.REVOKED_REFRESH_TOKEN);
    }

    public boolean isRefreshTokenRevoked(String refreshToken) {
        return userTokenRepository.findByRefreshTokenAndRevokedTrue(refreshToken).isPresent();
    }
} 