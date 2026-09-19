package com.amrsamy.caseflow.adapters.persistence.repo;

import com.amrsamy.caseflow.adapters.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<TenantEntity, Long> {
    Optional<TenantEntity> findByName(String name);
}
