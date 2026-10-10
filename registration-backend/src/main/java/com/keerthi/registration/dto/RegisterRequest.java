// [Core Java] Package declaration: Groups this class under the Data Transfer Object (DTO) layer
package com.keerthi.registration.dto;

// [Jakarta Bean Validation] Importing validation annotations used to enforce business rules on input fields
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest Class (DTO - Data Transfer Object)
 * 
 * TECHNOLOGY: [Core Java Class] + [Jakarta Bean Validation]
 * USE: Encapsulates the user input payload sent via HTTP POST from frontend to backend.
 * WHY:
 *  1. Security: Decouples the external API contract from internal database entity (User.java).
 *  2. Validation: Validates incoming raw input before any database or business operations occur.
 *  3. Encapsulation: Groups related registration fields into a single strongly-typed Java object.
 */
public class RegisterRequest {

    // [Jakarta Validation @NotBlank + @Size]: Validates full name is present and between 3 and 100 characters
    @NotBlank(message = "Name is required") // Ensures name is not null, not empty, and not just whitespace
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name; // [Core Java String]

    // [Jakarta Validation @NotBlank + @Email]: Validates email format (e.g., user@domain.com)
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email") // Verifies valid email structure using regular expressions
    private String email; // [Core Java String]

    // [Jakarta Validation @NotBlank + @Pattern]: Enforces exactly 10 numeric digits using regular expression
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$", // Regex: ^ begins with, [0-9] numbers only, {10} exactly 10 digits, $ ends
        message = "Phone number must contain exactly 10 digits"
    )
    private String phone; // [Core Java String]

    // [Jakarta Validation @NotBlank + @Size]: Enforces password length (min 8 chars, max 72 chars for BCrypt)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
    private String password; // [Core Java String]

    // [Jakarta Validation @NotBlank]: Enforces confirm password field is not empty
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword; // [Core Java String]

    // [Core Java] Getter and Setter methods: Allow Jackson (JSON deserializer) and Spring to access private fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}