package com.amrsamy.relateai.application.service;

import com.amrsamy.relateai.adapters.metrics.MetricsCollector;
import com.amrsamy.relateai.adapters.persistence.entity.ChallengeEntity;
import com.amrsamy.relateai.adapters.persistence.entity.ChallengeResponseEntity;
import com.amrsamy.relateai.adapters.persistence.entity.InteractionEntity;
import com.amrsamy.relateai.adapters.persistence.repo.ChallengeRepository;
import com.amrsamy.relateai.adapters.persistence.repo.ChallengeResponseRepository;
import com.amrsamy.relateai.adapters.persistence.repo.InteractionRepository;
import com.amrsamy.relateai.application.port.AiClientPort;
import com.amrsamy.relateai.domain.model.InteractionType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeResponseRepository responseRepository;
    private final InteractionRepository interactionRepository;
    private final AiClientPort aiClient;
    private final MetricsCollector metrics;

    public ChallengeService(
            ChallengeRepository challengeRepository,
            ChallengeResponseRepository responseRepository,
            InteractionRepository interactionRepository,
            AiClientPort aiClient,
            MetricsCollector metrics) {
        this.challengeRepository = challengeRepository;
        this.responseRepository = responseRepository;
        this.interactionRepository = interactionRepository;
        this.aiClient = aiClient;
        this.metrics = metrics;
    }

    @Transactional
    public ChallengeEntity getOrCreateToday() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return challengeRepository.findByChallengeDate(today).orElseGet(() -> {
            AiClientPort.GeneratedChallenge generated = aiClient.generateDailyChallenge(today.toString());
            ChallengeEntity entity = new ChallengeEntity();
            entity.setChallengeDate(today);
            entity.setPrompt(generated.prompt());
            entity.setSource(generated.source());
            ChallengeEntity saved = challengeRepository.save(entity);
            metrics.incChallengesGenerated();
            return saved;
        });
    }

    @Transactional
    public ChallengeResponseEntity submitResponse(Long challengeId, Long userId, String body) {
        if (body == null || body.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response body is required");
        }
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));

        ChallengeResponseEntity response = responseRepository
                .findByChallengeIdAndUserId(challengeId, userId)
                .orElseGet(ChallengeResponseEntity::new);
        response.setChallengeId(challenge.getId());
        response.setUserId(userId);
        response.setBody(body.trim());
        ChallengeResponseEntity saved = responseRepository.save(response);

        InteractionEntity interaction = new InteractionEntity();
        interaction.setActorId(userId);
        interaction.setType(InteractionType.CHALLENGE_RESPONSE);
        interaction.setPayload("challengeId=" + challengeId);
        interactionRepository.save(interaction);
        metrics.incInteractions();
        metrics.incChallengeResponses();
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponseEntity> listResponses(Long challengeId) {
        if (!challengeRepository.existsById(challengeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found");
        }
        return responseRepository.findByChallengeIdOrderByCreatedAtDesc(challengeId);
    }
}
