package com.authkit.backend.infrastructure.websocket.service;

import com.authkit.backend.infrastructure.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(UUID userId, NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    public void sendUnreadCount(UUID userId, long count) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId + "/unread", count);
    }
} 