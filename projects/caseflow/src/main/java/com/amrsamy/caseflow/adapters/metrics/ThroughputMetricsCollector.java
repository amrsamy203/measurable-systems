package com.amrsamy.caseflow.adapters.metrics;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
public class ThroughputMetricsCollector {

    private static final int LATENCY_WINDOW = 500;
    private static final long WINDOW_MS = 10_000L;

    private final LongAdder eventsProcessed = new LongAdder();
    private final LongAdder failureCount = new LongAdder();
    private final AtomicLong lastUpdatedEpochMs = new AtomicLong(System.currentTimeMillis());

    private final Object latencyLock = new Object();
    private final List<Long> recentLatenciesMs = new ArrayList<>();

    private final Object rateLock = new Object();
    private final List<Long> eventTimestamps = new ArrayList<>();

    private volatile long queueDepthOverride = -1;

    public void recordSuccess(long latencyMs) {
        eventsProcessed.increment();
        lastUpdatedEpochMs.set(System.currentTimeMillis());
        synchronized (latencyLock) {
            recentLatenciesMs.add(latencyMs);
            if (recentLatenciesMs.size() > LATENCY_WINDOW) {
                recentLatenciesMs.remove(0);
            }
        }
        synchronized (rateLock) {
            long now = System.currentTimeMillis();
            eventTimestamps.add(now);
            pruneOld(eventTimestamps, now);
        }
    }

    public void recordFailure() {
        failureCount.increment();
        lastUpdatedEpochMs.set(System.currentTimeMillis());
    }

    public void setQueueDepth(long depth) {
        this.queueDepthOverride = depth;
    }

    public ThroughputSnapshot snapshot(long queueDepth) {
        long depth = queueDepthOverride >= 0 ? queueDepthOverride : queueDepth;
        double eps;
        synchronized (rateLock) {
            long now = System.currentTimeMillis();
            pruneOld(eventTimestamps, now);
            eps = eventTimestamps.size() / (WINDOW_MS / 1000.0);
        }
        long p95;
        synchronized (latencyLock) {
            p95 = percentile95(recentLatenciesMs);
        }
        return new ThroughputSnapshot(
                eventsProcessed.sum(),
                round2(eps),
                p95,
                depth,
                failureCount.sum(),
                Instant.ofEpochMilli(lastUpdatedEpochMs.get()));
    }

    private void pruneOld(List<Long> timestamps, long now) {
        timestamps.removeIf(ts -> now - ts > WINDOW_MS);
    }

    private long percentile95(List<Long> values) {
        if (values.isEmpty()) {
            return 0;
        }
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(index);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public record ThroughputSnapshot(
            long eventsProcessed,
            double eventsPerSecond,
            long p95LatencyMs,
            long queueDepth,
            long failureCount,
            Instant lastUpdated
    ) {
    }
}
