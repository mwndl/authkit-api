package com.authkit.backend.features.v1.utils.audit;

import com.authkit.backend.features.v1.auth.common.dto.request.LoginRequest;
import com.authkit.backend.features.v1.auth.common.dto.request.RegisterRequest;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.domain.repository.auth.common.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        String entityId = extractEntityId(joinPoint.getArgs()[0]);
        
        // For session-related actions, get user before executing the method
        User user = null;
        if (audited.action().startsWith("REVOKE") || audited.action().equals("LOGOUT")) {
            user = userTokenRepository.findByAccessTokenAndRevokedFalse(entityId)
                .map(token -> token.getUser())
                .orElseGet(() -> {
                    log.warn("No active token found for: {}", entityId);
                    return null;
                });
            
            if (user == null) {
                log.warn("Could not find user for entityId: {} and action: {}", entityId, audited.action());
                return joinPoint.proceed();
            }
        }
        
        // Execute the method
        Object result = joinPoint.proceed();
        
        // If we already have the user (session actions), use it
        // Otherwise, try to find the user after the method execution
        if (user == null) {
            user = extractUser(entityId, audited.action());
            if (user == null) {
                log.warn("Could not find user for entityId: {} and action: {}", entityId, audited.action());
                return result;
            }
        }
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        
        auditService.logAction(
            audited.action(),
            audited.entityType(),
            entityId,
            String.format("Method %s executed", methodName),
            user.getId().toString(),
            user.getUsername()
        );
        
        return result;
    }

    private String extractEntityId(Object firstParam) {
        if (firstParam instanceof LoginRequest loginRequest) {
            return loginRequest.getEmail();
        } else if (firstParam instanceof RegisterRequest registerRequest) {
            return registerRequest.getEmail();
        } else {
            return String.valueOf(firstParam);
        }
    }

    private User extractUser(String entityId, String action) {
        try {
            // For session-related actions, extract user from token
            if (action.startsWith("REVOKE") || action.equals("LOGOUT")) {
                return userTokenRepository.findByAccessTokenAndRevokedFalse(entityId)
                    .map(token -> token.getUser())
                    .orElseGet(() -> {
                        log.warn("No active token found for: {}", entityId);
                        return null;
                    });
            }
            
            // For other actions, try to find user by email
            return userRepository.findByEmail(entityId)
                .orElseGet(() -> {
                    log.warn("No user found for email: {}", entityId);
                    return null;
                });
        } catch (Exception e) {
            log.error("Error extracting user for entityId: {} and action: {}", entityId, action, e);
            return null;
        }
    }
} 