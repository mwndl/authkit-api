package com.authkit.backend.infrastructure.notification.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UnreadCountDTO {
    private long count;
} 