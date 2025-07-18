package com.authkit.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Configura um executor dedicado para processamento de emails
     * com pool de threads otimizado para operações I/O intensivas
     */
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size: número mínimo de threads sempre ativos
        executor.setCorePoolSize(2);
        
        // Max pool size: número máximo de threads que podem ser criados
        executor.setMaxPoolSize(10);
        
        // Queue capacity: número máximo de tarefas na fila
        executor.setQueueCapacity(100);
        
        // Thread name prefix para facilitar debugging
        executor.setThreadNamePrefix("email-async-");
        
        // Keep alive time: tempo que threads extras ficam ativos após uso
        executor.setKeepAliveSeconds(60);
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Await termination time: tempo máximo para aguardar finalização
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        return executor;
    }
} 