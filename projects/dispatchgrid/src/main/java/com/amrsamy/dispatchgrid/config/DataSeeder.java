package com.amrsamy.dispatchgrid.config;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.ApiKeyEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.TenantEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.UserEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.ApiKeyRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.TenantRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.UserRepository;
import com.amrsamy.dispatchgrid.domain.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
public class DataSeeder {

    public static final String DEMO_API_KEY = "dg_demo_sk_live_7f3a9c2e8b1d4f6a";

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDemoData(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            ApiKeyRepository apiKeyRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (tenantRepository.findByName("Demo Messaging").isPresent()) {
                log.info("Demo data already present — skipping seed");
                return;
            }

            TenantEntity tenant = new TenantEntity();
            tenant.setName("Demo Messaging");
            tenant.setDailyQuota(500_000);
            tenant.setUsedToday(0);
            tenant = tenantRepository.save(tenant);

            String hash = passwordEncoder.encode("password");

            UserEntity admin = new UserEntity();
            admin.setTenantId(tenant.getId());
            admin.setEmail("admin@dispatchgrid.demo");
            admin.setDisplayName("Dispatch Admin");
            admin.setRole(Role.ADMIN);
            admin.setPasswordHash(hash);
            admin.setActive(true);
            userRepository.save(admin);

            UserEntity operator = new UserEntity();
            operator.setTenantId(tenant.getId());
            operator.setEmail("operator@dispatchgrid.demo");
            operator.setDisplayName("Dispatch Operator");
            operator.setRole(Role.OPERATOR);
            operator.setPasswordHash(hash);
            operator.setActive(true);
            userRepository.save(operator);

            ApiKeyEntity apiKey = new ApiKeyEntity();
            apiKey.setTenantId(tenant.getId());
            apiKey.setKeyValue(DEMO_API_KEY);
            apiKey.setLabel("Demo machine key");
            apiKey.setActive(true);
            apiKey.setCreatedAt(Instant.now());
            apiKeyRepository.save(apiKey);

            log.info("Seeded Demo Messaging tenant id={}", tenant.getId());
            log.info("Login: admin@dispatchgrid.demo / password");
            log.info("API key (X-Api-Key): {}", DEMO_API_KEY);
        };
    }
}
