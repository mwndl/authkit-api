package com.authkit.backend.features.v1.auth.common.controller;

import com.authkit.backend.domain.model.PasswordResetToken;
import com.authkit.backend.features.v1.auth.common.dto.request.*;
import com.authkit.backend.features.v1.auth.common.dto.response.AuthResponse;
import com.authkit.backend.features.v1.auth.common.dto.response.TokensResponse;
import com.authkit.backend.features.v1.auth.common.dto.response.SessionInfoResponse;
import com.authkit.backend.features.v1.auth.common.service.AuthService;
import com.authkit.backend.features.v1.auth.common.service.SessionService;
import com.authkit.backend.features.v1.user.service.UserService;
import com.authkit.backend.features.v1.utils.EmailServiceHelper;
import com.authkit.backend.features.v1.auth.common.service.PasswordResetService;
import com.authkit.backend.features.v1.utils.ResetLinkBuilderHelper;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

        private final AuthService authService;
        private final SessionService sessionService;
        private final PasswordResetService passwordResetService;
        private final EmailServiceHelper emailService;
        private final UserService userService;
        private final ResetLinkBuilderHelper resetLinkBuilderHelper;

        @PostMapping("/register")
        @Operation(summary = "Register a new user", description = "Creates a new user account", responses = {
                        @ApiResponse(responseCode = "201", description = "Created - User successfully registered"),
                        @ApiResponse(responseCode = "400", description = "Bad Request - Validation errors"),
        })
        public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request, HttpServletRequest httpRequest) {
                return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, httpRequest));
        }

        @PostMapping("/login")
        @Operation(summary = "Authenticate user", description = "Authenticates the user", responses = {
                        @ApiResponse(responseCode = "200", description = "OK - Login successful"),
                        @ApiResponse(responseCode = "400", description = "Bad Request - Validation errors"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Account not verified or locked")
        })
        public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpRequest) {
                return ResponseEntity.ok(authService.login(request, httpRequest));
        }

        @PostMapping("/validate-username")
        @Operation(summary = "Validate Username Availability", description = "Checks if the given username is available for registration. If the username is already taken, it returns an error.", responses = {
                        @ApiResponse(responseCode = "200", description = "OK - Username is available for registration"),
                        @ApiResponse(responseCode = "400", description = "Bad Request - Username is already taken")
        })
        public ResponseEntity<Void> validateUsername(@RequestBody UsernameValidationRequest request) {
                authService.validateUsernameAvailability(request.getUsername());
                return ResponseEntity.ok().build();
        }

        @PostMapping("/validate-email")
        @Operation(summary = "Validate Email Availability", description = "Checks if the given email is available for registration. If the email is already registered, it returns an error.", responses = {
                        @ApiResponse(responseCode = "200", description = "OK - Email is available for registration"),
                        @ApiResponse(responseCode = "400", description = "Bad Request - Email is already registered")
        })
        public ResponseEntity<Void> validateEmail(@RequestBody EmailValidationRequest request) {
                authService.validateEmailAvailability(request.getEmail());
                return ResponseEntity.ok().build();
        }

        @PostMapping("/refresh")
        @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token", responses = {
                        @ApiResponse(responseCode = "200", description = "OK - Token successfully refreshed"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
        })
        public ResponseEntity<TokensResponse> refresh(
                        @RequestBody @Valid RefreshRequest request) {
                return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
        }

        @DeleteMapping("/logout")
        @Operation(summary = "Logout user", description = "Invalidates the current access token and refresh token", responses = {
                        @ApiResponse(responseCode = "204", description = "No Content - Logout successful"),
                        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid or missing Authorization header"),
        })
        public ResponseEntity<Void> logout(HttpServletRequest request) {
                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                        throw new ApiException(ApiErrorCode.INVALID_ACCESS_TOKEN);

                String accessToken = authorizationHeader.substring(7);
                sessionService.revokeCurrentSession(accessToken);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("logout/{sessionId}")
        @Operation(summary = "Logout a specific session", description = "Logout and revokes the token of another open session.", responses = {
                        @ApiResponse(responseCode = "204", description = "No Content - Session revoked successfully"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User not allowed to revoke this session"),
                        @ApiResponse(responseCode = "404", description = "Not Found - Session not found")
        })
        public ResponseEntity<Void> logoutById(
                        @PathVariable UUID sessionId,
                        @AuthenticationPrincipal UserDetails userDetails,
                        HttpServletRequest httpRequest) {
                String authorizationHeader = httpRequest.getHeader("Authorization");

                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                        throw new ApiException(ApiErrorCode.INVALID_ACCESS_TOKEN);
                String currentAccessToken = authorizationHeader.substring(7);

                sessionService.revokeSpecificSession(sessionId, userDetails.getUsername(), currentAccessToken);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/logout-all")
        @Operation(summary = "Logout all sessions", description = "Invalidates all the active sessions", responses = {
                        @ApiResponse(responseCode = "204", description = "No Content - Logout successful"),
                        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid or missing Authorization header"),
        })
        public ResponseEntity<Void> logoutAllSessions(@AuthenticationPrincipal UserDetails userDetails,
                        HttpServletRequest httpRequest) {
                String authorizationHeader = httpRequest.getHeader("Authorization");
                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                        throw new ApiException(ApiErrorCode.INVALID_ACCESS_TOKEN);
                String currentAccessToken = authorizationHeader.substring(7);

                sessionService.logoutAllSessions(userDetails.getUsername(), currentAccessToken);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/sessions")
        @Operation(summary = "Get active sessions", description = "Returns a list of active user sessions", responses = {
                        @ApiResponse(responseCode = "200", description = "OK - Active sessions returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
        })
        public ResponseEntity<List<SessionInfoResponse>> getActiveSessions(@AuthenticationPrincipal UserDetails userDetails,
                                                                           HttpServletRequest httpRequest) {
                String authorizationHeader = httpRequest.getHeader("Authorization");
                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                        throw new ApiException(ApiErrorCode.INVALID_ACCESS_TOKEN);
                String currentAccessToken = authorizationHeader.substring(7);

                List<SessionInfoResponse> activeSessions = sessionService.getActiveSessions(userDetails.getUsername(),
                                currentAccessToken);
                return ResponseEntity.ok(activeSessions);
        }

        @PostMapping("/forgot-password")
        public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
                passwordResetService.handleForgotPassword(request.getEmail());
        }

        @PostMapping("/reset-password")
        public void resetPassword(@RequestBody ResetPasswordRequest request) {
                PasswordResetToken token = passwordResetService.validateToken(request.getToken());
                userService.updatePasswordByEmail(token.getEmail(), request.getNewPassword());
                passwordResetService.markTokenAsUsed(token);
        }
}
