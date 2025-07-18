package com.authkit.backend.domain.repository.auth.common;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    List<UserToken> findAllByUserAndRevokedFalse(User user);

    Optional<UserToken> findByRefreshTokenAndRevokedTrue(String refreshToken);

    List<UserToken> findByRevokedFalse();

    Optional<UserToken> findByRefreshTokenAndRevokedFalse(String refreshToken);

    Optional<UserToken> findByAccessTokenAndRevokedFalse(String accessToken);

    Optional<UserToken> findByIdAndRevokedFalse(UUID sessionId);

}