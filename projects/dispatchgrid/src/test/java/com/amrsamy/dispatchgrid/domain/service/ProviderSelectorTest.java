package com.amrsamy.dispatchgrid.domain.service;

import com.amrsamy.dispatchgrid.domain.model.Channel;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProviderSelectorTest {

    @Test
    void selectsConfiguredProviderPerChannel() {
        Map<Channel, String> defaults = new EnumMap<>(Channel.class);
        defaults.put(Channel.SMS, "FAKE_SMS");
        defaults.put(Channel.EMAIL, "FAKE_EMAIL");
        defaults.put(Channel.WEBHOOK, "FAKE_WEBHOOK");

        ProviderSelector selector = new ProviderSelector(defaults);

        assertEquals("FAKE_SMS", selector.select(Channel.SMS));
        assertEquals("FAKE_EMAIL", selector.select(Channel.EMAIL));
        assertEquals("FAKE_WEBHOOK", selector.select(Channel.WEBHOOK));
    }

    @Test
    void requiresAllChannels() {
        Map<Channel, String> incomplete = new EnumMap<>(Channel.class);
        incomplete.put(Channel.SMS, "FAKE_SMS");
        assertThrows(IllegalArgumentException.class, () -> new ProviderSelector(incomplete));
    }
}
