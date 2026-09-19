package com.amrsamy.relateai.adapters.web;

import com.amrsamy.relateai.adapters.persistence.entity.TopicEntity;
import com.amrsamy.relateai.adapters.persistence.entity.UserTopicEntity;
import com.amrsamy.relateai.adapters.web.dto.ApiDtos;
import com.amrsamy.relateai.adapters.web.security.RelateAiUserPrincipal;
import com.amrsamy.relateai.application.service.SocialGraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final SocialGraphService socialGraphService;

    public TopicController(SocialGraphService socialGraphService) {
        this.socialGraphService = socialGraphService;
    }

    @GetMapping
    public List<ApiDtos.TopicResponse> listTopics() {
        return socialGraphService.listTopics().stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Map<String, Object>> join(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @PathVariable("id") Long topicId) {
        UserTopicEntity membership = socialGraphService.joinTopic(principal.getId(), topicId);
        return ResponseEntity.ok(Map.of(
                "id", membership.getId(),
                "userId", membership.getUserId(),
                "topicId", membership.getTopicId()));
    }

    private ApiDtos.TopicResponse toDto(TopicEntity topic) {
        return new ApiDtos.TopicResponse(
                topic.getId(),
                topic.getSlug(),
                topic.getName(),
                topic.getDescription());
    }
}
