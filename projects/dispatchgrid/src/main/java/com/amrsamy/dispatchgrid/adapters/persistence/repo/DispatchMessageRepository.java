package com.amrsamy.dispatchgrid.adapters.persistence.repo;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.DispatchMessageEntity;
import com.amrsamy.dispatchgrid.domain.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DispatchMessageRepository extends JpaRepository<DispatchMessageEntity, Long> {

    List<DispatchMessageEntity> findByCampaignIdOrderByIdAsc(Long campaignId);

    long countByStatus(MessageStatus status);

    long countByCampaignIdAndStatus(Long campaignId, MessageStatus status);

    long countByCampaignIdAndStatusIn(Long campaignId, List<MessageStatus> statuses);

    Optional<DispatchMessageEntity> findByProviderMessageId(String providerMessageId);

    @Query("select count(m) from DispatchMessageEntity m where m.status in ('PENDING','QUEUED','SENDING')")
    long countInFlight();
}
