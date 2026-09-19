package com.amrsamy.relateai.config;

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
import com.amrsamy.relateai.domain.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDemoData(
            UserRepository userRepository,
            TopicRepository topicRepository,
            UserTopicRepository userTopicRepository,
            FollowRepository followRepository,
            InteractionRepository interactionRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.existsByEmailIgnoreCase("admin@relateai.demo")) {
                log.info("Demo data already present — skipping seed");
                return;
            }

            String hash = passwordEncoder.encode("password");

            UserEntity admin = saveUser(userRepository, "admin@relateai.demo", "Relate Admin", Role.ADMIN, hash);
            UserEntity user1 = saveUser(userRepository, "user1@relateai.demo", "Ava Rivera", Role.USER, hash);
            UserEntity user2 = saveUser(userRepository, "user2@relateai.demo", "Ben Okonkwo", Role.USER, hash);
            UserEntity user3 = saveUser(userRepository, "user3@relateai.demo", "Cara Nguyen", Role.USER, hash);
            UserEntity user4 = saveUser(userRepository, "user4@relateai.demo", "Diego Santos", Role.USER, hash);

            TopicEntity empathy = saveTopic(topicRepository, "empathy", "Empathy", "Listening and perspective-taking");
            TopicEntity boundaries = saveTopic(topicRepository, "boundaries", "Boundaries", "Healthy limits in relationships");
            TopicEntity community = saveTopic(topicRepository, "community", "Community", "Belonging and shared rituals");
            TopicEntity conflict = saveTopic(topicRepository, "conflict", "Conflict", "Repair after disagreement");

            join(userTopicRepository, user1.getId(), empathy.getId());
            join(userTopicRepository, user1.getId(), community.getId());
            join(userTopicRepository, user2.getId(), empathy.getId());
            join(userTopicRepository, user2.getId(), boundaries.getId());
            join(userTopicRepository, user3.getId(), community.getId());
            join(userTopicRepository, user3.getId(), conflict.getId());
            join(userTopicRepository, user4.getId(), empathy.getId());
            join(userTopicRepository, user4.getId(), conflict.getId());
            join(userTopicRepository, admin.getId(), community.getId());

            follow(followRepository, user1.getId(), user2.getId());
            follow(followRepository, user1.getId(), user3.getId());
            follow(followRepository, user2.getId(), user1.getId());
            follow(followRepository, user3.getId(), user4.getId());
            follow(followRepository, user4.getId(), user1.getId());
            follow(followRepository, user4.getId(), user2.getId());

            interact(interactionRepository, user1.getId(), empathy.getId(), InteractionType.LIKE, "seed-like");
            interact(interactionRepository, user2.getId(), empathy.getId(), InteractionType.COMMENT, "seed-comment");
            interact(interactionRepository, user3.getId(), community.getId(), InteractionType.LIKE, "seed-like");
            interact(interactionRepository, user4.getId(), conflict.getId(), InteractionType.COMMENT, "seed-comment");
            interact(interactionRepository, user1.getId(), community.getId(), InteractionType.LIKE, "seed-like");

            log.info("Seeded RelateAI users={}, topics={}", 5, 4);
            log.info("Login: user1@relateai.demo / password  |  admin@relateai.demo / password");
        };
    }

    private UserEntity saveUser(UserRepository repo, String email, String name, Role role, String hash) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setDisplayName(name);
        user.setRole(role);
        user.setPasswordHash(hash);
        user.setActive(true);
        return repo.save(user);
    }

    private TopicEntity saveTopic(TopicRepository repo, String slug, String name, String description) {
        TopicEntity topic = new TopicEntity();
        topic.setSlug(slug);
        topic.setName(name);
        topic.setDescription(description);
        return repo.save(topic);
    }

    private void join(UserTopicRepository repo, Long userId, Long topicId) {
        UserTopicEntity membership = new UserTopicEntity();
        membership.setUserId(userId);
        membership.setTopicId(topicId);
        repo.save(membership);
    }

    private void follow(FollowRepository repo, Long followerId, Long followeeId) {
        FollowEntity edge = new FollowEntity();
        edge.setFollowerId(followerId);
        edge.setFolloweeId(followeeId);
        repo.save(edge);
    }

    private void interact(
            InteractionRepository repo,
            Long actorId,
            Long topicId,
            InteractionType type,
            String payload) {
        InteractionEntity interaction = new InteractionEntity();
        interaction.setActorId(actorId);
        interaction.setTopicId(topicId);
        interaction.setType(type);
        interaction.setPayload(payload);
        repo.save(interaction);
    }
}
