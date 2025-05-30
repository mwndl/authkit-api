package com.authkit.backend.infrastructure.utils.twofactor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

import java.nio.ByteBuffer;
import java.time.Instant;

public class TOTPVerifier {
    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    
    public static boolean verifyCode(String base32Secret, String code) {
        try {
            byte[] key = new Base32().decode(base32Secret);
            long currentInterval = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;
            
            return checkCode(key, code, currentInterval) ||
                   checkCode(key, code, currentInterval - 1) ||
                   checkCode(key, code, currentInterval + 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    private static boolean checkCode(byte[] key, String code, long interval) {
        try {
            byte[] data = ByteBuffer.allocate(8).putLong(interval).array();
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(data);
            
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24) |
                        ((hash[offset + 1] & 0xFF) << 16) |
                        ((hash[offset + 2] & 0xFF) << 8) |
                        (hash[offset + 3] & 0xFF);
            
            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format("%0" + CODE_DIGITS + "d", otp).equals(code);
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateSecretKey() {
        byte[] randomBytes = new byte[20];
        new java.security.SecureRandom().nextBytes(randomBytes);
        return new Base32().encodeToString(randomBytes);
    }
}

