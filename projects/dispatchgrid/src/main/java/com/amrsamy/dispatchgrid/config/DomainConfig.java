package com.amrsamy.dispatchgrid.config;

import com.amrsamy.dispatchgrid.adapters.provider.FakeEmailProvider;
import com.amrsamy.dispatchgrid.adapters.provider.FakeSmsProvider;
import com.amrsamy.dispatchgrid.adapters.provider.FakeWebhookProvider;
import com.amrsamy.dispatchgrid.domain.model.Channel;
import com.amrsamy.dispatchgrid.domain.service.ProviderSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class DomainConfig {

    @Bean
    public ProviderSelector providerSelector() {
        Map<Channel, String> defaults = new EnumMap<>(Channel.class);
        defaults.put(Channel.SMS, FakeSmsProvider.KEY);
        defaults.put(Channel.EMAIL, FakeEmailProvider.KEY);
        defaults.put(Channel.WEBHOOK, FakeWebhookProvider.KEY);
        return new ProviderSelector(defaults);
    }
}
