package com.authkit.backend.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {
    // 400 - Bad Request
    PASSWORD_TOO_WEAK(400, "PASSWORD_TOO_WEAK", "Password does not meet complexity requirements", "Password must contain at least 8 characters, including uppercase, lowercase, number and special character"),
    NAME_INVALID(400, "NAME_INVALID", "Name format is invalid", "Name must contain at least two words with only letters"),
    SAME_PASSWORD(400, "SAME_PASSWORD", "Password is the same", "The new password must be different from the current one"),
    EMAIL_INVALID(400, "EMAIL_INVALID", "Email format is invalid", "The email address provided is not valid"),
    USERNAME_INVALID(400, "INVALID_USERNAME", "Invalid username", "Username must be 4-15 characters, all lowercase, using only letters, numbers, '.', '_' or '-', and must not start/end with special characters or contain double special characters"),
    USERNAME_RESERVED(400, "USERNAME_RESERVED", "Username is reserved", "This username is not allowed and cannot be used"),
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "Validation failed", "One or more fields did not pass validation"),
    INVALID_USER_IDENTIFIER(400, "INVALID_USER_IDENTIFIER", "Invalid user identifier", "The specified user identifier is not valid"),
    TWO_FACTOR_NOT_ENABLED(400, "TWO_FACTOR_NOT_ENABLED", "Two-factor authentication not enabled", "Two-factor authentication is not enabled for this account. Please enable it to use this feature."),
    TWO_FACTOR_ALREADY_ENABLED(400, "TWO_FACTOR_ALREADY_ENABLED", "Two-factor authentication is already enabled", "Two-factor authentication is already enabled for this method"),
    INVALID_2FA_METHOD(400, "INVALID_2FA_METHOD", "Invalid 2FA method", "Invalid two-factor authentication method"),
    INVALID_REQUEST(400, "INVALID_REQUEST", "Invalid request", "The request is invalid."),
    INVALID_EMAIL(400, "INVALID_EMAIL", "Invalid email", "The provided email is invalid."),
    INVALID_PASSWORD(400, "INVALID_PASSWORD", "Invalid password", "The provided password is invalid."),
    INVALID_USERNAME(400, "INVALID_USERNAME", "Invalid username", "The provided username is invalid."),
    INVALID_CODE(400, "INVALID_CODE", "Invalid code", "The provided code is invalid."),
    INVALID_DEVICE_NAME(400, "INVALID_DEVICE_NAME", "Invalid device name", "The provided device name is invalid."),
    INVALID_DEVICE_TYPE(400, "INVALID_DEVICE_TYPE", "Invalid device type", "The provided device type is invalid."),

    // 401 - Unauthorized
    UNAUTHORIZED(401, "UNAUTHORIZED", "Unauthorized", "You need to be authenticated to access this resource."),
    UNAUTHENTICATED(401, "UNAUTHENTICATED", "User not authenticated", "Authentication is required to access this resource"),
    AUTH_EMAIL_NOT_FOUND(401, "AUTH_EMAIL_NOT_FOUND", "Email not found", "The email provided was not found in the database."),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", "Invalid credentials", "Email or password is incorrect."),
    ACCOUNT_DEACTIVATED(401, "ACCOUNT_DEACTIVATED", "Account is deactivated", "Your account is in the process of being deleted. Please log in again to reactivate your account."),
    INVALID_ACCESS_TOKEN(401, "INVALID_ACCESS_TOKEN", "Invalid Access token" , "The token provided is invalid" ),
    INVALID_REFRESH_TOKEN(401, "INVALID_REFRESH_TOKEN", "Invalid refresh token" , "The refresh token provided is invalid" ),
    EXPIRED_SESSION(401, "EXPIRED_SESSION", "Session expired", "Your access token has expired. Please refresh it or log in again to continue."),
    EXPIRED_REFRESH_TOKEN(401, "EXPIRED_REFRESH_TOKEN", "Expired refresh token", "The refresh token provided is expired. Please login again to continue." ),
    REVOKED_REFRESH_TOKEN(401, "REVOKED_REFRESH_TOKEN", "Revoked Refresh token", "The refresh token provided has been revoked and is no longer valid. Please log in again to continue." ),
    INVALID_TOKEN(401, "INVALID_TOKEN" , "Invalid token" , "The token provided is invalid" ),
    EXPIRED_TOKEN(401,"EXPIRED_TOKEN", "Expired Token", "The token provided is expired and is no longer valid." ),
    INVALID_PENDING_TOKEN(401, "INVALID_PENDING_TOKEN", "Invalid pending token", "The pending token provided is invalid or has expired. Please request a new one."),
    INVALID_2FA_CODE(401, "INVALID_2FA_CODE", "Invalid 2FA code", "The two-factor authentication code provided is invalid or has expired. Please try again."),
    TWO_FACTOR_REQUIRED(401, "TWO_FACTOR_REQUIRED", "Two-factor authentication required", "Two-factor authentication is required to complete the login process."),

    // 403 - Forbidden
    ACCOUNT_LOCKED(403, "ACCOUNT_LOCKED", "Account is locked", "Your account is locked due to too many failed login attempts"),
    ACCOUNT_NOT_VERIFIED(403,"ACCOUNT_NOT_VERIFIED", "Account is not verified", "Please verify your account and try again."),
    ACCOUNT_ALREADY_VERIFIED(403, "ACCOUNT_ALREADY_VERIFIED", "Account is already verified", "This account has already been verified."),
    FORBIDDEN_ACTION(403, "FORBIDDEN_ACTION", "Forbidden Action", "You do not have permission to perform this action."),
    CANNOT_REVOKE_OWN_SESSION(403, "CANNOT_REVOKE_OWN_SESSION", "Cannot Revoke Own Session", "You cannot revoke your own active session through this method. Use the /logout endpoint to log out of your current session. This method is intended for revoking other active sessions."),

    // 404 - Not Found
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "User not found", "The user was not found in the database."),
    SESSION_NOT_FOUND(404, "SESSION_NOT_FOUND" , "Session not found" , "The session was not found in the database." ),
    NOTIFICATION_NOT_FOUND(404, "NOTIFICATION_NOT_FOUND", "Notification not found", "The notification was not found in the database."),
    PASSKEY_NOT_FOUND(404, "PASSKEY_NOT_FOUND", "Passkey not found", "The requested passkey could not be found"),

    // 409 - Conflict
    EMAIL_ALREADY_REGISTERED(409, "EMAIL_ALREADY_REGISTERED", "Email already registered", "An account with this email already exists."),
    ALREADY_USED_TOKEN(409, "ALREADY_USED_TOKEN", "Already Used Token", "The token provided has already been used and is no longer valid."),
    USERNAME_ALREADY_TAKEN(409, "USERNAME_ALREADY_TAKEN", "Username Already Taken", "The username you have chosen is already in use. Please select a different username."),

    // 429 - Too Many Requests
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", "Too many requests", "You have exceeded the number of allowed requests. Please try again later."),
    TOO_MANY_LOGIN_ATTEMPTS(429, "TOO_MANY_LOGIN_ATTEMPTS", "Too Many Login Attempts", "You have made too many failed login attempts. Please try again after the time indicated in the 'Retry-After' header."),
    TOO_MANY_2FA_ATTEMPTS(429, "TOO_MANY_2FA_ATTEMPTS", "Too many 2FA attempts", "Too many failed attempts. Please try again later"),

    // 500 - Internal Server Error
    INTERNAL_ERROR(500, "INTERNAL_ERROR", "Unexpected error", "An unexpected error occurred"),
    EMAIL_SEND_FAILED(500, "EMAIL_SEND_FAILED", "Failed to send email", "There was an error while trying to send the email. Please try again later.");

    private final int httpStatus;
    private final String code;
    private final String title;
    private final String description;

    ApiErrorCode(int httpStatus, String code, String title, String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(this.httpStatus);
    }
}
