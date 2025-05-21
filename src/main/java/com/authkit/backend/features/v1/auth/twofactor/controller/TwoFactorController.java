package com.authkit.backend.features.v1.auth.twofactor.controller;

import com.authkit.backend.features.v1.auth.common.dto.response.TokensResponse;
import com.authkit.backend.features.v1.auth.twofactor.dto.RemoveTwoFactorRequest;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorMethodResponse;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorSetupRequest;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorSetupResponse;
import com.authkit.backend.features.v1.auth.twofactor.dto.TwoFactorVerificationRequest;
import com.authkit.backend.features.v1.auth.twofactor.service.TwoFactorService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/2fa")
@RequiredArgsConstructor
@Tag(name = "2FA", description = "Endpoints for 2FA methods")
public class TwoFactorController {
    
    private final TwoFactorService twoFactorService;

    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponse> setup2FA(
            @RequestBody TwoFactorSetupRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(twoFactorService.setup2FA(request, httpRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<TokensResponse> verify2FA(
            @RequestBody TwoFactorVerificationRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(twoFactorService.verify2FA(request, httpRequest));
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> disable2FA(
            @RequestBody RemoveTwoFactorRequest request, HttpServletRequest httpRequest) {
        twoFactorService.disable2FA(request, httpRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/methods")
    public ResponseEntity<List<TwoFactorMethodResponse>> listMethods(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(twoFactorService.listMethods(httpRequest));
    }
} 