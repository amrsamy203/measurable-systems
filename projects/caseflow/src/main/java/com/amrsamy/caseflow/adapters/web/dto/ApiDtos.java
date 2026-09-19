package com.amrsamy.caseflow.adapters.web.dto;

import com.amrsamy.caseflow.domain.model.Priority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public final class ApiDtos {

    private ApiDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record LoginResponse(
            String token,
            String email,
            String role,
            Long userId,
            Long tenantId,
            String displayName
    ) {
    }

    public record IngestEventRequest(
            String externalRef,
            @NotBlank String title,
            String description,
            Priority priority,
            Set<String> skills
    ) {
    }

    public record TransitionRequest(
            @NotBlank String status
    ) {
    }

    public record SimulateRequest(Integer count) {
    }

    public record FailureInjectionRequest(Long slowProcessingMs, Integer failurePercent) {
    }

    public record CaseResponse(
            Long id,
            String externalRef,
            String title,
            String description,
            String priority,
            String status,
            String queueName,
            Long assigneeId,
            String assigneeName,
            Set<String> skills,
            String slaDeadline,
            String createdAt,
            String updatedAt
    ) {
    }

    public record CaseDetailResponse(
            CaseResponse caseInfo,
            java.util.List<AuditResponse> auditTrail
    ) {
    }

    public record AuditResponse(
            Long id,
            String action,
            Long actorId,
            String payloadJson,
            String createdAt
    ) {
    }

    public record AgentResponse(
            Long id,
            String displayName,
            String email,
            Set<String> skills,
            boolean active,
            String lastAssignedAt,
            long openCaseCount
    ) {
    }

    public record QueueResponse(
            String name,
            long depth
    ) {
    }

    public record ThroughputResponse(
            long eventsProcessed,
            double eventsPerSecond,
            long p95LatencyMs,
            long queueDepth,
            long failureCount,
            String lastUpdated
    ) {
    }

    public record SimulateResponse(int enqueued, java.util.List<Long> caseIds) {
    }

    public record MessageResponse(String message) {
    }
}
