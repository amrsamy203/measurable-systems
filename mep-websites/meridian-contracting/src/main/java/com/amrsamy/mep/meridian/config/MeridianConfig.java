package com.amrsamy.mep.meridian.config;

import com.amrsamy.mep.common.inquiry.ReferenceGenerator;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeridianConfig {

    @Bean
    public ReferenceGenerator quoteReferences(Clock clock) {
        return new ReferenceGenerator("MER-RFQ", clock);
    }
}
