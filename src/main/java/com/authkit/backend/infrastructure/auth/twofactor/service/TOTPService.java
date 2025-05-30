package com.authkit.backend.infrastructure.auth.twofactor.service;

import com.authkit.backend.domain.model.UserTwoFactorMethod;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TOTPService {
    private final GoogleAuthenticator gAuth;
    private final Map<String, Integer> verificationAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    public String generateSecret() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String generateQRCodeUrl(String email, String secret) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("AuthKit", email,
            new GoogleAuthenticatorKey.Builder(secret).build());
    }

    public boolean verifyCode(UserTwoFactorMethod method, String code) {
        String key = method.getUser().getEmail() + "_" + method.getId();
        
        // Check if user is locked out
        if (isLockedOut(key)) {
            throw new ApiException(ApiErrorCode.TOO_MANY_2FA_ATTEMPTS);
        }

        boolean isValid = gAuth.authorize(method.getSecret(), Integer.parseInt(code));
        
        if (!isValid) {
            incrementAttempts(key);
            return false;
        }

        // Reset attempts on successful verification
        verificationAttempts.remove(key);
        return true;
    }

    private boolean isLockedOut(String key) {
        Integer attempts = verificationAttempts.get(key);
        return attempts != null && attempts >= MAX_ATTEMPTS;
    }

    private void incrementAttempts(String key) {
        verificationAttempts.compute(key, (k, v) -> v == null ? 1 : v + 1);
    }

    public boolean validateSecret(String secret) {
        try {
            Base32 base32 = new Base32();
            byte[] decoded = base32.decode(secret);
            return decoded.length >= 16; // Minimum 128 bits
        } catch (Exception e) {
            return false;
        }
    }
}