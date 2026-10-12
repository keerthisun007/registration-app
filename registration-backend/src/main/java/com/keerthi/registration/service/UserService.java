package com.keerthi.registration.service;

import com.keerthi.registration.dto.RegisterRequest;
import com.keerthi.registration.entity.User;
import com.keerthi.registration.exception.EmailAlreadyExistsException;
import com.keerthi.registration.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * UserService Class
 * 
 * Production service layer executing business rules and transactional data persistence.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with email normalization, password encryption, and duplicate prevention.
     */
    @Transactional
    public void registerUser(RegisterRequest request) {
        // Step 1 - Normalize email
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        log.info("Starting registration workflow for email: {}", maskEmail(email));

        // Step 2 - Verify password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Registration failed: password and confirmPassword mismatch for email: {}", maskEmail(email));
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Step 3 - Check for existing account
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Registration failed: email already exists: {}", maskEmail(email));
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        // Step 4 - Hash the raw password securely with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Step 5 - Instantiate entity
        User user = new User(
                request.getName().trim(),
                email,
                request.getPhone().trim(),
                hashedPassword
        );

        // Step 6 - Save entity to database
        userRepository.save(user);
        log.info("Successfully registered user account for email: {}", maskEmail(email));
    }

    /**
     * Helper to mask PII (email address) in logs for security compliance (GDPR/HIPAA/SOC2).
     * Example: 'john.doe@example.com' -> 'j***e@example.com'
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        String namePart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (namePart.length() <= 2) {
            return namePart.charAt(0) + "***" + domainPart;
        }
        return namePart.charAt(0) + "***" + namePart.charAt(namePart.length() - 1) + domainPart;
    }
}