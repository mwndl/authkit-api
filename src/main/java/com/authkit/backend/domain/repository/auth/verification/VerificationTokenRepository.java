package com.authkit.backend.domain.repository.auth.verification;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByTokenAndUsedFalse(String token);
    Optional<VerificationToken> findByUserAndUsedFalse(User user);
} 