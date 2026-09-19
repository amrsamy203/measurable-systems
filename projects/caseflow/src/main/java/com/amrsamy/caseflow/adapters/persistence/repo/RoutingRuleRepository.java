package com.amrsamy.caseflow.adapters.persistence.repo;

import com.amrsamy.caseflow.adapters.persistence.entity.RoutingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutingRuleRepository extends JpaRepository<RoutingRuleEntity, Long> {
    List<RoutingRuleEntity> findByTenantIdAndActiveTrue(Long tenantId);
}
