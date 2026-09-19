package com.amrsamy.caseflow.application.service;

import com.amrsamy.caseflow.adapters.persistence.entity.AuditEventEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.CaseEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.RoutingRuleEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.UserEntity;
import com.amrsamy.caseflow.adapters.persistence.repo.AuditEventRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.CaseRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.RoutingRuleRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.UserRepository;
import com.amrsamy.caseflow.adapters.metrics.FailureInjectionState;
import com.amrsamy.caseflow.adapters.metrics.ThroughputMetricsCollector;
import com.amrsamy.caseflow.domain.model.CaseStatus;
import com.amrsamy.caseflow.domain.model.Priority;
import com.amrsamy.caseflow.domain.model.Role;
import com.amrsamy.caseflow.domain.service.RoundRobinSelector;
import com.amrsamy.caseflow.domain.service.RoutingMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CasePipelineService {

    private static final Logger log = LoggerFactory.getLogger(CasePipelineService.class);
    private static final String DEFAULT_QUEUE = "general";

    private final CaseRepository caseRepository;
    private final RoutingRuleRepository routingRuleRepository;
    private final UserRepository userRepository;
    private final AuditEventRepository auditEventRepository;
    private final RoutingMatcher routingMatcher;
    private final RoundRobinSelector roundRobinSelector;
    private final ThroughputMetricsCollector metricsCollector;
    private final FailureInjectionState failureInjectionState;
    private final ObjectMapper objectMapper;

    public CasePipelineService(
            CaseRepository caseRepository,
            RoutingRuleRepository routingRuleRepository,
            UserRepository userRepository,
            AuditEventRepository auditEventRepository,
            ThroughputMetricsCollector metricsCollector,
            FailureInjectionState failureInjectionState,
            ObjectMapper objectMapper) {
        this.caseRepository = caseRepository;
        this.routingRuleRepository = routingRuleRepository;
        this.userRepository = userRepository;
        this.auditEventRepository = auditEventRepository;
        this.routingMatcher = new RoutingMatcher();
        this.roundRobinSelector = new RoundRobinSelector();
        this.metricsCollector = metricsCollector;
        this.failureInjectionState = failureInjectionState;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void routeCase(Long caseId) {
        long start = System.nanoTime();
        applyFailureInjection();

        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        if (caseEntity.getStatus() != CaseStatus.NEW && caseEntity.getStatus() != CaseStatus.QUEUED) {
            log.debug("Skipping route for case {} in status {}", caseId, caseEntity.getStatus());
            return;
        }

        List<RoutingRuleEntity> rules = routingRuleRepository.findByTenantIdAndActiveTrue(caseEntity.getTenantId());
        List<RoutingMatcher.RuleCandidate> candidates = rules.stream()
                .map(r -> new RoutingMatcher.RuleCandidate(
                        r.getId(),
                        r.getMatchPriority(),
                        r.getMatchSkill(),
                        r.getTargetQueue(),
                        r.getPriorityWeight()))
                .toList();

        String queue = routingMatcher.match(caseEntity.getPriority(), caseEntity.getSkills(), candidates)
                .map(RoutingMatcher.RuleCandidate::targetQueue)
                .orElse(DEFAULT_QUEUE);

        caseEntity.setQueueName(queue);
        caseEntity.setStatus(CaseStatus.QUEUED);
        caseEntity.setSlaDeadline(computeSla(caseEntity.getPriority()));
        caseEntity.setUpdatedAt(Instant.now());
        caseRepository.save(caseEntity);

        audit(caseEntity.getId(), null, "ROUTED", Map.of(
                "queue", queue,
                "priority", caseEntity.getPriority().name()));

        metricsCollector.recordSuccess(Duration.ofNanos(System.nanoTime() - start).toMillis());
        log.info("Routed case {} to queue {}", caseId, queue);
    }

    @Transactional
    public void assignCase(Long caseId) {
        long start = System.nanoTime();
        applyFailureInjection();

        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        if (caseEntity.getStatus() != CaseStatus.QUEUED && caseEntity.getStatus() != CaseStatus.NEW) {
            log.debug("Skipping assign for case {} in status {}", caseId, caseEntity.getStatus());
            return;
        }

        if (caseEntity.getQueueName() == null) {
            routeCase(caseId);
            caseEntity = caseRepository.findById(caseId).orElseThrow();
        }

        List<UserEntity> agents = userRepository.findByTenantIdAndRoleAndActiveTrue(
                caseEntity.getTenantId(), Role.AGENT);

        // Prefer agents whose skills overlap case skills; fall back to any active agent
        Set<String> preferredSkills = caseEntity.getSkills() == null ? Set.of() : caseEntity.getSkills();

        List<RoundRobinSelector.AgentCandidate> candidates = agents.stream()
                .map(a -> new RoundRobinSelector.AgentCandidate(
                        a.getId(),
                        a.getDisplayName(),
                        a.isActive(),
                        a.getLastAssignedAt(),
                        a.getSkills()))
                .toList();

        var selected = roundRobinSelector.select(candidates, preferredSkills)
                .or(() -> roundRobinSelector.select(candidates, Set.of()));

        if (selected.isEmpty()) {
            log.warn("No agents available for case {}; leaving QUEUED", caseId);
            metricsCollector.recordSuccess(Duration.ofNanos(System.nanoTime() - start).toMillis());
            return;
        }

        RoundRobinSelector.AgentCandidate agent = selected.get();
        UserEntity agentEntity = userRepository.findById(agent.id()).orElseThrow();

        caseEntity.setAssigneeId(agent.id());
        caseEntity.setStatus(CaseStatus.ASSIGNED);
        caseEntity.setUpdatedAt(Instant.now());
        caseRepository.save(caseEntity);

        agentEntity.setLastAssignedAt(Instant.now());
        userRepository.save(agentEntity);

        audit(caseEntity.getId(), agent.id(), "ASSIGNED", Map.of(
                "assigneeId", agent.id(),
                "assignee", agent.displayName(),
                "queue", caseEntity.getQueueName()));

        metricsCollector.recordSuccess(Duration.ofNanos(System.nanoTime() - start).toMillis());
        log.info("Assigned case {} to agent {}", caseId, agent.displayName());
    }

    private Instant computeSla(Priority priority) {
        Duration sla = switch (priority) {
            case CRITICAL -> Duration.ofMinutes(30);
            case HIGH -> Duration.ofHours(2);
            case MEDIUM -> Duration.ofHours(8);
            case LOW -> Duration.ofHours(24);
        };
        return Instant.now().plus(sla);
    }

    private void applyFailureInjection() {
        long slowMs = failureInjectionState.getSlowProcessingMs();
        if (slowMs > 0) {
            try {
                Thread.sleep(slowMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int failurePercent = failureInjectionState.getFailurePercent();
        if (failurePercent > 0 && ThreadLocalRandom.current().nextInt(100) < failurePercent) {
            metricsCollector.recordFailure();
            throw new IllegalStateException("Injected processing failure (" + failurePercent + "%)");
        }
    }

    private void audit(Long caseId, Long actorId, String action, Map<String, Object> payload) {
        AuditEventEntity event = new AuditEventEntity();
        event.setCaseId(caseId);
        event.setActorId(actorId);
        event.setAction(action);
        try {
            event.setPayloadJson(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            event.setPayloadJson(payload.toString());
        }
        auditEventRepository.save(event);
    }

    public Map<String, Long> queueDepths(Long tenantId) {
        List<CaseStatus> open = List.of(CaseStatus.NEW, CaseStatus.QUEUED, CaseStatus.ASSIGNED, CaseStatus.IN_PROGRESS);
        List<Object[]> rows = caseRepository.countByQueueGrouped(tenantId, open);
        return rows.stream().collect(Collectors.toMap(
                r -> (String) r[0],
                r -> (Long) r[1],
                Long::sum,
                HashMap::new));
    }
}
