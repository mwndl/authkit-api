package com.authkit.backend.infrastructure.auth.passkey.service;

import com.authkit.backend.domain.model.Passkey;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.auth.passkey.PasskeyRepository;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.infrastructure.auth.common.dto.response.TokensResponse;
import com.authkit.backend.infrastructure.auth.common.service.AuthService;
import com.authkit.backend.infrastructure.auth.passkey.dto.*;
import com.authkit.backend.infrastructure.utils.UserServiceHelper;
import com.authkit.backend.infrastructure.utils.audit.Audited;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasskeyService {

    private final PasskeyRepository passkeyRepository;
    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;
    private final AuthService authService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.webauthn.rp.id}")
    private String rpId;

    @Value("${app.webauthn.rp.name}")
    private String rpName;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authentication object: {}", authentication);
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated. Authentication object: {}", authentication);
            throw new ApiException(ApiErrorCode.UNAUTHORIZED);
        }

        String email = authentication.getName();
        log.debug("Email from authentication: {}", email);
        
        User user = userServiceHelper.getUserByEmail(email);
        log.debug("User found in database: {}", user);
        
        if (user == null) {
            log.error("User not found in database for email: {}", email);
            throw new ApiException(ApiErrorCode.USER_NOT_FOUND);
        }
        
        return user;
    }

    @Audited(action = "START_PASSKEY_REGISTRATION", entityType = "USER")
    public PasskeyRegistrationResponse startRegistration(PasskeyRegistrationRequest request) {
        log.debug("Starting passkey registration for request: {}", request);
        User user = getAuthenticatedUser();
        userServiceHelper.checkUserStatus(user);

        String challenge = generateChallenge();
        log.debug("Generated challenge for user {}: {}", user.getEmail(), challenge);

        return new PasskeyRegistrationResponse(
            challenge,
            rpId,
            rpName,
            user.getId().toString(),
            user.getEmail(),
            user.getName() + " " + user.getSurname()
        );
    }

    @Audited(action = "FINISH_PASSKEY_REGISTRATION", entityType = "USER")
    @Transactional
    public void finishRegistration(String credentialId, String publicKey, String deviceName, String deviceType) {
        User user = getAuthenticatedUser();
        userServiceHelper.checkUserStatus(user);

        Passkey passkey = new Passkey();
        passkey.setUser(user);
        passkey.setCredentialId(credentialId);
        passkey.setPublicKey(publicKey);
        passkey.setDeviceName(deviceName);
        passkey.setDeviceType(deviceType);
        passkey.setCreatedAt(LocalDateTime.now());

        passkeyRepository.save(passkey);
    }

    @Audited(action = "START_PASSKEY_VERIFICATION", entityType = "USER")
    public PasskeyVerificationResponse startVerification(String email) {
        User user = userServiceHelper.getUserByEmail(email);
        userServiceHelper.checkUserStatus(user);

        // Get all enabled passkeys for the user
        Page<Passkey> passkeys = passkeyRepository.findByUserAndEnabledTrue(user, Pageable.unpaged());
        
        if (passkeys.isEmpty()) {
            PasskeyVerificationResponse response = new PasskeyVerificationResponse();
            response.setHasPasskey(false);
            return response;
        }

        String challenge = generateChallenge();
        String allowCredentials = passkeys.getContent().stream()
            .map(Passkey::getCredentialId)
            .reduce((a, b) -> a + "," + b)
            .orElse("");

        return new PasskeyVerificationResponse(true, challenge, rpId, allowCredentials);
    }

    @Audited(action = "FINISH_PASSKEY_VERIFICATION", entityType = "USER")
    @Transactional
    public TokensResponse finishVerification(String credentialId, HttpServletRequest request) {
        Passkey passkey = passkeyRepository.findByCredentialId(credentialId)
            .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_CREDENTIALS));

        if (!passkey.isEnabled()) {
            throw new ApiException(ApiErrorCode.INVALID_CREDENTIALS);
        }

        User user = passkey.getUser();
        userServiceHelper.checkUserStatus(user);

        // Update last used timestamp
        passkey.setLastUsedAt(java.time.LocalDateTime.now());
        passkeyRepository.save(passkey);

        // Generate authentication tokens
        return authService.generateAndPersistTokens(user, request);
    }

    @Audited(action = "LIST_PASSKEYS", entityType = "USER")
    public Page<Passkey> listPasskeys(Pageable pageable) {
        User user = getAuthenticatedUser();
        userServiceHelper.checkUserStatus(user);
        return passkeyRepository.findByUserAndEnabledTrue(user, pageable);
    }

    @Audited(action = "DELETE_PASSKEY", entityType = "USER")
    @Transactional
    public void deletePasskey(PasskeyDeleteRequest request) {
        User user = getAuthenticatedUser();
        userServiceHelper.checkUserStatus(user);

        Passkey passkey = passkeyRepository.findByIdAndUserAndEnabledTrue(request.getPasskeyId(), user)
            .orElseThrow(() -> new ApiException(ApiErrorCode.PASSKEY_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) 
            throw new ApiException(ApiErrorCode.INVALID_CREDENTIALS);

        passkeyRepository.delete(passkey);
    }

    private String generateChallenge() {
        byte[] challengeBytes = new byte[32];
        secureRandom.nextBytes(challengeBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes);
    }
} 