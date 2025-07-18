package com.authkit.backend.infrastructure.init;

import com.authkit.backend.domain.enums.UserStatus;
import com.authkit.backend.domain.model.User;
import com.authkit.backend.domain.repository.user.UserRepository;
import com.authkit.backend.infrastructure.utils.ValidationServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationServiceHelper validationService;

    @Value("${app.admin.user:}")
    private String adminUser;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.name:Admin}")
    private String adminName;

    @Value("${app.admin.surname:User}")
    private String adminSurname;

    @Override
    public void run(String... args) throws Exception {
        if (shouldCreateAdminUser()) {
            createAdminUser();
        }
    }

    private boolean shouldCreateAdminUser() {
        return adminUser != null && !adminUser.trim().isEmpty() &&
               adminPassword != null && !adminPassword.trim().isEmpty() &&
               adminEmail != null && !adminEmail.trim().isEmpty();
    }

    private void createAdminUser() {
        try {
            // Check if admin user already exists
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                log.info("Admin user already exists with email: {}", adminEmail);
                return;
            }

            if (userRepository.findByUsername(adminUser).isPresent()) {
                log.info("Admin user already exists with username: {}", adminUser);
                return;
            }

            // Validate inputs
            validationService.validateEmail(adminEmail);
            validateAdminUsername(adminUser);
            validationService.validateName(adminName);
            validationService.validateName(adminSurname);
            validationService.validatePassword(adminPassword);

            // Create admin user
            User adminUserEntity = new User();
            adminUserEntity.setEmail(adminEmail);
            adminUserEntity.setUsername(adminUser);
            adminUserEntity.setPasswordHash(passwordEncoder.encode(adminPassword));
            adminUserEntity.setName(adminName);
            adminUserEntity.setSurname(adminSurname);
            adminUserEntity.setStatus(UserStatus.ACTIVE); // Admin user is automatically verified
            adminUserEntity.setTwoFactorMethods(new ArrayList<>());

            userRepository.save(adminUserEntity);

            log.info("Admin user created successfully: {} ({})", adminUser, adminEmail);

        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage(), e);
        }
    }

    private void validateAdminUsername(String username) {
        // Admin username validation - allows reserved usernames
        String usernameRegex = "^(?!.*[._-]{2})(?![._-])[a-z0-9._-]{4,15}(?<![._-])$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(usernameRegex);
        java.util.regex.Matcher matcher = pattern.matcher(username);

        if (!matcher.matches()) {
            throw new com.authkit.backend.shared.exception.ApiException(
                com.authkit.backend.shared.exception.ApiErrorCode.USERNAME_INVALID
            );
        }
        
        if (userRepository.existsByUsername(username)) {
            throw new com.authkit.backend.shared.exception.ApiException(
                com.authkit.backend.shared.exception.ApiErrorCode.USERNAME_ALREADY_TAKEN
            );
        }
    }
} 