package com.authkit.backend.features.v1.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSearchResult {
    UUID id;
    String username;
    String name;
    String surname;
    String pictureHash;
    double confidence;
}
