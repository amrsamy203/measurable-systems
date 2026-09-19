package com.amrsamy.dispatchgrid.domain.service;

/**
 * Simple in-memory token-bucket rate limiter (refill continuous by elapsed time).
 */
public class TokenBucketRateLimiter {

    private final double tokensPerSecond;
    private final double burst;
    private final Object lock = new Object();
    private double tokens;
    private long lastRefillNanos;

    public TokenBucketRateLimiter(double tokensPerSecond, double burst) {
        if (tokensPerSecond <= 0 || burst <= 0) {
            throw new IllegalArgumentException("tokensPerSecond and burst must be > 0");
        }
        this.tokensPerSecond = tokensPerSecond;
        this.burst = burst;
        this.tokens = burst;
        this.lastRefillNanos = System.nanoTime();
    }

    public boolean tryAcquire() {
        return tryAcquire(1.0);
    }

    public boolean tryAcquire(double permits) {
        if (permits <= 0) {
            return true;
        }
        synchronized (lock) {
            refill();
            if (tokens >= permits) {
                tokens -= permits;
                return true;
            }
            return false;
        }
    }

    public double availableTokens() {
        synchronized (lock) {
            refill();
            return tokens;
        }
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        if (elapsedSeconds <= 0) {
            return;
        }
        tokens = Math.min(burst, tokens + elapsedSeconds * tokensPerSecond);
        lastRefillNanos = now;
    }

    public double getTokensPerSecond() {
        return tokensPerSecond;
    }

    public double getBurst() {
        return burst;
    }
}
