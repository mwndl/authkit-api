package com.authkit.backend.domain.event;

import com.authkit.backend.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordChangedEvent {

    private final User user;
}
