package com.authkit.backend.domain.repository.notification;

import com.authkit.backend.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> saveAll(List<Notification> notifications);
    Page<Notification> findByUserId(UUID userId, Pageable pageable);
    List<Notification> findByUserIdAndReadStatus(UUID userId, boolean readStatus);
    long countByUserIdAndReadStatus(UUID userId, boolean readStatus);
    void delete(Notification notification);
    void deleteAllByUserId(UUID userId);
} 