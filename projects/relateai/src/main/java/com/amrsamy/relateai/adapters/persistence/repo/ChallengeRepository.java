package com.amrsamy.relateai.adapters.persistence.repo;

import com.amrsamy.relateai.adapters.persistence.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    Optional<ChallengeEntity> findByChallengeDate(LocalDate challengeDate);
}
