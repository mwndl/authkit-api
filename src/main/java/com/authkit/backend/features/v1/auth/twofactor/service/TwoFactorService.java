package com.authkit.backend.features.v1.auth.twofactor.service;

import com.authkit.backend.domain.enums.TwoFactorMethod;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.UserTwoFactorMethod;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.features.v1.auth.common.dto.response.TokensResponse;
import com.authkit.backend.features.v1.auth.common.service.AuthService;
import com.authkit.backend.features.v1.auth.twofactor.dto.RemoveTwoFactorRequest;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorMethodResponse;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorSetupRequest;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorSetupResponse;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorVerificationRequest;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.security.JwtService;
import com.authkit.backend.features.v1.utils.audit.Audited;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TwoFactorService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtService jwtService;
    private final TOTPService totpService;
    private final PasswordEncoder passwordEncoder;

    @Audited(action = "ENABLE_2FA", entityType = "USER")
    @Transactional
    public TwoFactorSetupResponse setup2FA(TwoFactorSetupRequest request, HttpServletRequest httpRequest) {
        String username = jwtService.extractUsernameFromRequest(httpRequest);
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));

        // Check if method is already enabled
        Optional<UserTwoFactorMethod> existingMethod = user.getTwoFactorMethods().stream()
            .filter(method -> method.getMethod() == request.getMethod())
            .findFirst();
        
        if (existingMethod.isPresent() && existingMethod.get().isEnabled()) {
            throw new ApiException(ApiErrorCode.TWO_FACTOR_ALREADY_ENABLED);
        }

        // Generate setup data based on method
        String secret = null;
        String qrCodeUrl = null;

        if (request.getMethod() == TwoFactorMethod.TOTP) {
            secret = totpService.generateSecret();
            qrCodeUrl = totpService.generateQRCodeUrl(user.getEmail(), secret);
        } else {
            throw new ApiException(ApiErrorCode.INVALID_2FA_METHOD);
        }

        UserTwoFactorMethod twoFactorMethod;
        if (existingMethod.isPresent()) {
            // Update existing method
            twoFactorMethod = existingMethod.get();
            twoFactorMethod.setSecret(secret);
            twoFactorMethod.setEnabled(false);
            twoFactorMethod.setVerified(false);
        } else {
            // Create new method
            twoFactorMethod = new UserTwoFactorMethod();
            twoFactorMethod.setUser(user);
            twoFactorMethod.setMethod(request.getMethod());
            twoFactorMethod.setSecret(secret);
            twoFactorMethod.setEnabled(false);
            twoFactorMethod.setVerified(false);
            user.getTwoFactorMethods().add(twoFactorMethod);
        }
        
        userRepository.save(user);

        // Generate pending token for verification
        String pendingToken = jwtService.generatePendingToken(user);

        return TwoFactorSetupResponse.builder()
            .secret(secret)
            .qrCodeUrl(qrCodeUrl)
            .pendingToken(pendingToken)
            .build();
    }

    @Audited(action = "VERIFY_2FA", entityType = "USER")
    public TokensResponse verify2FA(TwoFactorVerificationRequest request, HttpServletRequest httpRequest) {
        String username = jwtService.extractUsername(request.getPendingToken());

        if (username == null || jwtService.isTokenExpired(request.getPendingToken())) 
            throw new ApiException(ApiErrorCode.INVALID_PENDING_TOKEN);

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));

        // Try to find a method that needs verification (setup)
        Optional<UserTwoFactorMethod> totpMethodOpt = user.getTwoFactorMethods().stream()
            .filter(method -> method.getMethod() == TwoFactorMethod.TOTP)
            .filter(method -> !method.isEnabled())
            .filter(method -> !method.isVerified())
            .findFirst();

        // If not found, try to find an enabled method (login)
        if (totpMethodOpt.isEmpty()) {
            totpMethodOpt = user.getTwoFactorMethods().stream()
                .filter(method -> method.getMethod() == TwoFactorMethod.TOTP)
                .filter(method -> method.isEnabled())
                .filter(method -> method.isVerified())
                .findFirst();
        }

        if (totpMethodOpt.isEmpty()) 
            throw new ApiException(ApiErrorCode.TWO_FACTOR_NOT_ENABLED);
        
        UserTwoFactorMethod totpMethod = totpMethodOpt.get();

        if (!totpService.verifyCode(totpMethod, request.getCode())) 
            throw new ApiException(ApiErrorCode.INVALID_2FA_CODE);

        // If this is a setup verification, enable and verify the method
        if (!totpMethod.isEnabled()) {
            totpMethod.setEnabled(true);
            totpMethod.setVerified(true);
            userRepository.save(user);
        }

        return authService.generateAndPersistTokens(user, httpRequest);
    }

    @Audited(action = "DISABLE_2FA", entityType = "USER")
    @Transactional
    public void disable2FA(RemoveTwoFactorRequest request, HttpServletRequest httpRequest) {
        String username = jwtService.extractUsernameFromRequest(httpRequest);
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));

        // Find the method to disable
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) 
            throw new ApiException(ApiErrorCode.INVALID_CREDENTIALS);

        Optional<UserTwoFactorMethod> methodOpt = user.getTwoFactorMethods().stream()
            .filter(m -> m.getMethod() == request.getMethod() && m.isEnabled())
            .findFirst();

        if (methodOpt.isEmpty()) 
            throw new ApiException(ApiErrorCode.TWO_FACTOR_NOT_ENABLED);

        UserTwoFactorMethod twoFactorMethod = methodOpt.get();
        twoFactorMethod.setEnabled(false);
        twoFactorMethod.setVerified(false);
        userRepository.save(user);
    }

    public List<TwoFactorMethodResponse> listMethods(HttpServletRequest httpRequest) {
        String username = jwtService.extractUsernameFromRequest(httpRequest);
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));

        Map<TwoFactorMethod, UserTwoFactorMethod> methodMap = user.getTwoFactorMethods().stream()
            .collect(Collectors.toMap(UserTwoFactorMethod::getMethod, Function.identity()));

        return Arrays.stream(TwoFactorMethod.values())
            .map(method -> {
                UserTwoFactorMethod userMethod = methodMap.get(method);
                return TwoFactorMethodResponse.builder()
                    .method(method)
                    .enabled(userMethod != null && userMethod.isEnabled())
                    .verified(userMethod != null && userMethod.isVerified())
                    .build();
            })
            .toList();
    }

}
