package com.amrsamy.relateai.domain.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pure graph ranking / recommendation logic over relational edge collections.
 * Unit-tested without Spring or a database.
 */
public class GraphInsightService {

    public record FollowEdge(long followerId, long followeeId) {}

    public record UserTopicEdge(long userId, long topicId) {}

    public record TopicActivity(long topicId, int weight) {}

    public record ScoredId(long id, double score) {}

    public List<ScoredId> topConnectedUsers(List<FollowEdge> follows, int limit) {
        Map<Long, Double> degree = new HashMap<>();
        for (FollowEdge edge : follows) {
            degree.merge(edge.followerId(), 1.0, Double::sum);
            degree.merge(edge.followeeId(), 1.0, Double::sum);
        }
        return rank(degree, limit);
    }

    public List<ScoredId> hottestTopics(List<TopicActivity> activities, int limit) {
        Map<Long, Double> scores = new HashMap<>();
        for (TopicActivity activity : activities) {
            scores.merge(activity.topicId(), (double) activity.weight(), Double::sum);
        }
        return rank(scores, limit);
    }

    /**
     * Recommend users who share the most topics with {@code userId}, excluding self and already-followed users.
     */
    public List<ScoredId> recommendBySharedTopics(
            long userId,
            List<UserTopicEdge> memberships,
            Set<Long> alreadyFollowing,
            int limit) {

        Set<Long> myTopics = memberships.stream()
                .filter(m -> m.userId() == userId)
                .map(UserTopicEdge::topicId)
                .collect(Collectors.toSet());

        if (myTopics.isEmpty()) {
            return List.of();
        }

        Map<Long, Set<Long>> topicsByUser = new HashMap<>();
        for (UserTopicEdge edge : memberships) {
            if (edge.userId() == userId) {
                continue;
            }
            topicsByUser.computeIfAbsent(edge.userId(), id -> new HashSet<>()).add(edge.topicId());
        }

        Map<Long, Double> scores = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : topicsByUser.entrySet()) {
            long candidateId = entry.getKey();
            if (alreadyFollowing.contains(candidateId)) {
                continue;
            }
            Set<Long> shared = new HashSet<>(entry.getValue());
            shared.retainAll(myTopics);
            if (!shared.isEmpty()) {
                scores.put(candidateId, (double) shared.size());
            }
        }
        return rank(scores, limit);
    }

    private List<ScoredId> rank(Map<Long, Double> scores, int limit) {
        int safeLimit = Math.max(0, limit);
        List<ScoredId> ranked = new ArrayList<>();
        scores.entrySet().stream()
                .sorted(Comparator
                        .<Map.Entry<Long, Double>>comparingDouble(Map.Entry::getValue).reversed()
                        .thenComparingLong(Map.Entry::getKey))
                .limit(safeLimit)
                .forEach(e -> ranked.add(new ScoredId(e.getKey(), e.getValue())));
        return ranked;
    }

    public static Set<Long> followingSet(long userId, List<FollowEdge> follows) {
        Set<Long> result = new HashSet<>();
        for (FollowEdge edge : follows) {
            if (Objects.equals(edge.followerId(), userId)) {
                result.add(edge.followeeId());
            }
        }
        return result;
    }
}
