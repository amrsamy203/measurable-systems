package com.amrsamy.caseflow.adapters.persistence.repo;

import com.amrsamy.caseflow.adapters.persistence.entity.CaseEntity;
import com.amrsamy.caseflow.domain.model.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {

    List<CaseEntity> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<CaseEntity> findByTenantIdAndStatusOrderByCreatedAtDesc(Long tenantId, CaseStatus status);

    Optional<CaseEntity> findByIdAndTenantId(Long id, Long tenantId);

    long countByTenantIdAndQueueNameAndStatusIn(Long tenantId, String queueName, List<CaseStatus> statuses);

    @Query("select c.queueName, count(c) from CaseEntity c where c.tenantId = :tenantId and c.queueName is not null and c.status in :statuses group by c.queueName")
    List<Object[]> countByQueueGrouped(@Param("tenantId") Long tenantId, @Param("statuses") List<CaseStatus> statuses);

    long countByTenantIdAndAssigneeIdAndStatusIn(Long tenantId, Long assigneeId, List<CaseStatus> statuses);
}
