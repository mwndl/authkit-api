package com.authkit.backend.features.v1.auth.common.dto.response;

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
