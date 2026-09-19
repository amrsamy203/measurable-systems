package com.amrsamy.dispatchgrid.adapters.persistence.repo;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.DeliveryReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryReceiptRepository extends JpaRepository<DeliveryReceiptEntity, Long> {
    List<DeliveryReceiptEntity> findByMessageIdOrderByReceivedAtDesc(Long messageId);
}
