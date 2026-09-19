package com.amrsamy.dispatchgrid.adapters.web.dto;

import com.amrsamy.dispatchgrid.domain.model.Channel;
import com.amrsamy.dispatchgrid.domain.model.CampaignStatus;
import com.amrsamy.dispatchgrid.domain.model.MessageStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ApiDtos {

    private ApiDtos() {
    }

    public record LoginRequest(
            @NotBlank String email,
            @NotBlank String password
    ) {
    }

    public record LoginResponse(
            String token,
            String email,
            String role,
            String displayName,
            Long tenantId
    ) {
    }

    public record CreateCampaignRequest(
            @NotBlank String name,
            @NotNull Channel channel,
            String bodyTemplate
    ) {
    }

    public record AddRecipientsRequest(
            List<String> destinations
    ) {
    }

    public record GenerateRecipientsRequest(
            @Min(1) @Max(5000) int count
    ) {
    }

    public record CampaignResponse(
            Long id,
            String name,
            Channel channel,
            CampaignStatus status,
            int recipientCount,
            int sentCount,
            int failedCount,
            Instant createdAt,
            Instant startedAt,
            Instant completedAt
    ) {
    }

    public record MessageResponse(
            Long id,
            Long campaignId,
            Channel channel,
            String destination,
            MessageStatus status,
            String providerKey,
            String providerMessageId,
            int attemptCount,
            String lastError,
            Instant sentAt,
            Instant deliveredAt
    ) {
    }

    public record SimulateCampaignRequest(
            @NotNull Channel channel,
            @Min(1) @Max(5000) int count
    ) {
    }

    public record FailureInjectionRequest(
            String providerKey,
            @Min(0) long slowMs,
            @Min(0) @Max(100) int failPercent
    ) {
    }

    public record ThroughputResponse(
            long messagesSent,
            double sentPerSecond,
            long p95LatencyMs,
            long queueDepth,
            long failureCount,
            Instant lastUpdated
    ) {
    }

    public record SendMessageRequest(
            @NotNull Channel channel,
            @NotBlank String destination,
            @NotBlank String body,
            String campaignName
    ) {
    }

    public record SendMessageResponse(
            Long campaignId,
            Long messageId,
            String status
    ) {
    }

    public record ArchitectureResponse(
            String product,
            List<Map<String, String>> components,
            List<Map<String, String>> adrs,
            Map<String, Object> runtime
    ) {
    }

    public record ErrorResponse(String error, String detail) {
    }
}
