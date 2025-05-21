package com.authkit.backend.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {
    // 400 - Bad Request
    PASSWORD_TOO_WEAK(400, "PASSWORD_TOO_WEAK", "Password does not meet complexity requirements", "Password must contain at least 8 characters, including uppercase, lowercase, number and special character"),
    NAME_INVALID(400, "NAME_INVALID", "Name format is invalid", "Name must contain at least two words with only letters"),
    SAME_NAME(400, "SAME_NAME", "Name is the same", "The new name must be different from the current one"),
    SAME_PASSWORD(400, "SAME_PASSWORD", "Password is the same", "The new password must be different from the current one"),
    SAME_USERNAME(400, "SAME_USERNAME", "Username is the same", "The new username must be different from the current one"),
    EMAIL_INVALID(400, "EMAIL_INVALID", "Email format is invalid", "The email address provided is not valid"),
    USERNAME_INVALID(400, "INVALID_USERNAME", "Invalid username", "Username must be 4-15 characters, all lowercase, using only letters, numbers, '.', '_' or '-', and must not start/end with special characters or contain double special characters"),
    USERNAME_RESERVED(400, "USERNAME_RESERVED", "Username is reserved", "This username is not allowed and cannot be used"),

    INVALID_DESCRIPTION(400, "INVALID_DESCRIPTION", "Invalid description", "The description must be between 1 and 255 characters."),
    INVALID_TRANSACTION_TYPE(400, "INVALID_TRANSACTION_TYPE", "Invalid transaction type", "The specified transaction type is not recognized. Use ENTRY or EXIT." ),
    INVALID_DATE_FUTURE(400, "INVALID_DATE_FUTURE", "Date is invalid", "The date cannot be in the future"),
    INVALID_AMOUNT(400, "INVALID_AMOUNT", "Invalid amount", "The amount must be greater than 0"),
    MISSING_AMOUNT(400, "MISSING_AMOUNT", "Amount is required", "The amount must be provided and greater than 0"),
    MISSING_DATE(400, "MISSING_DATE", "Date is required", "The date must be provided"),
    MISSING_TRANSACTION_TYPE(400, "MISSING_TRANSACTION_TYPE", "Transaction type is required", "The transaction type must be provided"),
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "Validation failed", "One or more fields did not pass validation"),
    COLLABORATOR_ALREADY_EXISTS(400, "COLLABORATOR_ALREADY_EXISTS", "Collaborator already added", "The user is already a collaborator of this FundBox."),
    COLLABORATOR_CANNOT_BE_OWNER(400, "COLLABORATOR_CANNOT_BE_OWNER", "Invalid collaborator", "The owner of a FundBox cannot be added as a collaborator."),
    CANNOT_LEAVE_AS_OWNER(400, "CANNOT_LEAVE_AS_OWNER", "Cannot Leave As Owner", "You cannot leave a FundBox that you own. This method is intended for FundBox collaborators."),
    INVALID_USER_IDENTIFIER(400, "INVALID_USER_IDENTIFIER", "Invalid user identifier", "The specified user identifier is not valid"),
    TWO_FACTOR_NOT_ENABLED(400, "TWO_FACTOR_NOT_ENABLED", "Two-factor authentication not enabled", "Two-factor authentication is not enabled for this account. Please enable it to use this feature."),

    // 401 - Unauthorized
    UNAUTHENTICATED(401, "UNAUTHENTICATED", "User not authenticated", "Authentication is required to access this resource. Please provide a valid token."),
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
    TWO_FACTOR_REQUIRED(401, "TWO_FACTOR_REQUIRED", "Two-factor authentication required", "This account has two-factor authentication enabled. Please use the V2 API to login."),
    
    // 403 - Forbidden
    UNAUTHORIZED_ACCESS(403, "UNAUTHORIZED_ACCESS", "Unauthorized access", "You are not authorized to access this resource."),
    ACCOUNT_LOCKED(403, "ACCOUNT_LOCKED", "Account is locked", "Your account is locked due to too many failed login attempts"),
    ACCOUNT_NOT_VERIFIED(403,"ACCOUNT_NOT_VERIFIED", "Account is not verified", "Please verify your account and try again."),
    FORBIDDEN_COLLABORATOR_ADDITION(403, "FORBIDDEN_COLLABORATOR_ADDITION", "Forbidden Collaborator Addition", "Only the owner of the fund box can add collaborators."),
    FORBIDDEN_ACTION(403, "FORBIDDEN_ACTION", "Forbidden Action", "You do not have permission to perform this action."),
    CANNOT_REVOKE_OWN_SESSION(403, "CANNOT_REVOKE_OWN_SESSION", "Cannot Revoke Own Session", "You cannot revoke your own active session through this method. Use the /logout endpoint to log out of your current session. This method is intended for revoking other active sessions."),
    IP_BLACKLISTED(403, "IP_BLACKLISTED", "IP blacklisted", "Access from your IP address has been permanently blocked due to suspicious or malicious activity."),

    // 404 - Not Found
    DEPOSIT_NOT_FOUND(404, "DEPOSIT_NOT_FOUND", "Deposit not found", "The requested deposit was not found"),
    COLLABORATOR_NOT_FOUND(404, "COLLABORATOR_NOT_FOUND", "Collaborator not found", "The specified collaborator does not exist in this FundBox."),
    FUND_BOX_NOT_FOUND(404, "FUND_BOX_NOT_FOUND", "FundBox not found", "Unable to find requested FundBox"),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "User not found", "The user was not found in the database."),
    INVITATION_NOT_FOUND(404, "INVITATION_NOT_FOUND", "Invitation Not Found", "The invitation with the provided ID could not be found. Please check the invitation ID and try again."),
    SESSION_NOT_FOUND(404, "SESSION_NOT_FOUND" , "Session not found" , "The session was not found in the database." ),

    // 409 - Conflict
    FUND_BOX_NAME_ALREADY_EXISTS(409, "FUND_BOX_NAME_ALREADY_EXISTS", "FundBox name must be unique", "The name provided is already in use, each FundBox must have a unique name per user"),
    EMAIL_ALREADY_REGISTERED(409, "EMAIL_ALREADY_REGISTERED", "Email already registered", "An account with this email already exists."),
    COLLABORATOR_ALREADY_INVITED(409, "COLLABORATOR_ALREADY_INVITED", "Collaborator Already Invited", "The user has already been invited to collaborate on this fund box. Please check the invitation status or send a new invitation."),
    INVITATION_ALREADY_ACCEPTED(409, "INVITATION_ALREADY_ACCEPTED", "Invitation Already Accepted", "The invitation has already been accepted. You cannot accept it again."),
    INVITATION_CANNOT_BE_CANCELED(409, "INVITATION_CANNOT_BE_CANCELED", "Invitation Cannot Be Canceled", "Only pending invitations can be canceled."),
    ALREADY_USED_TOKEN(409, "ALREADY_USED_TOKEN", "Already Used Token", "The token provided has already been used and is no longer valid."),
    USERNAME_ALREADY_TAKEN(409, "USERNAME_ALREADY_TAKEN", "Username Already Taken", "The username you have chosen is already in use. Please select a different username."),

    // 429 - Too Many Requests
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", "Too many requests", "You have exceeded the number of allowed requests. Please try again later."),
    TOO_MANY_LOGIN_ATTEMPTS(429, "TOO_MANY_LOGIN_ATTEMPTS", "Too Many Login Attempts", "You have made too many failed login attempts. Please try again after the time indicated in the 'Retry-After' header."),
    TOO_MANY_ATTEMPTS(429, "TOO_MANY_ATTEMPTS", "Too many failed attempts", "Too many failed attempts. Please try again later"),

    // Server Error
    INTERNAL_ERROR(500, "INTERNAL_ERROR", "Unexpected error", "An unexpected error occurred"),
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", "Service unavailable", "The service is temporarily unavailable. Please try again later"),

    // 2FA errors
    TWO_FACTOR_ALREADY_ENABLED(400, "TWO_FACTOR_ALREADY_ENABLED", "Two-factor authentication is already enabled", "Two-factor authentication is already enabled for this method"),
    INVALID_2FA_METHOD(400, "INVALID_2FA_METHOD", "Invalid 2FA method", "Invalid two-factor authentication method"),
    INVALID_2FA_CODE(401, "INVALID_2FA_CODE", "Invalid 2FA code", "The two-factor authentication code provided is invalid or has expired. Please try again."),
    TOO_MANY_2FA_ATTEMPTS(429, "TOO_MANY_2FA_ATTEMPTS", "Too many 2FA attempts", "Too many failed attempts. Please try again later");

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
