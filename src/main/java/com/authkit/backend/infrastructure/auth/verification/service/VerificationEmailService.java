package com.authkit.backend.infrastructure.auth.verification.service;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.VerificationToken;
import com.authkit.backend.domain.repository.auth.verification.VerificationTokenRepository;
import com.authkit.backend.infrastructure.utils.EmailServiceHelper;
import com.authkit.backend.infrastructure.utils.VerificationLinkBuilderHelper;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationEmailService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailServiceHelper emailService;
    private final JwtService jwtService;
    private final VerificationLinkBuilderHelper verificationLinkBuilderHelper;
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    public void sendVerificationEmail(User user) throws MailException, MessagingException {
        String verificationToken = jwtService.generateVerificationToken(user);
        String verificationCode = generateVerificationCode();

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(verificationToken);
        token.setCode(verificationCode);
        token.setExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));
        token.setUsed(false);
        verificationTokenRepository.save(token);

        try {
            sendVerificationEmail(user, verificationToken, verificationCode);
        } catch (MailException | MessagingException e) {
            throw new ApiException(ApiErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String generateVerificationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        
        return code.toString();
    }

    private void sendVerificationEmail(User user, String verificationToken, String verificationCode) throws MailException, MessagingException {
        String verificationUrl = verificationLinkBuilderHelper.buildVerificationLink(verificationToken);
        
        emailService.sendVerificationEmail(
            user.getEmail(),
            user.getName(),
            verificationUrl,
            verificationCode
        );
    }
} 