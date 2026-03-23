package com.dinotoptrumps.shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LOGIN_LIMIT = 5;
    private static final int REGISTER_LIMIT = 3;
    private static final long WINDOW_SECONDS = 60;

    private final boolean rateLimitEnabled;

    private final Map<String, RateWindow> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, RateWindow> registerAttempts = new ConcurrentHashMap<>();

    public RateLimitFilter(@Value("${app.rate-limit-enabled:true}") boolean rateLimitEnabled) {
        this.rateLimitEnabled = rateLimitEnabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (!"POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        if ("/api/v1/auth/login".equals(path) && isRateLimited(loginAttempts, clientIp, LOGIN_LIMIT)) {
            sendTooManyRequests(response, "Too many login attempts. Try again in 1 minute.");
            return;
        }

        if ("/api/v1/auth/register".equals(path) && isRateLimited(registerAttempts, clientIp, REGISTER_LIMIT)) {
            sendTooManyRequests(response, "Too many registration attempts. Try again in 1 minute.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(Map<String, RateWindow> store, String key, int limit) {
        RateWindow window = store.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new RateWindow();
            }
            return existing;
        });
        return window.incrementAndCheck(limit);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendTooManyRequests(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(
                "{\"type\":\"about:blank\",\"title\":\"Too Many Requests\","
                + "\"status\":429,\"detail\":\"" + message + "\"}"
        );
    }

    private static class RateWindow {
        private final Instant start = Instant.now();
        private final AtomicInteger count = new AtomicInteger(0);

        boolean isExpired() {
            return Instant.now().isAfter(start.plusSeconds(WINDOW_SECONDS));
        }

        boolean incrementAndCheck(int limit) {
            return count.incrementAndGet() > limit;
        }
    }
}
