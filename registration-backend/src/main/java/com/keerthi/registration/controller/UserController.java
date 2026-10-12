package com.keerthi.registration.controller;

import com.keerthi.registration.dto.ApiResponse;
import com.keerthi.registration.dto.RegisterRequest;
import com.keerthi.registration.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController Class
 * 
 * Production REST controller handling incoming HTTP requests for user operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Health Check Endpoint: GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Backend is running"));
    }

    /**
     * User Registration Endpoint: POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Processing user registration request");
        userService.registerUser(request);
        log.info("User registration completed successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful"));
    }
}