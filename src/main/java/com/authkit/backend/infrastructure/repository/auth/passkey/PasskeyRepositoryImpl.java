package com.authkit.backend.infrastructure.repository.auth.passkey;

import com.authkit.backend.domain.model.Passkey;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.auth.passkey.PasskeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PasskeyRepositoryImpl implements PasskeyRepository {

    private final JpaPasskeyRepository jpaPasskeyRepository;

    @Override
    public Passkey save(Passkey passkey) {
        return jpaPasskeyRepository.save(passkey);
    }

    @Override
    public Optional<Passkey> findByCredentialId(String credentialId) {
        return jpaPasskeyRepository.findByCredentialId(credentialId);
    }

    @Override
    public Page<Passkey> findByUserAndEnabledTrue(User user, Pageable pageable) {
        return jpaPasskeyRepository.findByUserAndEnabledTrue(user, pageable);
    }

    @Override
    public Optional<Passkey> findByIdAndUserAndEnabledTrue(UUID id, User user) {
        return jpaPasskeyRepository.findByIdAndUserAndEnabledTrue(id, user);
    }

    @Override
    public void delete(Passkey passkey) {
        jpaPasskeyRepository.delete(passkey);
    }

    @Override
    public void deleteAllByUser(User user) {
        jpaPasskeyRepository.deleteAllByUser(user);
    }
} 