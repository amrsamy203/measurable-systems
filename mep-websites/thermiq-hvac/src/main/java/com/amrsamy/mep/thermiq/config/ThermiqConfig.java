package com.amrsamy.mep.thermiq.config;

import com.amrsamy.mep.common.inquiry.ReferenceGenerator;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({BookingPolicy.class, PlanCatalog.class})
public class ThermiqConfig {

    /** Contact messages only; bookings take their reference from the database id. */
    @Bean
    ReferenceGenerator messageReferences(Clock clock) {
        return new ReferenceGenerator("TQ-MSG", clock);
    }
}
