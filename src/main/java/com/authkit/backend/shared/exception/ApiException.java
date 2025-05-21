package com.authkit.backend.shared.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;
    private final Map<String, String> headers;

    public ApiException(ApiErrorCode errorCode) {
        this(errorCode, null);
    }

    public ApiException(ApiErrorCode errorCode, Map<String, String> headers) {
        super(errorCode.getTitle());
        this.errorCode = errorCode;
        this.headers = headers;
    }

    public boolean hasHeaders() {
        return headers != null && !headers.isEmpty();
    }
}
