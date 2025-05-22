package com.authkit.backend.features.v1.utils.audit;

import com.authkit.backend.features.v1.auth.common.dto.request.LoginRequest;
import com.authkit.backend.features.v1.auth.common.dto.request.RegisterRequest;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final UserRepository userRepository;

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        Object result = joinPoint.proceed();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        
        String entityId = extractEntityId(joinPoint.getArgs()[0]);
        User user = userRepository.findByEmail(entityId).orElse(null);
        
        auditService.logAction(
            audited.action(),
            audited.entityType(),
            entityId,
            String.format("Method %s executed", methodName),
            user != null ? user.getId().toString() : null,
            user != null ? user.getUsername() : null
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
} 