package com.authkit.backend.infrastructure.repository.notification;

import com.authkit.backend.domain.model.Notification;
import com.authkit.backend.domain.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;

    @Override
    public Notification save(Notification notification) {
        return jpaNotificationRepository.save(notification);
    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        return jpaNotificationRepository.saveAll(notifications);
    }

    @Override
    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        return jpaNotificationRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Notification> findByUserIdAndReadStatus(UUID userId, boolean readStatus) {
        return jpaNotificationRepository.findByUserIdAndReadStatus(userId, readStatus);
    }

    @Override
    public long countByUserIdAndReadStatus(UUID userId, boolean readStatus) {
        return jpaNotificationRepository.countByUserIdAndReadStatus(userId, readStatus);
    }

    @Override
    public void delete(Notification notification) {
        jpaNotificationRepository.delete(notification);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        jpaNotificationRepository.deleteAllByUserId(userId);
    }
} 