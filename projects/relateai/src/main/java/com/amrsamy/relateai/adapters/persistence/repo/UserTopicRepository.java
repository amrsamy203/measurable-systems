package com.amrsamy.relateai.adapters.persistence.repo;

import com.amrsamy.relateai.adapters.persistence.entity.UserTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTopicRepository extends JpaRepository<UserTopicEntity, Long> {
    Optional<UserTopicEntity> findByUserIdAndTopicId(Long userId, Long topicId);
    boolean existsByUserIdAndTopicId(Long userId, Long topicId);
    List<UserTopicEntity> findByUserId(Long userId);
    long countByTopicId(Long topicId);
}
