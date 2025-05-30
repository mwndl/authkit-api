package com.authkit.backend.domain.service;

import com.authkit.backend.domain.model.Notification;
import com.authkit.backend.domain.repository.notification.NotificationRepository;
import com.authkit.backend.domain.enums.NotificationCode;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationDomainService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(UUID userId, NotificationCode code, Map<String, Object> params, String type) {
        Notification notification = Notification.builder()
                .userId(userId)
                .code(code)
                .params(params)
                .type(type)
                .build();
        return notificationRepository.save(notification);
    }

    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void markNotificationAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findByUserId(userId, null)
                .stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new ApiException(ApiErrorCode.FORBIDDEN_ACTION);
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadStatus(userId, false);
        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
    }

    public long getUnreadNotificationCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadStatus(userId, false);
    }

    @Transactional
    public void deleteNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findByUserId(userId, null)
                .stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new ApiException(ApiErrorCode.FORBIDDEN_ACTION);
        }

        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAllNotifications(UUID userId) {
        notificationRepository.deleteAllByUserId(userId);
    }
} 