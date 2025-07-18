package com.authkit.backend.domain.repository.auth.common;

import com.authkit.backend.domain.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {
    void deleteAllByIpAndEmail(String ip, String email);

    Optional<LoginAttempt> findByIpAndUserAgentAndEmail(String ip, String userAgent, String email);
}