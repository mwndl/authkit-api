package com.authkit.backend.shared.task;

import com.authkit.backend.features.v1.auth.common.repository.UserTokenRepository;
import com.authkit.backend.domain.model.UserToken;
import com.authkit.backend.shared.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupTask {

    private final UserTokenRepository userTokenRepository;
    private final JwtUtil jwtUtil;

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteExpiredOrNonRevokedTokens() {
        List<UserToken> userTokens = userTokenRepository.findAll();

        int deleted = 0;

        for (UserToken userToken : userTokens) {
            try {
                boolean expired = jwtUtil.isTokenExpired(userToken.getRefreshToken());
                if (expired || userToken.isRevoked()) {
                    userTokenRepository.delete(userToken);
                    deleted++;
                    log.info("Deleted {} token for user ID: {}", expired ? "expired" : "revoked", userToken.getUser().getId());
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                userTokenRepository.delete(userToken);
                deleted++;
                log.info("Deleted (caught ExpiredJwtException) token for user ID: {}", userToken.getUser().getId());
            } catch (Exception e) {
                log.warn("Failed to process token for user ID: {} - {}", userToken.getUser().getId(), e.getMessage());
            }
        }

        if (deleted > 0)
            log.info("Deleted {} expired or revoked tokens", deleted);
        else
            log.info("No tokens to delete.");
    }
}
