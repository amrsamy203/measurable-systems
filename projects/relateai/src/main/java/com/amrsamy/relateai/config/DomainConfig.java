package com.amrsamy.relateai.config;

import com.amrsamy.relateai.adapters.ai.MockAiClient;
import com.amrsamy.relateai.adapters.ai.OpenAiClient;
import com.amrsamy.relateai.application.port.AiClientPort;
import com.amrsamy.relateai.domain.service.GraphInsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DomainConfig {

    @Bean
    public GraphInsightService graphInsightService() {
        return new GraphInsightService();
    }

    /**
     * Prefer OpenAI when the bean is present (enabled + key); otherwise use mock.
     */
    @Bean
    @Primary
    public AiClientPort aiClientPort(MockAiClient mockAiClient, @Autowired(required = false) OpenAiClient openAiClient) {
        return openAiClient != null ? openAiClient : mockAiClient;
    }
}
