package com.amrsamy.caseflow.domain.service;

import com.amrsamy.caseflow.domain.model.Priority;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Selects the best routing rule for a case based on priority and skill tags.
 * Higher priorityWeight wins; rules with more specific skill matches beat generic ones.
 */
public class RoutingMatcher {

    public record RuleCandidate(
            Long id,
            Priority matchPriority,
            String matchSkill,
            String targetQueue,
            int priorityWeight
    ) {
    }

    public Optional<RuleCandidate> match(Priority casePriority, Set<String> caseSkills, List<RuleCandidate> rules) {
        if (rules == null || rules.isEmpty()) {
            return Optional.empty();
        }

        Set<String> skills = caseSkills == null ? Set.of() : caseSkills;

        return rules.stream()
                .filter(rule -> priorityMatches(rule.matchPriority(), casePriority))
                .filter(rule -> skillMatches(rule.matchSkill(), skills))
                .max(Comparator
                        .comparingInt(RuleCandidate::priorityWeight)
                        .thenComparingInt(r -> specificityScore(r, skills))
                        .thenComparing(RuleCandidate::id, Comparator.nullsLast(Long::compareTo)));
    }

    private boolean priorityMatches(Priority rulePriority, Priority casePriority) {
        return rulePriority == null || rulePriority == casePriority;
    }

    private boolean skillMatches(String matchSkill, Set<String> caseSkills) {
        if (matchSkill == null || matchSkill.isBlank()) {
            return true;
        }
        return caseSkills.stream().anyMatch(s -> s.equalsIgnoreCase(matchSkill.trim()));
    }

    private int specificityScore(RuleCandidate rule, Set<String> caseSkills) {
        int score = 0;
        if (rule.matchPriority() != null) {
            score += 2;
        }
        if (rule.matchSkill() != null && !rule.matchSkill().isBlank()
                && caseSkills.stream().anyMatch(s -> s.equalsIgnoreCase(rule.matchSkill().trim()))) {
            score += 3;
        }
        return score;
    }
}
