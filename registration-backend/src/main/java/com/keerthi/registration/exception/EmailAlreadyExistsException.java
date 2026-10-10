// [Core Java] Package declaration: Groups this class under the custom exceptions layer
package com.keerthi.registration.exception;

/**
 * EmailAlreadyExistsException Class
 * 
 * TECHNOLOGY: [Core Java Custom Exception] (Extends RuntimeException)
 * USE: Custom unchecked exception thrown when an email is already registered in the system.
 * WHY:
 *  1. Semantic Clarity: Makes code self-explanatory compared to generic RuntimeException.
 *  2. Target Exception Handling: Allows GlobalExceptionHandler to intercept this specific error
 *     and return a friendly HTTP 409 CONFLICT status code back to the frontend.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructor accepting error message
     * 
     * TECHNOLOGY: [Core Java Constructor]
     * @param message Detailed error explanation passed to super (e.g. "Email is already registered")
     */
    public EmailAlreadyExistsException(String message) {
        super(message); // [Core Java]: Passes error message to parent RuntimeException class
    }
}