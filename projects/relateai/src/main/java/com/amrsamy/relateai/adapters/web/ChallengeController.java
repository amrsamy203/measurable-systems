package com.amrsamy.relateai.adapters.web;

import com.amrsamy.relateai.adapters.persistence.entity.ChallengeEntity;
import com.amrsamy.relateai.adapters.persistence.entity.ChallengeResponseEntity;
import com.amrsamy.relateai.adapters.web.dto.ApiDtos;
import com.amrsamy.relateai.adapters.web.security.RelateAiUserPrincipal;
import com.amrsamy.relateai.application.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/today")
    public ApiDtos.ChallengeResponseDto today() {
        ChallengeEntity challenge = challengeService.getOrCreateToday();
        return toDto(challenge);
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<ApiDtos.ChallengeAnswerResponse> submit(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @PathVariable("id") Long challengeId,
            @Valid @RequestBody ApiDtos.SubmitChallengeRequest request) {
        ChallengeResponseEntity saved =
                challengeService.submitResponse(challengeId, principal.getId(), request.body());
        return ResponseEntity.ok(toAnswerDto(saved));
    }

    @GetMapping("/{id}/responses")
    public List<ApiDtos.ChallengeAnswerResponse> listResponses(@PathVariable("id") Long challengeId) {
        return challengeService.listResponses(challengeId).stream()
                .map(this::toAnswerDto)
                .toList();
    }

    private ApiDtos.ChallengeResponseDto toDto(ChallengeEntity challenge) {
        return new ApiDtos.ChallengeResponseDto(
                challenge.getId(),
                challenge.getChallengeDate().toString(),
                challenge.getPrompt(),
                challenge.getSource());
    }

    private ApiDtos.ChallengeAnswerResponse toAnswerDto(ChallengeResponseEntity response) {
        return new ApiDtos.ChallengeAnswerResponse(
                response.getId(),
                response.getChallengeId(),
                response.getUserId(),
                response.getBody(),
                response.getCreatedAt().toString());
    }
}
