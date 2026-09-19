package com.amrsamy.dispatchgrid.domain.service;

import com.amrsamy.dispatchgrid.domain.model.Channel;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Selects a provider key for a channel. Demo uses a single fake provider per channel;
 * extension point for multi-provider weighted routing.
 */
public class ProviderSelector {

    private final Map<Channel, String> defaults;

    public ProviderSelector(Map<Channel, String> defaults) {
        this.defaults = new EnumMap<>(Channel.class);
        this.defaults.putAll(Objects.requireNonNull(defaults, "defaults"));
        for (Channel channel : Channel.values()) {
            if (!this.defaults.containsKey(channel)) {
                throw new IllegalArgumentException("Missing default provider for " + channel);
            }
        }
    }

    public String select(Channel channel) {
        String provider = defaults.get(channel);
        if (provider == null) {
            throw new IllegalArgumentException("No provider for channel " + channel);
        }
        return provider;
    }

    public Map<Channel, String> defaults() {
        return Map.copyOf(defaults);
    }
}
