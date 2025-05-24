package com.authkit.backend.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        ApiErrorCode code = ex.getErrorCode();

        ApiErrorResponse response = new ApiErrorResponse(
                code.getCode(),
                code.getTitle(),
                code.getDescription()
        );

        HttpHeaders headers = new HttpHeaders();
        if (ex.getHeaders() != null && !ex.getHeaders().isEmpty()) {
            ex.getHeaders().forEach(headers::add);
        }

        return new ResponseEntity<>(response, headers, code.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            ApiErrorCode errorCode = switch (fieldError.getField()) {
                case "password" -> ApiErrorCode.PASSWORD_TOO_WEAK;
                case "name" -> ApiErrorCode.NAME_INVALID;
                default -> ApiErrorCode.VALIDATION_ERROR;
            };

            String customMessage = fieldError.getDefaultMessage();

            ApiErrorResponse response = new ApiErrorResponse(
                    errorCode.getCode(),
                    errorCode.getTitle(),
                    customMessage != null ? customMessage : errorCode.getDescription()
            );

            return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
        }

        ApiErrorCode fallback = ApiErrorCode.VALIDATION_ERROR;
        ApiErrorResponse response = new ApiErrorResponse(
                fallback.getCode(),
                fallback.getTitle(),
                fallback.getDescription()
        );
        return ResponseEntity.status(fallback.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ApiErrorCode code = ApiErrorCode.VALIDATION_ERROR;
        ApiErrorResponse response = new ApiErrorResponse(
                code.getCode(),
                "Constraint violation",
                ex.getMessage()
        );
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ApiErrorCode code = ApiErrorCode.INTERNAL_ERROR;
        ApiErrorResponse response = new ApiErrorResponse(
                code.getCode(),
                code.getTitle(),
                code.getDescription()
        );
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }
}
