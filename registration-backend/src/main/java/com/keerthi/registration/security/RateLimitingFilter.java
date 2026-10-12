package com.keerthi.registration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keerthi.registration.dto.ApiErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Production Rate Limiting Filter
 *
 * Protects CPU-intensive registration endpoints (BCrypt password hashing) against
 * brute-force abuse, credential stuffing, and denial-of-service spamming.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, ClientWindow> requestCounts = new ConcurrentHashMap<>();

    @Value("${app.rate-limiting.enabled:true}")
    private boolean rateLimitingEnabled;

    @Value("${app.rate-limiting.max-requests:10}")
    private int maxRequestsPerMinute;

    public RateLimitingFilter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!rateLimitingEnabled) {
            chain.doFilter(request, response);
            return;
        }

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String path = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();

            // Only rate-limit registration attempts
            if ("POST".equalsIgnoreCase(method) && path.endsWith("/api/users/register")) {
                String clientIp = extractClientIp(httpRequest);
                long currentMinute = System.currentTimeMillis() / 60000;

                // Cleanup stale IP entries if map exceeds 5000 items
                if (requestCounts.size() > 5000) {
                    requestCounts.entrySet().removeIf(entry -> entry.getValue().minuteWindow < currentMinute);
                }

                ClientWindow clientWindow = requestCounts.compute(clientIp, (key, existing) -> {
                    if (existing == null || existing.minuteWindow < currentMinute) {
                        return new ClientWindow(currentMinute, new AtomicInteger(1));
                    }
                    existing.counter.incrementAndGet();
                    return existing;
                });

                if (clientWindow.counter.get() > maxRequestsPerMinute) {
                    log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, path);

                    httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    httpResponse.setHeader("Retry-After", "60");

                    ApiErrorResponse errorResponse = new ApiErrorResponse(
                            HttpStatus.TOO_MANY_REQUESTS.value(),
                            "Too Many Requests",
                            "Rate limit exceeded. Please wait a minute before trying again.",
                            null,
                            path,
                            LocalDateTime.now()
                    );

                    httpResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }

    private static class ClientWindow {
        final long minuteWindow;
        final AtomicInteger counter;

        ClientWindow(long minuteWindow, AtomicInteger counter) {
            this.minuteWindow = minuteWindow;
            this.counter = counter;
        }
    }
}
