package com.amrsamy.dispatchgrid.adapters.persistence.repo;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.OutboxMessageEntity;
import com.amrsamy.dispatchgrid.domain.model.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, Long> {

    List<OutboxMessageEntity> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);

    long countByStatus(OutboxStatus status);

    @Query("select count(o) from OutboxMessageEntity o where o.status = 'PENDING'")
    long countPending();
}
