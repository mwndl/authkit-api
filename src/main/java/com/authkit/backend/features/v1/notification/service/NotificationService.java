package com.authkit.backend.features.v1.notification.service;

import com.authkit.backend.domain.model.Notification;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.notification.NotificationRepository;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.features.v1.utils.UserServiceHelper;
import com.authkit.backend.features.v1.utils.audit.Audited;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;

    @Audited(action = "CREATE_NOTIFICATION", entityType = "NOTIFICATION")
    public Notification createNotification(UUID userId, String title, String message, String type) {
        if (userId == null) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(message) || !StringUtils.hasText(type)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReadStatus(false);
        return notificationRepository.save(notification);
    }

    @Audited(action = "GET_NOTIFICATIONS", entityType = "NOTIFICATION")
    public Page<Notification> getUserNotifications(String email, Pageable pageable) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        return notificationRepository.findByUserId(user.getId(), pageable);
    }

    @Audited(action = "MARK_NOTIFICATION_AS_READ", entityType = "NOTIFICATION")
    @Transactional
    public void markNotificationAsRead(String email, UUID notificationId) {
        if (!StringUtils.hasText(email) || notificationId == null) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(user.getId())) {
            throw new ApiException(ApiErrorCode.FORBIDDEN_ACTION);
        }

        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    @Audited(action = "MARK_ALL_NOTIFICATIONS_AS_READ", entityType = "NOTIFICATION")
    @Transactional
    public void markAllNotificationsAsRead(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        List<Notification> notifications = notificationRepository.findByUserIdAndReadStatus(user.getId(), false);
        notifications.forEach(notification -> notification.setReadStatus(true));
        notificationRepository.saveAll(notifications);
    }

    public long getUnreadNotificationCount(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        return notificationRepository.countByUserIdAndReadStatus(user.getId(), false);
    }

    @Audited(action = "DELETE_NOTIFICATION", entityType = "NOTIFICATION")
    @Transactional
    public void deleteNotification(String email, UUID notificationId) {
        if (!StringUtils.hasText(email) || notificationId == null) {
            throw new ApiException(ApiErrorCode.VALIDATION_ERROR);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(user.getId())) {
            throw new ApiException(ApiErrorCode.FORBIDDEN_ACTION);
        }

        notificationRepository.delete(notification);
    }

    @Audited(action = "DELETE_ALL_NOTIFICATIONS", entityType = "NOTIFICATION")
    @Transactional
    public void deleteAllNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        notificationRepository.deleteAllByUserId(user.getId());
    }
} 