package com.amrsamy.relateai.adapters.persistence.repo;

import com.amrsamy.relateai.adapters.persistence.entity.InteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionRepository extends JpaRepository<InteractionEntity, Long> {
    List<InteractionEntity> findByTopicIdIsNotNull();
}
