package com.amrsamy.caseflow.application.service;

import com.amrsamy.caseflow.adapters.persistence.entity.AuditEventEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.CaseEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.UserEntity;
import com.amrsamy.caseflow.adapters.persistence.repo.AuditEventRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.CaseRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.UserRepository;
import com.amrsamy.caseflow.application.port.EventIngestPort;
import com.amrsamy.caseflow.domain.model.CaseStatus;
import com.amrsamy.caseflow.domain.model.Priority;
import com.amrsamy.caseflow.domain.model.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CaseApplicationService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final AuditEventRepository auditEventRepository;
    private final EventIngestPort eventIngestPort;
    private final ObjectMapper objectMapper;

    public CaseApplicationService(
            CaseRepository caseRepository,
            UserRepository userRepository,
            AuditEventRepository auditEventRepository,
            EventIngestPort eventIngestPort,
            ObjectMapper objectMapper) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.auditEventRepository = auditEventRepository;
        this.eventIngestPort = eventIngestPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CaseEntity ingestEvent(
            Long tenantId,
            String externalRef,
            String title,
            String description,
            Priority priority,
            Set<String> skills,
            Long actorId) {

        CaseEntity entity = new CaseEntity();
        entity.setTenantId(tenantId);
        entity.setExternalRef(externalRef != null && !externalRef.isBlank()
                ? externalRef
                : "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setPriority(priority != null ? priority : Priority.MEDIUM);
        entity.setSkills(skills != null ? new HashSet<>(skills) : new HashSet<>());
        entity.setStatus(CaseStatus.NEW);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        CaseEntity saved = caseRepository.save(entity);

        audit(saved.getId(), actorId, "CREATED", Map.of(
                "externalRef", saved.getExternalRef(),
                "priority", saved.getPriority().name()));

        return saved;
    }

    public void enqueuePipeline(Long caseId) {
        eventIngestPort.enqueueCase(caseId);
    }

    @Transactional(readOnly = true)
    public List<CaseEntity> listCases(Long tenantId, CaseStatus status) {
        if (status != null) {
            return caseRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, status);
        }
        return caseRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional(readOnly = true)
    public CaseEntity getCase(Long tenantId, Long id) {
        return caseRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<AuditEventEntity> getAuditTrail(Long caseId) {
        return auditEventRepository.findByCaseIdOrderByCreatedAtAsc(caseId);
    }

    @Transactional
    public CaseEntity transition(Long tenantId, Long caseId, CaseStatus target, Long actorId) {
        CaseEntity entity = getCase(tenantId, caseId);
        CaseStatus current = entity.getStatus();
        if (!current.canTransitionTo(target)) {
            throw new IllegalArgumentException(
                    "Cannot transition from " + current + " to " + target);
        }
        entity.setStatus(target);
        entity.setUpdatedAt(Instant.now());
        CaseEntity saved = caseRepository.save(entity);

        audit(saved.getId(), actorId, "STATUS_CHANGED", Map.of(
                "from", current.name(),
                "to", target.name()));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> listAgents(Long tenantId) {
        return userRepository.findByTenantIdAndRoleAndActiveTrue(tenantId, Role.AGENT);
    }

    private void audit(Long caseId, Long actorId, String action, Map<String, Object> payload) {
        AuditEventEntity event = new AuditEventEntity();
        event.setCaseId(caseId);
        event.setActorId(actorId);
        event.setAction(action);
        try {
            event.setPayloadJson(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            event.setPayloadJson(payload.toString());
        }
        auditEventRepository.save(event);
    }
}
