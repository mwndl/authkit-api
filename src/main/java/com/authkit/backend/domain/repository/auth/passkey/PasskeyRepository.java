package com.authkit.backend.domain.repository.auth.passkey;

import com.authkit.backend.domain.model.Passkey;
import com.authkit.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PasskeyRepository {
    Passkey save(Passkey passkey);
    Optional<Passkey> findByCredentialId(String credentialId);
    Page<Passkey> findByUserAndEnabledTrue(User user, Pageable pageable);
    Optional<Passkey> findByIdAndUserAndEnabledTrue(UUID id, User user);
    void delete(Passkey passkey);
    void deleteAllByUser(User user);
} 