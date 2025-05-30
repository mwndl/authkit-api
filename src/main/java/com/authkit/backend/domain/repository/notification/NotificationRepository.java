package com.authkit.backend.domain.repository.notification;

import com.authkit.backend.domain.model.Notification;
import com.authkit.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    Page<Notification> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.readStatus = :readStatus")
    List<Notification> findByUserIdAndReadStatus(@Param("userId") UUID userId, @Param("readStatus") boolean readStatus);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.readStatus = :readStatus")
    long countByUserIdAndReadStatus(@Param("userId") UUID userId, @Param("readStatus") boolean readStatus);

    void deleteAllByUserId(UUID userId);
} 