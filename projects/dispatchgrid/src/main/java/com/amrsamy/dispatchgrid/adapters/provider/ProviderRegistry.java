package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.domain.model.Channel;
import com.amrsamy.dispatchgrid.domain.service.ProviderSelector;
import com.amrsamy.dispatchgrid.domain.service.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProviderRegistry {

    private final Map<String, MessageProvider> byKey;
    private final ProviderSelector selector;
    private final Map<String, TokenBucketRateLimiter> limiters = new ConcurrentHashMap<>();

    public ProviderRegistry(
            List<MessageProvider> providers,
            ProviderSelector selector,
            @Value("${dispatchgrid.providers.sms.tokens-per-second:80}") double smsTps,
            @Value("${dispatchgrid.providers.sms.burst:40}") double smsBurst,
            @Value("${dispatchgrid.providers.email.tokens-per-second:120}") double emailTps,
            @Value("${dispatchgrid.providers.email.burst:60}") double emailBurst,
            @Value("${dispatchgrid.providers.webhook.tokens-per-second:100}") double webhookTps,
            @Value("${dispatchgrid.providers.webhook.burst:50}") double webhookBurst) {
        this.byKey = providers.stream().collect(Collectors.toMap(MessageProvider::key, Function.identity()));
        this.selector = selector;
        limiters.put(FakeSmsProvider.KEY, new TokenBucketRateLimiter(smsTps, smsBurst));
        limiters.put(FakeEmailProvider.KEY, new TokenBucketRateLimiter(emailTps, emailBurst));
        limiters.put(FakeWebhookProvider.KEY, new TokenBucketRateLimiter(webhookTps, webhookBurst));
    }

    public MessageProvider resolve(Channel channel) {
        String key = selector.select(channel);
        MessageProvider provider = byKey.get(key);
        if (provider == null) {
            throw new IllegalStateException("No provider bean for key " + key);
        }
        return provider;
    }

    public MessageProvider byKey(String key) {
        MessageProvider provider = byKey.get(key);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider " + key);
        }
        return provider;
    }

    public TokenBucketRateLimiter limiterFor(String providerKey) {
        return limiters.computeIfAbsent(providerKey, k -> new TokenBucketRateLimiter(50, 25));
    }

    public Map<String, String> providerKeysByChannel() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Channel channel : Channel.values()) {
            map.put(channel.name(), selector.select(channel));
        }
        return map;
    }
}
