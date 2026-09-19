package com.amrsamy.caseflow.domain.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Fair round-robin assignment: prefers the eligible agent with the oldest lastAssignedAt.
 * Ties break on agent id for determinism.
 */
public class RoundRobinSelector {

    public record AgentCandidate(
            Long id,
            String displayName,
            boolean active,
            Instant lastAssignedAt,
            java.util.Set<String> skills
    ) {
    }

    public Optional<AgentCandidate> select(List<AgentCandidate> agents, java.util.Set<String> requiredSkills) {
        if (agents == null || agents.isEmpty()) {
            return Optional.empty();
        }

        java.util.Set<String> required = requiredSkills == null ? java.util.Set.of() : requiredSkills;

        return agents.stream()
                .filter(AgentCandidate::active)
                .filter(a -> hasRequiredSkills(a, required))
                .min(Comparator
                        .comparing((AgentCandidate a) -> a.lastAssignedAt() == null
                                ? Instant.EPOCH
                                : a.lastAssignedAt())
                        .thenComparing(AgentCandidate::id));
    }

    private boolean hasRequiredSkills(AgentCandidate agent, java.util.Set<String> required) {
        if (required.isEmpty()) {
            return true;
        }
        if (agent.skills() == null || agent.skills().isEmpty()) {
            return false;
        }
        return required.stream().allMatch(req ->
                agent.skills().stream().anyMatch(s -> s.equalsIgnoreCase(req)));
    }
}
