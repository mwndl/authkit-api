package com.authkit.backend.features.v1.utils.audit;

import com.authkit.backend.domain.model.AuditLog;
import com.authkit.backend.domain.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;

    public void logAction(String action, String entityType, String entityId, String details, String userId, String username) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .username(username)
                .details(details)
                .ipAddress(getClientIp())
                .build();

        auditLogRepository.save(auditLog);
    }

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
} 