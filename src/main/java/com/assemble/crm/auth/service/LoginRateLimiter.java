package com.assemble.crm.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory sliding-window rate limiter for login attempts, keyed by
 * email (and optionally IP). Good enough for a single-instance deployment;
 * swap for Redis-backed limiting when scaling horizontally.
 */
@Component
public class LoginRateLimiter {

    @Value("${app.ratelimit.login.max-attempts}")
    private int maxAttempts;

    @Value("${app.ratelimit.login.window-seconds}")
    private long windowSeconds;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    private record Attempt(int count, Instant windowStart) {}

    public boolean isBlocked(String key) {
        Attempt a = attempts.get(key);
        if (a == null) return false;
        if (a.windowStart().plusSeconds(windowSeconds).isBefore(Instant.now())) {
            attempts.remove(key);
            return false;
        }
        return a.count() >= maxAttempts;
    }

    public void recordFailure(String key) {
        attempts.compute(key, (k, a) -> {
            Instant now = Instant.now();
            if (a == null || a.windowStart().plusSeconds(windowSeconds).isBefore(now)) {
                return new Attempt(1, now);
            }
            return new Attempt(a.count() + 1, a.windowStart());
        });
    }

    public void reset(String key) {
        attempts.remove(key);
    }
}
