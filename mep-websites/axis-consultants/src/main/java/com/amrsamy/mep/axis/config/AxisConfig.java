package com.amrsamy.mep.axis.config;

import com.amrsamy.mep.axis.service.FeeSchedule;
import com.amrsamy.mep.common.inquiry.ReferenceGenerator;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeeSchedule.class)
public class AxisConfig {

    @Bean
    ReferenceGenerator briefReferences(Clock clock) {
        return new ReferenceGenerator("AXS-BRF", clock);
    }
}
