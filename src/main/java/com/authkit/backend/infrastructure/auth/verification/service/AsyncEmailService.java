package com.authkit.backend.infrastructure.auth.verification.service;

import com.authkit.backend.domain.model.User;
import com.authkit.backend.infrastructure.utils.EmailServiceHelper;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailService {

    private final EmailServiceHelper emailService;
    private final VerificationEmailService verificationEmailService;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Envia email de verificação de forma assíncrona
     * Retorna imediatamente sem aguardar o envio
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendVerificationEmailAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Iniciando envio assíncrono de email de verificação para: {}", user.getEmail());
                verificationEmailService.sendVerificationEmail(user);
                log.info("Email de verificação enviado com sucesso para: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Erro ao enviar email de verificação para {}: {}", user.getEmail(), e.getMessage());
                handleEmailError(user, e);
            }
        });
    }

    /**
     * Envia email de reset de senha de forma assíncrona
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendPasswordResetEmailAsync(String email, String resetUrl) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Iniciando envio assíncrono de email de reset de senha para: {}", email);
                emailService.sendPasswordResetEmail(email, resetUrl);
                log.info("Email de reset de senha enviado com sucesso para: {}", email);
            } catch (Exception e) {
                log.error("Erro ao enviar email de reset de senha para {}: {}", email, e.getMessage());
                handlePasswordResetEmailError(email, e);
            }
        });
    }

    /**
     * Envia email de verificação com retry automático
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendVerificationEmailWithRetry(User user) {
        return CompletableFuture.runAsync(() -> {
            int attempts = 0;
            Exception lastException = null;

            while (attempts < MAX_RETRY_ATTEMPTS) {
                try {
                    attempts++;
                    log.info("Tentativa {} de envio de email de verificação para: {}", attempts, user.getEmail());
                    
                    verificationEmailService.sendVerificationEmail(user);
                    log.info("Email de verificação enviado com sucesso na tentativa {} para: {}", attempts, user.getEmail());
                    return; // Sucesso, sai do loop
                    
                } catch (Exception e) {
                    lastException = e;
                    log.warn("Tentativa {} falhou para {}: {}", attempts, user.getEmail(), e.getMessage());
                    
                    if (attempts < MAX_RETRY_ATTEMPTS) {
                        try {
                            // Espera exponencial: 1s, 2s, 4s
                            long delay = (long) Math.pow(2, attempts - 1) * 1000;
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            log.error("Thread interrompida durante retry de email para: {}", user.getEmail());
                            break;
                        }
                    }
                }
            }

            // Todas as tentativas falharam
            log.error("Todas as {} tentativas de envio de email falharam para: {}", MAX_RETRY_ATTEMPTS, user.getEmail());
            handleEmailError(user, lastException);
        });
    }

    /**
     * Trata erros de envio de email de verificação
     */
    private void handleEmailError(User user, Exception e) {
        // Aqui você pode implementar:
        // - Logging para análise
        // - Métricas para monitoramento
        // - Notificação para administradores
        // - Salvamento em fila de retry posterior
        
        log.error("Falha crítica no envio de email de verificação para usuário {} (ID: {}): {}", 
                 user.getEmail(), user.getId(), e.getMessage());
        
        // TODO: Implementar sistema de notificação para admins
        // TODO: Implementar métricas de falha
        // TODO: Implementar fila de retry com backoff exponencial
    }

    /**
     * Trata erros de envio de email de reset de senha
     */
    private void handlePasswordResetEmailError(String email, Exception e) {
        log.error("Falha crítica no envio de email de reset de senha para: {}", email, e);
        
        // TODO: Implementar sistema de notificação para admins
        // TODO: Implementar métricas de falha
    }
} 