package com.amrsamy.dispatchgrid.application.service;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.domain.model.Channel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoSimulationService {

    private final CampaignApplicationService campaignService;

    public DemoSimulationService(CampaignApplicationService campaignService) {
        this.campaignService = campaignService;
    }

    @Transactional
    public CampaignEntity simulateCampaign(Long tenantId, Channel channel, int count) {
        String name = "Simulate " + channel + " x" + count + " @ " + System.currentTimeMillis();
        CampaignEntity campaign = campaignService.createCampaign(tenantId, name, channel, null);
        campaignService.generateSyntheticRecipients(campaign.getId(), count);
        return campaignService.startCampaign(campaign.getId());
    }
}
