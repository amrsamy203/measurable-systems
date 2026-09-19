package com.amrsamy.caseflow.adapters.metrics;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FailureInjectionState {

    private final AtomicLong slowProcessingMs = new AtomicLong(0);
    private final AtomicInteger failurePercent = new AtomicInteger(0);

    public long getSlowProcessingMs() {
        return slowProcessingMs.get();
    }

    public int getFailurePercent() {
        return failurePercent.get();
    }

    public void configure(long slowProcessingMs, int failurePercent) {
        this.slowProcessingMs.set(Math.max(0, slowProcessingMs));
        this.failurePercent.set(Math.min(100, Math.max(0, failurePercent)));
    }
}
