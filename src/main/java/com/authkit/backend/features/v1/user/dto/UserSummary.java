package com.authkit.backend.features.v1.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSummary {
    private UUID id;
    private String username;
    private String name;
    private String surname;
}
