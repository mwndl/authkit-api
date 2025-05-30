package com.authkit.backend.infrastructure.user.dto;

import lombok.Data;

@Data
public class UpdateUserDataRequest {

    private String newName;
    private String newSurname;
    private String newUsername;

}
