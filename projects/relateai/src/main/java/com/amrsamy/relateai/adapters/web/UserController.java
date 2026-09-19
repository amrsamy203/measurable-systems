package com.amrsamy.relateai.adapters.web;

import com.amrsamy.relateai.adapters.persistence.entity.FollowEntity;
import com.amrsamy.relateai.adapters.persistence.entity.UserEntity;
import com.amrsamy.relateai.adapters.web.dto.ApiDtos;
import com.amrsamy.relateai.adapters.web.security.RelateAiUserPrincipal;
import com.amrsamy.relateai.application.service.SocialGraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SocialGraphService socialGraphService;

    public UserController(SocialGraphService socialGraphService) {
        this.socialGraphService = socialGraphService;
    }

    @GetMapping
    public List<ApiDtos.UserResponse> listUsers() {
        return socialGraphService.listUsers().stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<ApiDtos.FollowResponse> follow(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @PathVariable("id") Long followeeId) {
        FollowEntity edge = socialGraphService.follow(principal.getId(), followeeId);
        return ResponseEntity.ok(new ApiDtos.FollowResponse(edge.getId(), edge.getFollowerId(), edge.getFolloweeId()));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @PathVariable("id") Long followeeId) {
        socialGraphService.unfollow(principal.getId(), followeeId);
        return ResponseEntity.noContent().build();
    }

    private ApiDtos.UserResponse toDto(UserEntity user) {
        return new ApiDtos.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name());
    }
}
