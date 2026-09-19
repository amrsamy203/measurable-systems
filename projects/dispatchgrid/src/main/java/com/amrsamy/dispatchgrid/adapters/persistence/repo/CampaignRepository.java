package com.amrsamy.dispatchgrid.adapters.persistence.repo;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.domain.model.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {
    List<CampaignEntity> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<CampaignEntity> findByStatus(CampaignStatus status);
}
