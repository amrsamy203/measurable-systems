package com.amrsamy.relateai.application.service;

import com.amrsamy.relateai.adapters.metrics.MetricsCollector;
import com.amrsamy.relateai.adapters.persistence.entity.FollowEntity;
import com.amrsamy.relateai.adapters.persistence.entity.InteractionEntity;
import com.amrsamy.relateai.adapters.persistence.entity.TopicEntity;
import com.amrsamy.relateai.adapters.persistence.entity.UserEntity;
import com.amrsamy.relateai.adapters.persistence.entity.UserTopicEntity;
import com.amrsamy.relateai.adapters.persistence.repo.FollowRepository;
import com.amrsamy.relateai.adapters.persistence.repo.InteractionRepository;
import com.amrsamy.relateai.adapters.persistence.repo.TopicRepository;
import com.amrsamy.relateai.adapters.persistence.repo.UserRepository;
import com.amrsamy.relateai.adapters.persistence.repo.UserTopicRepository;
import com.amrsamy.relateai.domain.model.InteractionType;
import com.amrsamy.relateai.domain.service.GraphInsightService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SocialGraphService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final TopicRepository topicRepository;
    private final UserTopicRepository userTopicRepository;
    private final InteractionRepository interactionRepository;
    private final GraphInsightService graphInsightService;
    private final MetricsCollector metrics;

    public SocialGraphService(
            UserRepository userRepository,
            FollowRepository followRepository,
            TopicRepository topicRepository,
            UserTopicRepository userTopicRepository,
            InteractionRepository interactionRepository,
            GraphInsightService graphInsightService,
            MetricsCollector metrics) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.topicRepository = topicRepository;
        this.userTopicRepository = userTopicRepository;
        this.interactionRepository = interactionRepository;
        this.graphInsightService = graphInsightService;
        this.metrics = metrics;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public FollowEntity follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
        }
        ensureUser(followeeId);
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseGet(() -> {
                    FollowEntity edge = new FollowEntity();
                    edge.setFollowerId(followerId);
                    edge.setFolloweeId(followeeId);
                    FollowEntity saved = followRepository.save(edge);

                    InteractionEntity interaction = new InteractionEntity();
                    interaction.setActorId(followerId);
                    interaction.setTargetUserId(followeeId);
                    interaction.setType(InteractionType.FOLLOW);
                    interactionRepository.save(interaction);

                    metrics.incFollows();
                    metrics.incInteractions();
                    return saved;
                });
    }

    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .ifPresent(followRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<TopicEntity> listTopics() {
        return topicRepository.findAll();
    }

    @Transactional
    public UserTopicEntity joinTopic(Long userId, Long topicId) {
        ensureUser(userId);
        TopicEntity topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));

        return userTopicRepository.findByUserIdAndTopicId(userId, topicId)
                .orElseGet(() -> {
                    UserTopicEntity membership = new UserTopicEntity();
                    membership.setUserId(userId);
                    membership.setTopicId(topic.getId());
                    UserTopicEntity saved = userTopicRepository.save(membership);

                    InteractionEntity interaction = new InteractionEntity();
                    interaction.setActorId(userId);
                    interaction.setTopicId(topicId);
                    interaction.setType(InteractionType.JOIN_TOPIC);
                    interactionRepository.save(interaction);

                    metrics.incTopicJoins();
                    metrics.incInteractions();
                    return saved;
                });
    }

    @Transactional
    public InteractionEntity recordInteraction(
            Long actorId,
            InteractionType type,
            Long targetUserId,
            Long topicId,
            String payload) {
        if (type == InteractionType.FOLLOW || type == InteractionType.JOIN_TOPIC
                || type == InteractionType.CHALLENGE_RESPONSE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use dedicated endpoints for FOLLOW, JOIN_TOPIC, and CHALLENGE_RESPONSE");
        }
        if (topicId != null) {
            topicRepository.findById(topicId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        }
        if (targetUserId != null) {
            ensureUser(targetUserId);
        }

        InteractionEntity interaction = new InteractionEntity();
        interaction.setActorId(actorId);
        interaction.setTargetUserId(targetUserId);
        interaction.setTopicId(topicId);
        interaction.setType(type);
        interaction.setPayload(payload);
        InteractionEntity saved = interactionRepository.save(interaction);
        metrics.incInteractions();
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ScoredUser> topConnected(int limit) {
        List<GraphInsightService.FollowEdge> edges = followRepository.findAll().stream()
                .map(f -> new GraphInsightService.FollowEdge(f.getFollowerId(), f.getFolloweeId()))
                .toList();
        List<GraphInsightService.ScoredId> ranked = graphInsightService.topConnectedUsers(edges, limit);
        return enrichUsers(ranked);
    }

    @Transactional(readOnly = true)
    public List<ScoredTopic> hottestTopics(int limit) {
        List<GraphInsightService.TopicActivity> activities = new ArrayList<>();
        for (InteractionEntity interaction : interactionRepository.findByTopicIdIsNotNull()) {
            activities.add(new GraphInsightService.TopicActivity(
                    interaction.getTopicId(),
                    weightFor(interaction.getType())));
        }
        for (UserTopicEntity membership : userTopicRepository.findAll()) {
            activities.add(new GraphInsightService.TopicActivity(membership.getTopicId(), 1));
        }
        List<GraphInsightService.ScoredId> ranked = graphInsightService.hottestTopics(activities, limit);
        Map<Long, TopicEntity> topics = topicRepository.findAll().stream()
                .collect(Collectors.toMap(TopicEntity::getId, t -> t));
        List<ScoredTopic> result = new ArrayList<>();
        for (GraphInsightService.ScoredId scored : ranked) {
            TopicEntity topic = topics.get(scored.id());
            if (topic != null) {
                result.add(new ScoredTopic(topic.getId(), topic.getSlug(), topic.getName(), scored.score()));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<ScoredUser> recommendations(Long userId, int limit) {
        ensureUser(userId);
        List<GraphInsightService.UserTopicEdge> memberships = userTopicRepository.findAll().stream()
                .map(m -> new GraphInsightService.UserTopicEdge(m.getUserId(), m.getTopicId()))
                .toList();
        List<GraphInsightService.FollowEdge> follows = followRepository.findAll().stream()
                .map(f -> new GraphInsightService.FollowEdge(f.getFollowerId(), f.getFolloweeId()))
                .toList();
        Set<Long> alreadyFollowing = GraphInsightService.followingSet(userId, follows);
        List<GraphInsightService.ScoredId> ranked =
                graphInsightService.recommendBySharedTopics(userId, memberships, alreadyFollowing, limit);
        return enrichUsers(ranked);
    }

    private List<ScoredUser> enrichUsers(List<GraphInsightService.ScoredId> ranked) {
        Map<Long, UserEntity> users = new HashMap<>();
        userRepository.findAll().forEach(u -> users.put(u.getId(), u));
        List<ScoredUser> result = new ArrayList<>();
        for (GraphInsightService.ScoredId scored : ranked) {
            UserEntity user = users.get(scored.id());
            if (user != null) {
                result.add(new ScoredUser(user.getId(), user.getEmail(), user.getDisplayName(), scored.score()));
            }
        }
        return result;
    }

    private int weightFor(InteractionType type) {
        return switch (type) {
            case LIKE -> 2;
            case COMMENT -> 3;
            case CHALLENGE_RESPONSE -> 4;
            case JOIN_TOPIC -> 2;
            case FOLLOW -> 1;
        };
    }

    private void ensureUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public record ScoredUser(long id, String email, String displayName, double score) {}

    public record ScoredTopic(long id, String slug, String name, double score) {}
}
