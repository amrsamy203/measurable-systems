package com.amrsamy.relateai.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphInsightServiceTest {

    private GraphInsightService service;

    @BeforeEach
    void setUp() {
        service = new GraphInsightService();
    }

    @Test
    void topConnectedUsers_ranksByDegree() {
        List<GraphInsightService.FollowEdge> follows = List.of(
                new GraphInsightService.FollowEdge(1, 2),
                new GraphInsightService.FollowEdge(1, 3),
                new GraphInsightService.FollowEdge(2, 1),
                new GraphInsightService.FollowEdge(3, 1),
                new GraphInsightService.FollowEdge(4, 2)
        );

        List<GraphInsightService.ScoredId> ranked = service.topConnectedUsers(follows, 3);

        assertEquals(1L, ranked.get(0).id());
        assertEquals(4.0, ranked.get(0).score());
        assertEquals(2L, ranked.get(1).id());
        assertEquals(3.0, ranked.get(1).score());
    }

    @Test
    void hottestTopics_sumsWeights() {
        List<GraphInsightService.TopicActivity> activities = List.of(
                new GraphInsightService.TopicActivity(10, 2),
                new GraphInsightService.TopicActivity(20, 5),
                new GraphInsightService.TopicActivity(10, 3),
                new GraphInsightService.TopicActivity(30, 1)
        );

        List<GraphInsightService.ScoredId> ranked = service.hottestTopics(activities, 2);

        assertEquals(10L, ranked.get(0).id());
        assertEquals(5.0, ranked.get(0).score());
        assertEquals(20L, ranked.get(1).id());
        assertEquals(5.0, ranked.get(1).score());
    }

    @Test
    void recommendBySharedTopics_excludesSelfAndAlreadyFollowing() {
        List<GraphInsightService.UserTopicEdge> memberships = List.of(
                new GraphInsightService.UserTopicEdge(1, 100),
                new GraphInsightService.UserTopicEdge(1, 200),
                new GraphInsightService.UserTopicEdge(2, 100),
                new GraphInsightService.UserTopicEdge(2, 200),
                new GraphInsightService.UserTopicEdge(3, 100),
                new GraphInsightService.UserTopicEdge(4, 300)
        );

        List<GraphInsightService.ScoredId> ranked = service.recommendBySharedTopics(
                1L,
                memberships,
                Set.of(2L),
                5);

        assertEquals(1, ranked.size());
        assertEquals(3L, ranked.get(0).id());
        assertEquals(1.0, ranked.get(0).score());
        assertTrue(ranked.stream().noneMatch(s -> s.id() == 2L));
        assertTrue(ranked.stream().noneMatch(s -> s.id() == 4L));
    }

    @Test
    void recommendBySharedTopics_returnsEmptyWhenUserHasNoTopics() {
        List<GraphInsightService.UserTopicEdge> memberships = List.of(
                new GraphInsightService.UserTopicEdge(2, 100)
        );
        List<GraphInsightService.ScoredId> ranked =
                service.recommendBySharedTopics(1L, memberships, Set.of(), 5);
        assertTrue(ranked.isEmpty());
    }
}
