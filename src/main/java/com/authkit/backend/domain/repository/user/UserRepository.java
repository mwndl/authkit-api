package com.authkit.backend.domain.repository.user;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE %:query%")
    List<User> searchByUsername(@Param("query") String query, Pageable pageable);

    int deleteByStatusAndDeletionRequestedAtBefore(UserStatus status, LocalDateTime dateTime);
    boolean existsByUsername(@NotBlank(message = "Username cannot be blank") String attr0);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.twoFactorMethods WHERE u.email = :email")
    Optional<User> findByEmailWithTwoFactorMethods(@Param("email") String email);
}
