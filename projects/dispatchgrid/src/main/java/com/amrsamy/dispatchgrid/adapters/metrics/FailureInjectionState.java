package com.amrsamy.dispatchgrid.adapters.metrics;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FailureInjectionState {

    public static final String GLOBAL = "GLOBAL";

    private final Map<String, AtomicLong> slowMsByProvider = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failPctByProvider = new ConcurrentHashMap<>();

    public void configure(String providerKey, long slowMs, int failPercent) {
        String key = normalize(providerKey);
        slowMsByProvider.computeIfAbsent(key, k -> new AtomicLong()).set(Math.max(0, slowMs));
        failPctByProvider.computeIfAbsent(key, k -> new AtomicInteger())
                .set(Math.min(100, Math.max(0, failPercent)));
    }

    public long slowMsFor(String providerKey) {
        long specific = slowMsByProvider.getOrDefault(normalize(providerKey), new AtomicLong(0)).get();
        long global = slowMsByProvider.getOrDefault(GLOBAL, new AtomicLong(0)).get();
        return Math.max(specific, global);
    }

    public int failPercentFor(String providerKey) {
        int specific = failPctByProvider.getOrDefault(normalize(providerKey), new AtomicInteger(0)).get();
        int global = failPctByProvider.getOrDefault(GLOBAL, new AtomicInteger(0)).get();
        return Math.max(specific, global);
    }

    public boolean shouldFail(String providerKey) {
        int pct = failPercentFor(providerKey);
        if (pct <= 0) {
            return false;
        }
        return ThreadLocalRandom.current().nextInt(100) < pct;
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> out = new ConcurrentHashMap<>();
        for (String key : slowMsByProvider.keySet()) {
            out.put(key, Map.of(
                    "slowMs", slowMsByProvider.get(key).get(),
                    "failPercent", failPctByProvider.getOrDefault(key, new AtomicInteger(0)).get()));
        }
        for (String key : failPctByProvider.keySet()) {
            out.putIfAbsent(key, Map.of(
                    "slowMs", slowMsByProvider.getOrDefault(key, new AtomicLong(0)).get(),
                    "failPercent", failPctByProvider.get(key).get()));
        }
        return out;
    }

    private String normalize(String providerKey) {
        return providerKey == null || providerKey.isBlank() ? GLOBAL : providerKey.trim().toUpperCase();
    }
}
