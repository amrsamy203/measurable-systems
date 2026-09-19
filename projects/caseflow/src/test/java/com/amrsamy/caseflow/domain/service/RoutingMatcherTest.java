package com.amrsamy.caseflow.domain.service;

import com.amrsamy.caseflow.domain.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoutingMatcherTest {

    private RoutingMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new RoutingMatcher();
    }

    @Test
    void selectsHighestWeightMatchingRule() {
        List<RoutingMatcher.RuleCandidate> rules = List.of(
                new RoutingMatcher.RuleCandidate(1L, null, null, "general", 1),
                new RoutingMatcher.RuleCandidate(2L, Priority.HIGH, "fraud", "fraud-high", 80),
                new RoutingMatcher.RuleCandidate(3L, null, "fraud", "fraud-any", 40)
        );

        Optional<RoutingMatcher.RuleCandidate> match =
                matcher.match(Priority.HIGH, Set.of("fraud"), rules);

        assertTrue(match.isPresent());
        assertEquals("fraud-high", match.get().targetQueue());
    }

    @Test
    void fallsBackToGenericWhenSkillDoesNotMatch() {
        List<RoutingMatcher.RuleCandidate> rules = List.of(
                new RoutingMatcher.RuleCandidate(1L, null, "aml", "aml-review", 70),
                new RoutingMatcher.RuleCandidate(2L, null, null, "general", 1)
        );

        Optional<RoutingMatcher.RuleCandidate> match =
                matcher.match(Priority.MEDIUM, Set.of("payments"), rules);

        assertTrue(match.isPresent());
        assertEquals("general", match.get().targetQueue());
    }

    @Test
    void criticalPriorityBeatsLowerWeightSkillRule() {
        List<RoutingMatcher.RuleCandidate> rules = List.of(
                new RoutingMatcher.RuleCandidate(1L, Priority.CRITICAL, null, "critical-response", 100),
                new RoutingMatcher.RuleCandidate(2L, null, "kyc", "kyc-queue", 60)
        );

        Optional<RoutingMatcher.RuleCandidate> match =
                matcher.match(Priority.CRITICAL, Set.of("kyc"), rules);

        assertTrue(match.isPresent());
        assertEquals("critical-response", match.get().targetQueue());
    }

    @Test
    void returnsEmptyWhenNoRules() {
        assertTrue(matcher.match(Priority.LOW, Set.of(), List.of()).isEmpty());
    }
}
