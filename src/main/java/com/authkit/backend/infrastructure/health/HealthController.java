package com.authkit.backend.infrastructure.health;

import com.authkit.backend.infrastructure.utils.EmailServiceHelper;
import com.authkit.backend.shared.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final EmailServiceHelper emailServiceHelper;
    private final BuildProperties buildProperties;
    private final Instant startTime = Instant.now();

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.environment}")
    private String environment;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("application", appName);
        response.put("environment", environment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Environment
        stats.put("environment", environment);
        
        // API Version
        stats.put("version", buildProperties.getVersion());
        
        // Uptime in seconds
        long uptimeSeconds = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        stats.put("uptime", uptimeSeconds);
        
        // Start time
        stats.put("startTime", startTime.toString());
        
        // Build information
        stats.put("buildTime", buildProperties.getTime());
        stats.put("buildGroup", buildProperties.getGroup());
        stats.put("buildArtifact", buildProperties.getArtifact());
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/test-email")
    public ResponseEntity<ApiResponse<String>> testEmail(@RequestParam String to) {
        try {
            String subject = "Test Email - AuthKit";
            String message = String.format("""
                This is a test email from AuthKit.
                
                Timestamp: %s
                Environment: %s
                Application: %s
                
                If you received this email, the AWS SES configuration is working correctly!
                """, 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                environment,
                appName
            );
            
            emailServiceHelper.sendEmail(to, subject, message);
            
            log.info("Test email sent successfully to: {}", to);
            return ResponseEntity.ok(ApiResponse.success("Test email sent successfully"));
            
        } catch (MailException e) {
            log.error("Failed to send test email to: {}", to, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to send test email: " + e.getMessage()));
        }
    }
} 