package com.authkit.backend.features.v1.notification.service;

import com.authkit.backend.domain.model.Notification;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.service.NotificationDomainService;
import com.authkit.backend.features.v1.utils.UserServiceHelper;
import com.authkit.backend.features.v1.utils.audit.Audited;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDomainService notificationDomainService;
    private final UserServiceHelper userServiceHelper;

    @Audited(action = "CREATE_NOTIFICATION", entityType = "NOTIFICATION")
    public Notification createNotification(UUID userId, String title, String message, String type) {
        if (userId == null) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(message) || !StringUtils.hasText(type)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userServiceHelper.getUserById(userId);
        return notificationDomainService.createNotification(user.getId(), title, message, type);
    }

    @Audited(action = "GET_NOTIFICATIONS", entityType = "NOTIFICATION")
    public Page<Notification> getUserNotifications(String email, Pageable pageable) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userServiceHelper.getActiveUserByEmail(email);
        return notificationDomainService.getUserNotifications(user.getId(), pageable);
    }

    @Audited(action = "MARK_NOTIFICATION_AS_READ", entityType = "NOTIFICATION")
    public void markNotificationAsRead(String email, UUID notificationId) {
        if (!StringUtils.hasText(email) || notificationId == null) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userServiceHelper.getActiveUserByEmail(email);
        notificationDomainService.markNotificationAsRead(user.getId(), notificationId);
    }

    @Audited(action = "MARK_ALL_NOTIFICATIONS_AS_READ", entityType = "NOTIFICATION")
    public void markAllNotificationsAsRead(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userServiceHelper.getActiveUserByEmail(email);
        notificationDomainService.markAllNotificationsAsRead(user.getId());
    }

    public long getUnreadNotificationCount(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userServiceHelper.getActiveUserByEmail(email);
        return notificationDomainService.getUnreadNotificationCount(user.getId());
    }

    @Audited(action = "DELETE_NOTIFICATION", entityType = "NOTIFICATION")
    public void deleteNotification(String email, UUID notificationId) {
        if (!StringUtils.hasText(email) || notificationId == null) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userServiceHelper.getActiveUserByEmail(email);
        notificationDomainService.deleteNotification(user.getId(), notificationId);
    }

    @Audited(action = "DELETE_ALL_NOTIFICATIONS", entityType = "NOTIFICATION")
    public void deleteAllNotifications(String email) {
        User user = userServiceHelper.getActiveUserByEmail(email);
        notificationDomainService.deleteAllNotifications(user.getId());
    }
} 