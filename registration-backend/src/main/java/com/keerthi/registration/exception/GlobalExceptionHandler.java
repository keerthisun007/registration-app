// [Core Java] Package declaration: Groups this class under the exception handling layer
package com.keerthi.registration.exception;

// [Spring Framework] Importing HTTP status codes (400 BAD_REQUEST, 409 CONFLICT)
import org.springframework.http.HttpStatus;
// [Spring Framework] Importing ResponseEntity to package HTTP status codes and JSON error responses
import org.springframework.http.ResponseEntity;
// [Spring Web / Jakarta Validation] Importing exception thrown when @Valid fails on incoming request body
import org.springframework.web.bind.MethodArgumentNotValidException;
// [Spring Web] Importing @ExceptionHandler annotation to designate methods as exception listeners
import org.springframework.web.bind.annotation.ExceptionHandler;
// [Spring Web] Importing @RestControllerAdvice for centralized global exception interception across all controllers
import org.springframework.web.bind.annotation.RestControllerAdvice;

// [Core Java] Importing LinkedHashMap to maintain predictable order of JSON response keys
import java.util.LinkedHashMap;
// [Core Java] Importing Map interface for key-value structures
import java.util.Map;

/**
 * GlobalExceptionHandler Class
 * 
 * TECHNOLOGY: [Core Java] + [Spring Web @RestControllerAdvice] + [Jakarta Bean Validation]
 * USE: Intercepts and formats all application exceptions into user-friendly JSON responses.
 * WHY:
 *  1. Without this, Spring Boot returns ugly internal stack traces with HTTP 500 status.
 *  2. Centralizes error handling so controllers don't need messy try-catch blocks.
 *  3. Formats validation errors into clean field-level error messages for the frontend.
 */
@RestControllerAdvice // [Spring Web]: Combines @ControllerAdvice and @ResponseBody to intercept exceptions globally and return JSON
public class GlobalExceptionHandler {

    /**
     * Handler for Jakarta Bean Validation Errors (@Valid failure)
     * 
     * TECHNOLOGY: [Spring Web @ExceptionHandler] + [Jakarta Validation MethodArgumentNotValidException]
     * USE: Catches MethodArgumentNotValidException when user inputs invalid data (e.g., short name, bad email).
     * WHY: Extracts each field's specific error message and returns an organized errors map.
     * 
     * RETURNS: HTTP 400 Bad Request with JSON:
     * {
     *    "message": "Validation failed",
     *    "errors": { "email": "Enter a valid email", "phone": "Phone number must contain exactly 10 digits" }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        // [Core Java Map]: Stores fieldName -> errorMessage pairs
        Map<String, String> errors = new LinkedHashMap<>();

        // [Core Java Stream / Lambda]: Iterate through all field errors found by Jakarta validation
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),          // Field name (e.g. 'phone', 'email')
                                error.getDefaultMessage()  // Validation message defined in RegisterRequest
                        )
                );

        // [Core Java Map]: Build top-level response JSON
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Validation failed");
        response.put("errors", errors);

        // [Spring ResponseEntity]: Return HTTP 400 (Bad Request)
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handler for Duplicate Email Exception
     * 
     * TECHNOLOGY: [Spring Web @ExceptionHandler] + [Core Java Custom Exception]
     * USE: Catches EmailAlreadyExistsException thrown by UserService.
     * WHY: Returns standard HTTP 409 (Conflict) to indicate resource conflict.
     * 
     * RETURNS: HTTP 409 Conflict with {"message": "Email is already registered"}
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(
            EmailAlreadyExistsException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of("message", ex.getMessage())
                );
    }

    /**
     * Handler for Illegal Argument Exception (e.g., Passwords do not match)
     * 
     * TECHNOLOGY: [Spring Web @ExceptionHandler] + [Core Java IllegalArgumentException]
     * USE: Catches IllegalArgumentException thrown by UserService.
     * WHY: Returns HTTP 400 (Bad Request) with the error message.
     * 
     * RETURNS: HTTP 400 Bad Request with {"message": "Passwords do not match"}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of("message", ex.getMessage())
                );
    }
}