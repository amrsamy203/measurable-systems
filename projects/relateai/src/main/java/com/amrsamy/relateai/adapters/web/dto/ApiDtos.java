package com.amrsamy.relateai.adapters.web.dto;

import com.amrsamy.relateai.domain.model.InteractionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ApiDtos {

    private ApiDtos() {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record LoginResponse(
            String token,
            String email,
            String role,
            Long userId,
            String displayName) {}

    public record UserResponse(Long id, String email, String displayName, String role) {}

    public record TopicResponse(Long id, String slug, String name, String description) {}

    public record FollowResponse(Long id, Long followerId, Long followeeId) {}

    public record InteractionRequest(
            @NotNull InteractionType type,
            Long targetUserId,
            Long topicId,
            String payload) {}

    public record InteractionResponse(
            Long id,
            Long actorId,
            Long targetUserId,
            Long topicId,
            String type,
            String payload) {}

    public record ScoredUserResponse(Long id, String email, String displayName, double score) {}

    public record ScoredTopicResponse(Long id, String slug, String name, double score) {}

    public record ChallengeResponseDto(
            Long id,
            String challengeDate,
            String prompt,
            String source) {}

    public record SubmitChallengeRequest(@NotBlank String body) {}

    public record ChallengeAnswerResponse(
            Long id,
            Long challengeId,
            Long userId,
            String body,
            String createdAt) {}

    public record MediaUploadResponse(
            Long id,
            String publicPath,
            String contentType,
            long sizeBytes) {}

    public record ThroughputResponse(
            long challengesGenerated,
            long interactionsRecorded,
            long followsCreated,
            long topicJoins,
            long challengeResponses,
            long uploads,
            String lastUpdated) {}
}
