package com.authkit.backend.infrastructure.auth.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionInfoResponse {
    private String sessionId;
    private String createdAt;
    private String ipAddress;
    private String deviceInfo;
    private boolean isCurrentSession;
}
