// [Core Java] Package declaration: Groups this class under the service (business logic) layer
package com.keerthi.registration.service;

// [Core Java] Importing the DTO (Data Transfer Object) containing user form input
import com.keerthi.registration.dto.RegisterRequest;
// [Hibernate / JPA] Importing the User entity representing the MySQL database row
import com.keerthi.registration.entity.User;
// [Core Java] Importing custom exception thrown when email is already in use
import com.keerthi.registration.exception.EmailAlreadyExistsException;
// [Spring Data JPA] Importing UserRepository to perform database queries
import com.keerthi.registration.repository.UserRepository;
// [Spring Security] Importing PasswordEncoder interface for BCrypt one-way password hashing
import org.springframework.security.crypto.password.PasswordEncoder;
// [Spring Boot / Spring Framework] Importing @Service annotation to register as a managed Service bean
import org.springframework.stereotype.Service;

// [Core Java] Importing Locale for consistent lowercase normalization of strings
import java.util.Locale;

/**
 * UserService Class
 * 
 * TECHNOLOGY: [Core Java] + [Spring Boot Service] + [Spring Security] + [Spring Data JPA]
 * USE: Contains the core business logic and security rules for user registration operations.
 * WHY: Separating business logic from Controller (handling HTTP) and Repository (handling DB)
 *      follows the Single Responsibility Principle and MVC architecture best practices.
 */
@Service // [Spring Framework] Tells Spring IoC container: "Create, configure, and manage this class as a singleton bean"
public class UserService {

    // [Core Java final] Dependency variables: Injected once at creation time, ensuring immutability and thread-safety
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor Injection
     * 
     * TECHNOLOGY: [Spring IoC / Dependency Injection] + [Core Java Constructor]
     * USE: Injects UserRepository and PasswordEncoder beans provided by Spring.
     * WHY: Constructor injection is the recommended Spring pattern because:
     *      1. Ensures required dependencies cannot be null.
     *      2. Promotes loose coupling and makes unit testing simple.
     */
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * registerUser Method
     * 
     * TECHNOLOGY: [Core Java] + [Spring Security] + [Spring Data JPA / Hibernate]
     * USE: Validates business rules, hashes password, creates User entity, and saves to database.
     * WHY: Orchestrates the complete registration lifecycle in a secure, orderly sequence.
     */
    public void registerUser(RegisterRequest request) {

        // [Core Java]: Step 1 - Normalize email (remove surrounding whitespace and convert to lowercase)
        // WHY: Prevents duplicate accounts caused by casing differences (e.g. 'User@GMAIL.COM' vs 'user@gmail.com')
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // [Core Java]: Step 2 - Double-check that password and confirmPassword match on the backend
        // WHY: Even if frontend validates this, backend must never trust client-side validation alone
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // [Spring Data JPA + Hibernate]: Step 3 - Check database if the normalized email is already registered
        // WHY: Guarantees unique accounts per email and provides friendly feedback before DB collision
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        // [Spring Security / BCrypt]: Step 4 - Hash the raw password using BCrypt cryptographic algorithm
        // WHY: Converts plain text password into a secure 60-character salted hash (e.g., $2a$10$...).
        //      Java Spring Security provides this feature so plain passwords are NEVER stored in the database.
        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        // [Core Java + Hibernate JPA Entity]: Step 5 - Instantiate User entity with cleaned data and hashed password
        // WHY: Prepares the Java in-memory object to be converted into a MySQL row
        User user = new User(
                request.getName().trim(), // [Core Java] Trim leading/trailing spaces from user's name
                email,                    // [Core Java] Cleaned, unique email
                request.getPhone().trim(),// [Core Java] Cleaned 10-digit mobile number
                hashedPassword            // [Spring Security] Secure BCrypt password hash (NOT plain password!)
        );

        // [Spring Data JPA + Hibernate]: Step 6 - Save the User entity to MySQL database
        // WHY: Hibernate generates and executes the SQL: "INSERT INTO users (...) VALUES (...)"
        userRepository.save(user);
    }
}