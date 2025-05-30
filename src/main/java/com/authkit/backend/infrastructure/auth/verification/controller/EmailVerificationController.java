package com.authkit.backend.infrastructure.auth.verification.controller;

import com.authkit.backend.infrastructure.auth.common.dto.response.TokensResponse;
import com.authkit.backend.infrastructure.auth.verification.dto.request.VerifyEmailCodeRequest;
import com.authkit.backend.infrastructure.auth.verification.dto.request.VerifyEmailTokenRequest;
import com.authkit.backend.infrastructure.auth.verification.dto.response.ResendVerificationResponse;
import com.authkit.backend.infrastructure.auth.verification.service.EmailVerificationService;
import com.authkit.backend.shared.dto.response.ApiResponse;
import com.authkit.backend.shared.security.JwtService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/verify")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "Endpoints for account verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<TokensResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailTokenRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(emailVerificationService.verifyEmail(request.getToken(), httpRequest));
    }

    @PostMapping("/code")
    public ResponseEntity<Void> verifyEmailWithCode(
            @Valid @RequestBody VerifyEmailCodeRequest request,
            HttpServletRequest httpRequest) {
        String email = jwtService.extractUsernameFromRequest(httpRequest);
        emailVerificationService.verifyEmailWithCode(email, request.getCode());
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