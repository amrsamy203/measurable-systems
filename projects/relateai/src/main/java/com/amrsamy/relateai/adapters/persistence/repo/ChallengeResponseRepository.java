package com.amrsamy.relateai.adapters.persistence.repo;

import com.amrsamy.relateai.adapters.persistence.entity.ChallengeResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeResponseRepository extends JpaRepository<ChallengeResponseEntity, Long> {
    List<ChallengeResponseEntity> findByChallengeIdOrderByCreatedAtDesc(Long challengeId);
    Optional<ChallengeResponseEntity> findByChallengeIdAndUserId(Long challengeId, Long userId);
}
