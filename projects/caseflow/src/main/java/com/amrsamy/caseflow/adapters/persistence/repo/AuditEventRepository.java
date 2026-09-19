package com.amrsamy.caseflow.adapters.persistence.repo;

import com.amrsamy.caseflow.adapters.persistence.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
    List<AuditEventEntity> findByCaseIdOrderByCreatedAtAsc(Long caseId);
}
