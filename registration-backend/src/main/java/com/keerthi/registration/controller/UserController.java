// [Core Java] Package declaration: Groups this class under the presentation/web controller layer
package com.keerthi.registration.controller;

// [Core Java] Importing the DTO that maps the incoming JSON request body
import com.keerthi.registration.dto.RegisterRequest;
// [Core Java] Importing the UserService that contains business logic
import com.keerthi.registration.service.UserService;
// [Jakarta Bean Validation] Importing @Valid to trigger automated validation rules on incoming requests
import jakarta.validation.Valid;
// [Spring Framework] Importing HttpStatus enum representing standard HTTP status codes (200 OK, 201 CREATED)
import org.springframework.http.HttpStatus;
// [Spring Framework] Importing ResponseEntity to wrap response data along with HTTP headers and status codes
import org.springframework.http.ResponseEntity;
// [Spring Web / Spring MVC] Importing REST web annotations (@RestController, @RequestMapping, @PostMapping, @GetMapping, @CrossOrigin, @RequestBody)
import org.springframework.web.bind.annotation.*;

// [Core Java] Importing Map utility for building key-value response bodies
import java.util.Map;

/**
 * UserController Class
 * 
 * TECHNOLOGY: [Core Java] + [Spring Web / Spring MVC] + [Jakarta Bean Validation]
 * USE: Acts as the REST API Controller that exposes HTTP endpoints to the frontend.
 * WHY: Receives HTTP requests from the browser, validates payload, invokes the service layer,
 *      and returns standardized JSON responses back to the client.
 */
@RestController // [Spring Web] Combines @Controller and @ResponseBody: Spring serializes returned Java objects into JSON
@RequestMapping("/api/users") // [Spring Web] Base URL prefix for all endpoints in this controller (http://localhost:8080/api/users)
@CrossOrigin(origins = "*") // [Spring Web] Cross-Origin Resource Sharing: Permits requests from any origin/port (including frontend)
public class UserController {

    // [Core Java final] Dependency variable holding reference to UserService
    private final UserService userService;

    /**
     * Constructor Injection
     * 
     * TECHNOLOGY: [Spring IoC / Dependency Injection] + [Core Java Constructor]
     * USE: Injects the UserService dependency.
     * WHY: Guarantees UserController has access to user business logic when handling web requests.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Health Check Endpoint: GET /api/users/health
     * 
     * TECHNOLOGY: [Spring Web @GetMapping] + [Spring ResponseEntity]
     * USE: Allows clients or monitoring tools to verify if the backend server is active and running.
     * WHY: A lightweight endpoint to test server status without touching the database.
     * RETURNS: HTTP 200 OK with {"message": "Backend is running"}
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(
                Map.of("message", "Backend is running"));
    }

    /**
     * User Registration Endpoint: POST /api/users/register
     * 
     * TECHNOLOGY: [Spring Web @PostMapping] + [Jakarta Validation @Valid] + [Jackson @RequestBody]
     * USE: Receives user registration data submitted from the frontend form.
     * WHY: 
     *   - @Valid [Jakarta Validation]: Triggers validation rules on RegisterRequest fields before method executes.
     *   - @RequestBody [Jackson / Spring Web]: Deserializes incoming JSON string into Java RegisterRequest instance.
     * RETURNS: HTTP 201 CREATED with {"message": "Registration successful"}
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {

        // [Core Java]: Delegate registration processing (validation, hashing, saving) to UserService
        userService.registerUser(request);

        // [Spring ResponseEntity]: Return HTTP 201 (Created) along with success JSON response to frontend
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        Map.of(
                                "message",
                                "Registration successful"));
    }
}