package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.adapters.metrics.FailureInjectionState;
import com.amrsamy.dispatchgrid.domain.model.Channel;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

abstract class AbstractFakeProvider implements MessageProvider {

    private final String key;
    private final Channel channel;
    private final FailureInjectionState failureInjection;
    private final int baseLatencyMs;

    protected AbstractFakeProvider(String key, Channel channel, FailureInjectionState failureInjection, int baseLatencyMs) {
        this.key = key;
        this.channel = channel;
        this.failureInjection = failureInjection;
        this.baseLatencyMs = baseLatencyMs;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public ProviderSendResult send(ProviderSendRequest request) {
        long extra = failureInjection.slowMsFor(key);
        long sleepMs = baseLatencyMs + extra + ThreadLocalRandom.current().nextInt(0, 15);
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ProviderSendResult.fail("interrupted");
        }

        if (failureInjection.shouldFail(key)) {
            return ProviderSendResult.fail(key + " injected failure");
        }

        String providerId = key.toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        return ProviderSendResult.ok(providerId);
    }
}
