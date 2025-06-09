package com.authkit.backend.infrastructure.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check and statistics endpoints")
public class HealthController {

    private final BuildProperties buildProperties;
    private final Instant startTime = Instant.now();

    @Value("${app.environment}")
    private String environment;

    @GetMapping("/ping")
    public String ping() {
        return "pong!";
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Ambiente
        stats.put("environment", environment);
        
        // Versão da API
        stats.put("version", buildProperties.getVersion());
        
        // Tempo de execução
        long uptimeSeconds = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        stats.put("uptime", uptimeSeconds);
        
        // Data de início
        stats.put("startTime", startTime.toString());
        
        // Informações do build
        stats.put("buildTime", buildProperties.getTime());
        stats.put("buildGroup", buildProperties.getGroup());
        stats.put("buildArtifact", buildProperties.getArtifact());
        
        return stats;
    }
} 