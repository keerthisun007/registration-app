package com.keerthi.registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard API Error Response
 *
 * Provides a uniform error structure for client-side error handling, debugging, and auditability.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    int status,
    String error,
    String message,
    Map<String, String> errors,
    String path,
    LocalDateTime timestamp
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, null, path, LocalDateTime.now());
    }

    public static ApiErrorResponse ofValidation(
        int status,
        String error,
        String message,
        Map<String, String> errors,
        String path
    ) {
        return new ApiErrorResponse(status, error, message, errors, path, LocalDateTime.now());
    }
}
