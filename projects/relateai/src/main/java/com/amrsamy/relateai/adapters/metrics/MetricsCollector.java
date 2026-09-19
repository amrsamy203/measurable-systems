package com.amrsamy.relateai.adapters.metrics;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MetricsCollector {

    private final AtomicLong challengesGenerated = new AtomicLong();
    private final AtomicLong interactionsRecorded = new AtomicLong();
    private final AtomicLong followsCreated = new AtomicLong();
    private final AtomicLong topicJoins = new AtomicLong();
    private final AtomicLong challengeResponses = new AtomicLong();
    private final AtomicLong uploads = new AtomicLong();
    private volatile Instant lastUpdated = Instant.now();

    public void incChallengesGenerated() {
        challengesGenerated.incrementAndGet();
        touch();
    }

    public void incInteractions() {
        interactionsRecorded.incrementAndGet();
        touch();
    }

    public void incFollows() {
        followsCreated.incrementAndGet();
        touch();
    }

    public void incTopicJoins() {
        topicJoins.incrementAndGet();
        touch();
    }

    public void incChallengeResponses() {
        challengeResponses.incrementAndGet();
        touch();
    }

    public void incUploads() {
        uploads.incrementAndGet();
        touch();
    }

    private void touch() {
        lastUpdated = Instant.now();
    }

    public Snapshot snapshot() {
        return new Snapshot(
                challengesGenerated.get(),
                interactionsRecorded.get(),
                followsCreated.get(),
                topicJoins.get(),
                challengeResponses.get(),
                uploads.get(),
                lastUpdated.toString());
    }

    public record Snapshot(
            long challengesGenerated,
            long interactionsRecorded,
            long followsCreated,
            long topicJoins,
            long challengeResponses,
            long uploads,
            String lastUpdated) {}
}
