package com.authkit.backend.features.v1.notification.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UnreadCountDTO {
    private long count;
} 