package com.authkit.backend.shared.task;

import com.authkit.backend.domain.enums.UserStatus;
import com.authkit.backend.domain.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCleanupTask {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *") // runs every day at 3am
    public void deleteInactiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        int deleted = userRepository.deleteByStatusAndDeletionRequestedAtBefore(UserStatus.DEACTIVATION_REQUESTED, threshold);

        if (deleted > 0) {
            log.info("Deleted {} users pending deletion for over 30 days", deleted);
        }
    }
}
