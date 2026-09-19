package com.amrsamy.caseflow.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundRobinSelectorTest {

    private RoundRobinSelector selector;

    @BeforeEach
    void setUp() {
        selector = new RoundRobinSelector();
    }

    @Test
    void picksAgentWithOldestAssignment() {
        Instant now = Instant.parse("2026-01-01T12:00:00Z");
        List<RoundRobinSelector.AgentCandidate> agents = List.of(
                new RoundRobinSelector.AgentCandidate(1L, "A", true, now.minusSeconds(10), Set.of("fraud")),
                new RoundRobinSelector.AgentCandidate(2L, "B", true, now.minusSeconds(100), Set.of("fraud")),
                new RoundRobinSelector.AgentCandidate(3L, "C", true, now, Set.of("fraud"))
        );

        Optional<RoundRobinSelector.AgentCandidate> selected =
                selector.select(agents, Set.of("fraud"));

        assertTrue(selected.isPresent());
        assertEquals(2L, selected.get().id());
    }

    @Test
    void skipsInactiveAgents() {
        List<RoundRobinSelector.AgentCandidate> agents = List.of(
                new RoundRobinSelector.AgentCandidate(1L, "A", false, Instant.EPOCH, Set.of("aml")),
                new RoundRobinSelector.AgentCandidate(2L, "B", true, Instant.EPOCH.plusSeconds(5), Set.of("aml"))
        );

        Optional<RoundRobinSelector.AgentCandidate> selected =
                selector.select(agents, Set.of("aml"));

        assertTrue(selected.isPresent());
        assertEquals(2L, selected.get().id());
    }

    @Test
    void prefersNeverAssignedOverRecentlyAssigned() {
        Instant now = Instant.now();
        List<RoundRobinSelector.AgentCandidate> agents = List.of(
                new RoundRobinSelector.AgentCandidate(10L, "Recent", true, now, Set.of()),
                new RoundRobinSelector.AgentCandidate(20L, "Fresh", true, null, Set.of())
        );

        Optional<RoundRobinSelector.AgentCandidate> selected =
                selector.select(agents, Set.of());

        assertTrue(selected.isPresent());
        assertEquals(20L, selected.get().id());
    }

    @Test
    void requiresAllSkillsWhenSpecified() {
        List<RoundRobinSelector.AgentCandidate> agents = List.of(
                new RoundRobinSelector.AgentCandidate(1L, "Partial", true, Instant.EPOCH, Set.of("fraud")),
                new RoundRobinSelector.AgentCandidate(2L, "Full", true, Instant.EPOCH.plusSeconds(1), Set.of("fraud", "aml"))
        );

        Optional<RoundRobinSelector.AgentCandidate> selected =
                selector.select(agents, Set.of("fraud", "aml"));

        assertTrue(selected.isPresent());
        assertEquals(2L, selected.get().id());
    }

    @Test
    void returnsEmptyWhenNoEligibleAgents() {
        List<RoundRobinSelector.AgentCandidate> agents = List.of(
                new RoundRobinSelector.AgentCandidate(1L, "X", false, null, Set.of("kyc"))
        );
        assertTrue(selector.select(agents, Set.of("kyc")).isEmpty());
    }
}
