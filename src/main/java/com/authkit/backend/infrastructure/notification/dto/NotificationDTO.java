package com.authkit.backend.infrastructure.notification.dto;

import com.authkit.backend.domain.model.Notification;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationDTO {
    private UUID id;
    private String code;
    private Map<String, Object> params;
    private String type;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public static NotificationDTO fromEntity(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .code(notification.getCode().getCode())
                .params(notification.getParams())
                .type(notification.getType())
                .readStatus(notification.isReadStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
} 