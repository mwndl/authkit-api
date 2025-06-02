package com.authkit.backend.infrastructure.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/test")
@RequiredArgsConstructor
@Tag(name = "Admin Test", description = "Test endpoints for admin users only")
public class AdminTestController {

    @GetMapping("/hello")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Test endpoint for admin users", description = "Returns a hello message if the user has admin role")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not an admin")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello Admin! You have access to this endpoint.");
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get admin statistics", description = "Returns some dummy statistics for admin users")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not an admin")
    public ResponseEntity<String> getStats() {
        return ResponseEntity.ok("Admin Statistics: 100 users, 50 active sessions, 10 failed login attempts");
    }
} 