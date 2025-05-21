package com.authkit.backend.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private final String errorCode;
    private final String title;
    private final String description;
}
