package com.authkit.backend.infrastructure.utils;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.domain.enums.UserStatus;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceHelper {

    private final UserRepository userRepository;

    public User getActiveUserByEmail(String email) {
        User user = getUserByEmail(email);
        checkUserStatus(user);
        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
    }

    public void checkUserStatus(User user) {
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION)
            throw new ApiException(ApiErrorCode.ACCOUNT_NOT_VERIFIED);
        if (user.getStatus() == UserStatus.DEACTIVATION_REQUESTED)
            throw new ApiException(ApiErrorCode.ACCOUNT_DEACTIVATED);
        if (user.getStatus() == UserStatus.LOCKED)
            throw new ApiException(ApiErrorCode.ACCOUNT_LOCKED);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
    }

    public User getUserByIdentifier(String identifier) {
        if (isUUID(identifier)) {
            return getUserById(UUID.fromString(identifier));
        } else if (isUsername(identifier)) {
            return getUserByUsername(identifier);
        } else if (isEmail(identifier)) {
            return getUserByEmail(identifier);
        } else {
            throw new ApiException(ApiErrorCode.INVALID_USER_IDENTIFIER);
        }
    }

    public boolean hasAnyTwoFactorEnabled(User user) {
        if (user.getTwoFactorMethods() == null) return false;
        return user.getTwoFactorMethods().stream()
                .anyMatch(method -> method.isEnabled() && method.isVerified());
    }

    private boolean isUUID(String str) {
        return str.matches("^[0-9a-fA-F-]{36}$");
    }

    private boolean isUsername(String str) {
        return str.matches("^(?!.*[._-]{2})(?![._-])[a-z0-9._-]{4,15}(?<![._-])$");
    }

    private boolean isEmail(String str) {
        return str.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }


}
