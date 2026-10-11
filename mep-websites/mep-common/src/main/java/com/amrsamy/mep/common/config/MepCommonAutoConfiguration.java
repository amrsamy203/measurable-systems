package com.amrsamy.mep.common.config;

import com.amrsamy.mep.common.CompanyProfile;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.common.inquiry.LoggingInquiryNotifier;
import com.amrsamy.mep.common.web.SiteModelAdvice;
import java.time.Clock;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/** Wires the shared pieces into any site that has mep-common on its classpath. */
@AutoConfiguration
@EnableConfigurationProperties(CompanyProfile.class)
public class MepCommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @ConditionalOnMissingBean
    public InquiryNotifier inquiryNotifier() {
        return new LoggingInquiryNotifier();
    }

    @Bean
    public SiteModelAdvice siteModelAdvice(CompanyProfile company, Clock clock) {
        return new SiteModelAdvice(company, clock);
    }
}
