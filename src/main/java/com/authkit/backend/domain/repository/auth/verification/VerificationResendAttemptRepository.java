package com.authkit.backend.domain.repository.auth.verification;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.VerificationResendAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationResendAttemptRepository extends JpaRepository<VerificationResendAttempt, UUID> {
    Optional<VerificationResendAttempt> findByUser(User user);
} 