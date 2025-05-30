package com.authkit.backend.domain.listener;

import com.authkit.backend.domain.event.PasswordChangedEvent;
import com.authkit.backend.infrastructure.auth.common.service.SessionService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordChangedListener {

    private final SessionService sessionService;

    @EventListener
    public void onPasswordChanged(PasswordChangedEvent event) {
        sessionService.revokeAllUserSessions(event.getUser());
    }
}
