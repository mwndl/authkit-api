package com.authkit.backend.infrastructure.auth.passkey.controller;

import com.authkit.backend.domain.model.Passkey;
import com.authkit.backend.infrastructure.auth.passkey.dto.*;
import com.authkit.backend.infrastructure.auth.passkey.service.PasskeyService;
import com.authkit.backend.infrastructure.utils.audit.Audited;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/passkeys")
@RequiredArgsConstructor
@Tag(name = "Passkey", description = "Endpoints for managing passkeys")
public class PasskeyController {

    private final PasskeyService passkeyService;

    @PostMapping("/register/start")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Start passkey registration",
        description = "Initiates the passkey registration process for the authenticated user",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK - Registration started successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
        }
    )
    public ResponseEntity<PasskeyRegistrationResponse> startRegistration(
        @Valid @RequestBody PasskeyRegistrationRequest request
    ) {
        return ResponseEntity.ok(passkeyService.startRegistration(request));
    }

    @PostMapping("/register/finish")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Finish passkey registration",
            description = "Completes the passkey registration process by saving the generated credentials",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Passkey registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid credentials"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
            }
    )
    public ResponseEntity<Void> finishRegistration(@RequestBody PasskeyRegistrationFinishRequest request) {
        passkeyService.finishRegistration(
            request.getCredentialId(),
            request.getPublicKey(),
            request.getDeviceName(),
            request.getDeviceType()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify/start")
    @Operation(
        summary = "Start passkey verification",
        description = "Initiates the passkey verification process for a user",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK - Verification started successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid email"),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found")
        }
    )
    public ResponseEntity<PasskeyVerificationResponse> startVerification(
        @Valid @RequestBody PasskeyVerificationStartRequest request
    ) {
        return ResponseEntity.ok(passkeyService.startVerification(request.getEmail()));
    }

    @PostMapping("/verify/finish")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Finish passkey verification",
        description = "Completes the passkey verification process",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK - Verification completed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Not Found - Passkey not found")
        }
    )
    public ResponseEntity<Void> finishVerification(
        @Valid @RequestBody PasskeyVerificationFinishRequest request,
        HttpServletRequest httpRequest
    ) {
        // TODO: Implement signature verification
        passkeyService.finishVerification(request.getCredentialId(), httpRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "List passkeys",
        description = "Returns a paginated list of the authenticated user's passkeys",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK - Passkeys returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
        }
    )
    public ResponseEntity<Page<PasskeyResponse>> listPasskeys(Pageable pageable) {
        return ResponseEntity.ok(passkeyService.listPasskeys(pageable)
            .map(PasskeyResponse::fromEntity));
    }

    @DeleteMapping("/{passkeyId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Delete passkey",
        description = "Deletes a specific passkey for the authenticated user",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK - Passkey deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Not Found - Passkey not found")
        }
    )
    public ResponseEntity<Void> deletePasskey(@PathVariable UUID passkeyId) {
        passkeyService.deletePasskey(passkeyId);
        return ResponseEntity.ok().build();
    }
} 