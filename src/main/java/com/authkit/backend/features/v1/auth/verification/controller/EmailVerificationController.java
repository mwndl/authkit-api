package com.authkit.backend.features.v1.auth.verification.controller;

import com.authkit.backend.features.v1.auth.verification.service.EmailVerificationService;
import com.authkit.backend.features.v1.auth.common.dto.response.AuthResponse;
import com.authkit.backend.features.v1.auth.verification.dto.request.VerifyEmailCodeRequest;
import com.authkit.backend.features.v1.auth.verification.dto.response.ResendVerificationResponse;
import com.authkit.backend.shared.dto.response.ApiResponse;
import com.authkit.backend.shared.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/verify")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(@RequestParam String token, HttpServletRequest request) {
        AuthResponse response = emailVerificationService.verifyEmail(token, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/code")
    public ResponseEntity<Void> verifyEmailWithCode(
            @Valid @RequestBody VerifyEmailCodeRequest request) {
        emailVerificationService.verifyEmailWithCode(
            request.getEmail(),
            request.getCode()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<ResendVerificationResponse>> resendVerificationEmail(HttpServletRequest request) {
        String email = jwtService.extractUsernameFromRequest(request);
        Map<String, Object> result = emailVerificationService.resendVerificationEmail(email);
        ResendVerificationResponse response = new ResendVerificationResponse(
            Integer.parseInt(result.get("retryAfter").toString())
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 