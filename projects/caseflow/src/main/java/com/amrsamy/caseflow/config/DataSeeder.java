package com.amrsamy.caseflow.config;

import com.amrsamy.caseflow.adapters.persistence.entity.RoutingRuleEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.TenantEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.UserEntity;
import com.amrsamy.caseflow.adapters.persistence.repo.RoutingRuleRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.TenantRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.UserRepository;
import com.amrsamy.caseflow.domain.model.Priority;
import com.amrsamy.caseflow.domain.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDemoData(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            RoutingRuleRepository routingRuleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (tenantRepository.findByName("Demo Ops").isPresent()) {
                log.info("Demo data already present — skipping seed");
                return;
            }

            TenantEntity tenant = new TenantEntity();
            tenant.setName("Demo Ops");
            tenant = tenantRepository.save(tenant);
            Long tenantId = tenant.getId();

            String hash = passwordEncoder.encode("password");

            seedUser(userRepository, tenantId, "admin@caseflow.demo", "Admin User", Role.ADMIN, hash, Set.of());
            seedUser(userRepository, tenantId, "supervisor@caseflow.demo", "Supervisor User", Role.SUPERVISOR, hash, Set.of("fraud", "aml"));
            seedUser(userRepository, tenantId, "agent1@caseflow.demo", "Agent One", Role.AGENT, hash, Set.of("fraud", "payments"));
            seedUser(userRepository, tenantId, "agent2@caseflow.demo", "Agent Two", Role.AGENT, hash, Set.of("aml", "kyc"));
            seedUser(userRepository, tenantId, "agent3@caseflow.demo", "Agent Three", Role.AGENT, hash, Set.of("kyc", "fraud"));

            seedRule(routingRuleRepository, tenantId, Priority.CRITICAL, null, "critical-response", 100);
            seedRule(routingRuleRepository, tenantId, Priority.HIGH, "fraud", "fraud-high", 80);
            seedRule(routingRuleRepository, tenantId, null, "aml", "aml-review", 70);
            seedRule(routingRuleRepository, tenantId, null, "kyc", "kyc-queue", 60);
            seedRule(routingRuleRepository, tenantId, Priority.LOW, null, "general", 10);
            seedRule(routingRuleRepository, tenantId, null, null, "general", 1);

            log.info("Seeded Demo Ops tenant id={}, users=5, routing rules=6", tenantId);
            log.info("Login: admin@caseflow.demo / password (also agent1@, supervisor@)");
        };
    }

    private void seedUser(
            UserRepository repo,
            Long tenantId,
            String email,
            String displayName,
            Role role,
            String passwordHash,
            Set<String> skills) {
        UserEntity user = new UserEntity();
        user.setTenantId(tenantId);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setPasswordHash(passwordHash);
        user.setActive(true);
        user.setSkills(skills);
        repo.save(user);
    }

    private void seedRule(
            RoutingRuleRepository repo,
            Long tenantId,
            Priority matchPriority,
            String matchSkill,
            String targetQueue,
            int weight) {
        RoutingRuleEntity rule = new RoutingRuleEntity();
        rule.setTenantId(tenantId);
        rule.setMatchPriority(matchPriority);
        rule.setMatchSkill(matchSkill);
        rule.setTargetQueue(targetQueue);
        rule.setPriorityWeight(weight);
        rule.setActive(true);
        repo.save(rule);
    }
}
