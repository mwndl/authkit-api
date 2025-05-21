package com.authkit.backend.features.v1.auth.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Username cannot be blank")
    private String Username;

    @NotBlank(message = "Surname cannot be blank")
    private String surname;

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank
    private String password;
}
