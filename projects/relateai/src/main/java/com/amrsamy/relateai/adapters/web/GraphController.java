package com.amrsamy.relateai.adapters.web;

import com.amrsamy.relateai.adapters.persistence.entity.InteractionEntity;
import com.amrsamy.relateai.adapters.web.dto.ApiDtos;
import com.amrsamy.relateai.adapters.web.security.RelateAiUserPrincipal;
import com.amrsamy.relateai.application.service.SocialGraphService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GraphController {

    private final SocialGraphService socialGraphService;

    public GraphController(SocialGraphService socialGraphService) {
        this.socialGraphService = socialGraphService;
    }

    @PostMapping("/interactions")
    public ResponseEntity<ApiDtos.InteractionResponse> recordInteraction(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @Valid @RequestBody ApiDtos.InteractionRequest request) {
        InteractionEntity saved = socialGraphService.recordInteraction(
                principal.getId(),
                request.type(),
                request.targetUserId(),
                request.topicId(),
                request.payload());
        return ResponseEntity.ok(new ApiDtos.InteractionResponse(
                saved.getId(),
                saved.getActorId(),
                saved.getTargetUserId(),
                saved.getTopicId(),
                saved.getType().name(),
                saved.getPayload()));
    }

    @GetMapping("/graph/top-connected")
    public List<ApiDtos.ScoredUserResponse> topConnected(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return socialGraphService.topConnected(limit).stream()
                .map(u -> new ApiDtos.ScoredUserResponse(u.id(), u.email(), u.displayName(), u.score()))
                .toList();
    }

    @GetMapping("/graph/hottest-topics")
    public List<ApiDtos.ScoredTopicResponse> hottestTopics(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return socialGraphService.hottestTopics(limit).stream()
                .map(t -> new ApiDtos.ScoredTopicResponse(t.id(), t.slug(), t.name(), t.score()))
                .toList();
    }

    @GetMapping("/graph/recommendations")
    public List<ApiDtos.ScoredUserResponse> recommendations(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return socialGraphService.recommendations(principal.getId(), limit).stream()
                .map(u -> new ApiDtos.ScoredUserResponse(u.id(), u.email(), u.displayName(), u.score()))
                .toList();
    }
}
