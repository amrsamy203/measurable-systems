package com.amrsamy.dispatchgrid.application.service;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.DispatchMessageEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.OutboxMessageEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.TenantEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.CampaignRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.DispatchMessageRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.OutboxMessageRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.TenantRepository;
import com.amrsamy.dispatchgrid.application.port.DispatchPipelinePort;
import com.amrsamy.dispatchgrid.domain.model.CampaignStatus;
import com.amrsamy.dispatchgrid.domain.model.Channel;
import com.amrsamy.dispatchgrid.domain.model.MessageStatus;
import com.amrsamy.dispatchgrid.domain.model.OutboxStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignApplicationService {

    private final CampaignRepository campaignRepository;
    private final DispatchMessageRepository messageRepository;
    private final OutboxMessageRepository outboxRepository;
    private final TenantRepository tenantRepository;
    private final DispatchPipelinePort pipelinePort;

    public CampaignApplicationService(
            CampaignRepository campaignRepository,
            DispatchMessageRepository messageRepository,
            OutboxMessageRepository outboxRepository,
            TenantRepository tenantRepository,
            DispatchPipelinePort pipelinePort) {
        this.campaignRepository = campaignRepository;
        this.messageRepository = messageRepository;
        this.outboxRepository = outboxRepository;
        this.tenantRepository = tenantRepository;
        this.pipelinePort = pipelinePort;
    }

    @Transactional
    public CampaignEntity createCampaign(Long tenantId, String name, Channel channel, String bodyTemplate) {
        CampaignEntity campaign = new CampaignEntity();
        campaign.setTenantId(tenantId);
        campaign.setName(name);
        campaign.setChannel(channel);
        campaign.setBodyTemplate(bodyTemplate == null || bodyTemplate.isBlank()
                ? defaultBody(channel)
                : bodyTemplate);
        campaign.setStatus(CampaignStatus.DRAFT);
        campaign.setCreatedAt(Instant.now());
        return campaignRepository.save(campaign);
    }

    @Transactional
    public CampaignEntity addRecipients(Long campaignId, List<String> destinations) {
        CampaignEntity campaign = requireCampaign(campaignId);
        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Recipients can only be added to DRAFT campaigns");
        }
        List<String> cleaned = destinations == null ? List.of() : destinations.stream()
                .filter(d -> d != null && !d.isBlank())
                .map(String::trim)
                .toList();
        for (String dest : cleaned) {
            DispatchMessageEntity msg = new DispatchMessageEntity();
            msg.setTenantId(campaign.getTenantId());
            msg.setCampaignId(campaign.getId());
            msg.setChannel(campaign.getChannel());
            msg.setDestination(dest);
            msg.setBody(renderBody(campaign.getBodyTemplate(), dest));
            msg.setStatus(MessageStatus.PENDING);
            msg.setCreatedAt(Instant.now());
            messageRepository.save(msg);
        }
        campaign.setRecipientCount(campaign.getRecipientCount() + cleaned.size());
        return campaignRepository.save(campaign);
    }

    @Transactional
    public CampaignEntity generateSyntheticRecipients(Long campaignId, int count) {
        if (count < 1 || count > 5000) {
            throw new IllegalArgumentException("count must be 1..5000");
        }
        CampaignEntity campaign = requireCampaign(campaignId);
        List<String> destinations = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            destinations.add(syntheticDestination(campaign.getChannel(), campaign.getId(), i));
        }
        return addRecipients(campaignId, destinations);
    }

    @Transactional
    public CampaignEntity startCampaign(Long campaignId) {
        CampaignEntity campaign = requireCampaign(campaignId);
        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT campaigns can be started");
        }
        if (campaign.getRecipientCount() <= 0) {
            throw new IllegalStateException("Campaign has no recipients");
        }

        TenantEntity tenant = tenantRepository.findById(campaign.getTenantId())
                .orElseThrow(() -> new IllegalStateException("Tenant not found"));
        long remaining = tenant.getDailyQuota() - tenant.getUsedToday();
        if (campaign.getRecipientCount() > remaining) {
            throw new IllegalStateException("Tenant daily quota exceeded. Remaining=" + remaining);
        }

        List<DispatchMessageEntity> messages = messageRepository.findByCampaignIdOrderByIdAsc(campaignId);
        List<Long> outboxIds = new ArrayList<>();
        for (DispatchMessageEntity msg : messages) {
            if (msg.getStatus() != MessageStatus.PENDING) {
                continue;
            }
            msg.setStatus(MessageStatus.QUEUED);
            messageRepository.save(msg);

            OutboxMessageEntity outbox = new OutboxMessageEntity();
            outbox.setMessageId(msg.getId());
            outbox.setCampaignId(campaignId);
            outbox.setTenantId(campaign.getTenantId());
            outbox.setStatus(OutboxStatus.PENDING);
            outbox.setCreatedAt(Instant.now());
            outbox = outboxRepository.save(outbox);
            outboxIds.add(outbox.getId());
        }

        campaign.setStatus(CampaignStatus.RUNNING);
        campaign.setStartedAt(Instant.now());
        campaignRepository.save(campaign);

        for (Long outboxId : outboxIds) {
            pipelinePort.enqueueMessage(outboxId);
        }
        return campaign;
    }

    @Transactional(readOnly = true)
    public List<CampaignEntity> listCampaigns(Long tenantId) {
        return campaignRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional(readOnly = true)
    public CampaignEntity getCampaign(Long id) {
        return requireCampaign(id);
    }

    @Transactional(readOnly = true)
    public List<DispatchMessageEntity> listMessages(Long campaignId) {
        return messageRepository.findByCampaignIdOrderByIdAsc(campaignId);
    }

    private CampaignEntity requireCampaign(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + id));
    }

    private String defaultBody(Channel channel) {
        return switch (channel) {
            case SMS -> "DispatchGrid alert: {{dest}} — action required.";
            case EMAIL -> "Hello {{dest}}, this is a DispatchGrid campaign email.";
            case WEBHOOK -> "{\"event\":\"dispatch\",\"to\":\"{{dest}}\"}";
        };
    }

    private String renderBody(String template, String dest) {
        return template.replace("{{dest}}", dest);
    }

    private String syntheticDestination(Channel channel, Long campaignId, int index) {
        return switch (channel) {
            case SMS -> "+1555" + String.format("%07d", (campaignId * 1000 + index) % 10_000_000);
            case EMAIL -> "user" + campaignId + "_" + index + "@demo.dispatchgrid.local";
            case WEBHOOK -> "https://hooks.demo.local/c/" + campaignId + "/r/" + index;
        };
    }
}
