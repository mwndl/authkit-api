package com.authkit.backend.features.v1.auth.common.service;

import com.authkit.backend.domain.enums.UserStatus;
import com.authkit.backend.features.v1.auth.common.dto.response.TokensResponse;
import com.authkit.backend.features.v1.auth.common.dto.request.LoginRequest;
import com.authkit.backend.features.v1.auth.common.dto.request.RegisterRequest;
import com.authkit.backend.features.v1.utils.ValidationServiceHelper;
import com.authkit.backend.features.v1.auth.common.dto.response.AuthResponse;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.UserToken;
import com.authkit.backend.domain.repository.auth.common.UserTokenRepository;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.shared.security.JwtService;
import com.authkit.backend.shared.security.UserDetailsImpl;
import com.authkit.backend.shared.utils.LoginUtil;
import com.authkit.backend.features.v1.utils.audit.Audited;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ValidationServiceHelper validationService;
    private final LoginUtil loginUtil;

    @Audited(action = "REGISTER", entityType = "USER")
    public void register(RegisterRequest request, HttpServletRequest httpRequest) {
        validationService.validateEmail(request.getEmail());
        validationService.validateUsername(request.getUsername());
        validationService.validateName(request.getName());
        validationService.validateName(request.getSurname());
        validationService.validatePassword(request.getPassword());

        User user = createUser(request);
        userRepository.save(user);
    }

    @Audited(action = "LOGIN", entityType = "USER")
    public void login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new ApiException(ApiErrorCode.INVALID_CREDENTIALS);

        if (user.getStatus() == UserStatus.PENDING_VERIFICATION)
            throw new ApiException(ApiErrorCode.ACCOUNT_NOT_VERIFIED);

        if (user.getStatus() == UserStatus.DEACTIVATION_REQUESTED) {
            user.setStatus(UserStatus.ACTIVE);
            user.setDeletionRequestedAt(null);
            userRepository.save(user);
        }

        // Check if user has 2FA enabled
        boolean has2FA = user.getTwoFactorMethods().stream()
                .anyMatch(method -> method.isEnabled() && method.isVerified());

        if (has2FA) {
            // Generate a short-lived pending token for 2FA verification
            String pendingToken = jwtService.generatePendingToken(user);
            throw new ApiException(ApiErrorCode.TWO_FACTOR_REQUIRED, Map.of("pendingToken", pendingToken));
        }

        throw new ApiException(ApiErrorCode.ACCOUNT_NOT_VERIFIED);
    }

    public void validateUsernameAvailability(String username) {
        String normalized = username.trim().toLowerCase();
        validationService.validateUsername(normalized);
    }

    public void validateEmailAvailability(String email) {
        String normalized = email.trim().toLowerCase();
        validationService.validateEmail(normalized);
    }

    public TokensResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        if (username == null)
            throw new ApiException(ApiErrorCode.INVALID_REFRESH_TOKEN);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        loginUtil.validateRefreshToken(refreshToken, userDetails);

        String newAccessToken = loginUtil.generateAccessTokenForUser(user);
        Date newAccessTokenExpirationDate = jwtService.extractExpiration(newAccessToken);

        updateAccessToken(refreshToken, newAccessToken, newAccessTokenExpirationDate);

        return TokensResponse.builder()
            .accessToken(newAccessToken)
            .accessTokenExpiresAt(newAccessTokenExpirationDate)
            .refreshToken(refreshToken)
            .refreshTokenExpiresAt(jwtService.extractExpiration(refreshToken))
            .build();
    }

    private void updateAccessToken(String refreshToken, String newAccessToken, Date newAccessTokenExpiration) {
        UserToken userToken = userTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_REFRESH_TOKEN));

        userToken.setAccessToken(newAccessToken);
        userToken.setAccessTokenExpiration(newAccessTokenExpiration);
        userToken.setUpdatedAt(new Date());
        userToken.setRevoked(false);

        userTokenRepository.save(userToken);
    }

    @Audited(action = "CREATE_SESSION", entityType = "USER")
    public TokensResponse generateAndPersistTokens(User user, HttpServletRequest httpRequest) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        Date accessTokenExpiration = jwtService.extractExpiration(accessToken);
        Date refreshTokenExpiration = jwtService.extractExpiration(refreshToken);

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setAccessToken(accessToken);
        userToken.setRefreshToken(refreshToken);
        userToken.setAccessTokenExpiration(accessTokenExpiration);
        userToken.setRefreshTokenExpiration(refreshTokenExpiration);
        userToken.setRevoked(false);
        userToken.setCreatedAt(new Date());
        userToken.setUpdatedAt(new Date());
        userToken.setDeviceInfo(httpRequest.getHeader("User-Agent"));
        userToken.setDeviceIp(httpRequest.getRemoteAddr());

        userTokenRepository.save(userToken);

        return TokensResponse.builder()
            .accessToken(accessToken)
            .accessTokenExpiresAt(accessTokenExpiration)
            .refreshToken(refreshToken)
            .refreshTokenExpiresAt(refreshTokenExpiration)
            .build();
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setTwoFactorMethods(new ArrayList<>());
        return user;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ApiErrorCode.AUTH_EMAIL_NOT_FOUND));
    }
}
