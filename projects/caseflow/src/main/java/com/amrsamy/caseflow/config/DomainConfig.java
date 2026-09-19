package com.amrsamy.caseflow.config;

import com.amrsamy.caseflow.domain.service.RoundRobinSelector;
import com.amrsamy.caseflow.domain.service.RoutingMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public RoutingMatcher routingMatcher() {
        return new RoutingMatcher();
    }

    @Bean
    public RoundRobinSelector roundRobinSelector() {
        return new RoundRobinSelector();
    }
}
