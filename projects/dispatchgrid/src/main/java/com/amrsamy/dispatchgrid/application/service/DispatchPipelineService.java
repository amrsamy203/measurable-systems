package com.amrsamy.dispatchgrid.application.service;

import com.amrsamy.dispatchgrid.adapters.metrics.ThroughputMetricsCollector;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.DeliveryReceiptEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.DispatchMessageEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.OutboxMessageEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.CampaignRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.DeliveryReceiptRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.DispatchMessageRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.OutboxMessageRepository;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.TenantRepository;
import com.amrsamy.dispatchgrid.adapters.provider.MessageProvider;
import com.amrsamy.dispatchgrid.adapters.provider.ProviderRegistry;
import com.amrsamy.dispatchgrid.adapters.provider.ProviderSendRequest;
import com.amrsamy.dispatchgrid.adapters.provider.ProviderSendResult;
import com.amrsamy.dispatchgrid.domain.model.CampaignStatus;
import com.amrsamy.dispatchgrid.domain.model.MessageStatus;
import com.amrsamy.dispatchgrid.domain.model.OutboxStatus;
import com.amrsamy.dispatchgrid.domain.service.TokenBucketRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DispatchPipelineService {

    private static final Logger log = LoggerFactory.getLogger(DispatchPipelineService.class);

    private final OutboxMessageRepository outboxRepository;
    private final DispatchMessageRepository messageRepository;
    private final CampaignRepository campaignRepository;
    private final TenantRepository tenantRepository;
    private final DeliveryReceiptRepository receiptRepository;
    private final ProviderRegistry providerRegistry;
    private final ThroughputMetricsCollector metrics;
    private final int maxAttempts;
    private final long baseBackoffMs;

    public DispatchPipelineService(
            OutboxMessageRepository outboxRepository,
            DispatchMessageRepository messageRepository,
            CampaignRepository campaignRepository,
            TenantRepository tenantRepository,
            DeliveryReceiptRepository receiptRepository,
            ProviderRegistry providerRegistry,
            ThroughputMetricsCollector metrics,
            @Value("${dispatchgrid.dispatch.max-attempts:3}") int maxAttempts,
            @Value("${dispatchgrid.dispatch.base-backoff-ms:200}") long baseBackoffMs) {
        this.outboxRepository = outboxRepository;
        this.messageRepository = messageRepository;
        this.campaignRepository = campaignRepository;
        this.tenantRepository = tenantRepository;
        this.receiptRepository = receiptRepository;
        this.providerRegistry = providerRegistry;
        this.metrics = metrics;
        this.maxAttempts = maxAttempts;
        this.baseBackoffMs = baseBackoffMs;
    }

    @Transactional
    public void processOutbox(Long outboxId) {
        OutboxMessageEntity outbox = outboxRepository.findById(outboxId).orElse(null);
        if (outbox == null || outbox.getStatus() == OutboxStatus.COMPLETED) {
            return;
        }

        outbox.setStatus(OutboxStatus.PROCESSING);
        outboxRepository.save(outbox);

        DispatchMessageEntity message = messageRepository.findById(outbox.getMessageId()).orElse(null);
        if (message == null) {
            outbox.setStatus(OutboxStatus.FAILED);
            outbox.setLastError("message missing");
            outbox.setProcessedAt(Instant.now());
            outboxRepository.save(outbox);
            return;
        }

        if (message.getStatus() == MessageStatus.DELIVERED
                || message.getStatus() == MessageStatus.FAILED_DLQ
                || message.getStatus() == MessageStatus.SENT) {
            outbox.setStatus(OutboxStatus.COMPLETED);
            outbox.setProcessedAt(Instant.now());
            outboxRepository.save(outbox);
            return;
        }

        MessageProvider provider = providerRegistry.resolve(message.getChannel());
        TokenBucketRateLimiter limiter = providerRegistry.limiterFor(provider.key());

        int waitLoops = 0;
        while (!limiter.tryAcquire() && waitLoops < 40) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            waitLoops++;
        }
        if (waitLoops >= 40) {
            // Re-queue: leave pending for next poll
            outbox.setStatus(OutboxStatus.PENDING);
            outbox.setLastError("rate-limited");
            outboxRepository.save(outbox);
            message.setStatus(MessageStatus.QUEUED);
            messageRepository.save(message);
            return;
        }

        message.setStatus(MessageStatus.SENDING);
        message.setProviderKey(provider.key());
        message.setAttemptCount(message.getAttemptCount() + 1);
        messageRepository.save(message);

        long start = System.currentTimeMillis();
        ProviderSendResult result = provider.send(new ProviderSendRequest(
                message.getId(),
                message.getChannel(),
                message.getDestination(),
                message.getBody()));
        long latency = System.currentTimeMillis() - start;

        if (result.success()) {
            message.setStatus(MessageStatus.SENT);
            message.setProviderMessageId(result.providerMessageId());
            message.setSentAt(Instant.now());
            message.setLastError(null);
            messageRepository.save(message);

            outbox.setStatus(OutboxStatus.COMPLETED);
            outbox.setProcessedAt(Instant.now());
            outbox.setLastError(null);
            outboxRepository.save(outbox);

            incrementCampaignSent(message.getCampaignId());
            incrementTenantUsage(message.getTenantId());
            metrics.recordSuccess(latency);

            simulateDeliveryReceipt(message, result.providerMessageId());
            maybeCompleteCampaign(message.getCampaignId());
            return;
        }

        metrics.recordFailure();
        message.setLastError(result.error());

        if (message.getAttemptCount() >= maxAttempts) {
            message.setStatus(MessageStatus.FAILED_DLQ);
            messageRepository.save(message);

            outbox.setStatus(OutboxStatus.FAILED);
            outbox.setProcessedAt(Instant.now());
            outbox.setLastError(result.error());
            outboxRepository.save(outbox);

            incrementCampaignFailed(message.getCampaignId());
            maybeCompleteCampaign(message.getCampaignId());
            log.warn("Message {} moved to FAILED_DLQ after {} attempts: {}",
                    message.getId(), message.getAttemptCount(), result.error());
            return;
        }

        // Backoff then re-queue
        long backoff = baseBackoffMs * (1L << Math.min(message.getAttemptCount() - 1, 4));
        try {
            Thread.sleep(backoff);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        message.setStatus(MessageStatus.QUEUED);
        messageRepository.save(message);
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setLastError(result.error());
        outboxRepository.save(outbox);
    }

    private void simulateDeliveryReceipt(DispatchMessageEntity message, String providerMessageId) {
        // ~90% delivered, ~10% failed receipt
        boolean delivered = ThreadLocalRandom.current().nextInt(100) < 90;
        MessageStatus receiptStatus = delivered ? MessageStatus.DELIVERED : MessageStatus.FAILED;

        DeliveryReceiptEntity receipt = new DeliveryReceiptEntity();
        receipt.setMessageId(message.getId());
        receipt.setProviderMessageId(providerMessageId);
        receipt.setStatus(receiptStatus);
        receipt.setDetail(delivered ? "simulated delivery" : "simulated provider bounce");
        receipt.setReceivedAt(Instant.now());
        receiptRepository.save(receipt);

        if (delivered) {
            message.setStatus(MessageStatus.DELIVERED);
            message.setDeliveredAt(Instant.now());
        } else {
            message.setStatus(MessageStatus.FAILED);
            message.setLastError("delivery receipt FAILED");
        }
        messageRepository.save(message);
    }

    private void incrementCampaignSent(Long campaignId) {
        campaignRepository.findById(campaignId).ifPresent(c -> {
            c.setSentCount(c.getSentCount() + 1);
            campaignRepository.save(c);
        });
    }

    private void incrementCampaignFailed(Long campaignId) {
        campaignRepository.findById(campaignId).ifPresent(c -> {
            c.setFailedCount(c.getFailedCount() + 1);
            campaignRepository.save(c);
        });
    }

    private void incrementTenantUsage(Long tenantId) {
        tenantRepository.findById(tenantId).ifPresent(t -> {
            t.setUsedToday(t.getUsedToday() + 1);
            tenantRepository.save(t);
        });
    }

    private void maybeCompleteCampaign(Long campaignId) {
        CampaignEntity campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign == null || campaign.getStatus() != CampaignStatus.RUNNING) {
            return;
        }
        long terminal = messageRepository.countByCampaignIdAndStatusIn(
                campaignId,
                List.copyOf(EnumSet.of(
                        MessageStatus.DELIVERED,
                        MessageStatus.FAILED,
                        MessageStatus.FAILED_DLQ,
                        MessageStatus.SENT)));
        if (terminal >= campaign.getRecipientCount()) {
            campaign.setStatus(CampaignStatus.COMPLETED);
            campaign.setCompletedAt(Instant.now());
            campaignRepository.save(campaign);
        }
    }
}
