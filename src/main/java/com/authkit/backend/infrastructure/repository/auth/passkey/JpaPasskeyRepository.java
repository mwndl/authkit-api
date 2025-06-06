package com.authkit.backend.infrastructure.repository.auth.passkey;

import com.authkit.backend.domain.model.Passkey;
import com.authkit.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPasskeyRepository extends JpaRepository<Passkey, UUID> {
    Optional<Passkey> findByCredentialId(String credentialId);
    
    @Query("SELECT p FROM Passkey p WHERE p.user = :user AND p.enabled = true")
    Page<Passkey> findByUserAndEnabledTrue(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT p FROM Passkey p WHERE p.id = :id AND p.user = :user AND p.enabled = true")
    Optional<Passkey> findByIdAndUserAndEnabledTrue(@Param("id") UUID id, @Param("user") User user);
    
    @Query("DELETE FROM Passkey p WHERE p.user = :user")
    void deleteAllByUser(@Param("user") User user);
} 