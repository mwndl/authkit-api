package com.authkit.backend.features.v1.notification.controller;

import com.authkit.backend.domain.model.Notification;
import com.authkit.backend.features.v1.notification.dto.NotificationDTO;
import com.authkit.backend.features.v1.notification.dto.UnreadCountDTO;
import com.authkit.backend.features.v1.notification.service.NotificationService;
import com.authkit.backend.shared.dto.response.ApiResponse;
import com.authkit.backend.shared.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getUserNotifications(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = jwtService.extractUsernameFromRequest(request);
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getUserNotifications(email, pageable);
        Page<NotificationDTO> notificationDTOs = notifications.map(NotificationDTO::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(notificationDTOs));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<UnreadCountDTO> getUnreadNotificationCount(HttpServletRequest request) {
        String email = jwtService.extractUsernameFromRequest(request);
        long count = notificationService.getUnreadNotificationCount(email);
        return ResponseEntity.ok(UnreadCountDTO.builder().count(count).build());
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable UUID notificationId,
            HttpServletRequest request) {
        String email = jwtService.extractUsernameFromRequest(request);
        notificationService.markNotificationAsRead(email, notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(HttpServletRequest request) {
        String email = jwtService.extractUsernameFromRequest(request);
        notificationService.markAllNotificationsAsRead(email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationId,
            HttpServletRequest request) {
        String email = jwtService.extractUsernameFromRequest(request);
        notificationService.deleteNotification(email, notificationId);
        return ResponseEntity.noContent().build();
    }
} 