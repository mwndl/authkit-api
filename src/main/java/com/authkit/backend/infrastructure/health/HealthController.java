package com.authkit.backend.infrastructure.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check endpoint")
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        return "pong!";
    }
} 