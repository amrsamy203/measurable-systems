package com.amrsamy.caseflow.adapters.web;

import com.amrsamy.caseflow.adapters.persistence.entity.AuditEventEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.CaseEntity;
import com.amrsamy.caseflow.adapters.persistence.entity.UserEntity;
import com.amrsamy.caseflow.adapters.persistence.repo.CaseRepository;
import com.amrsamy.caseflow.adapters.persistence.repo.UserRepository;
import com.amrsamy.caseflow.adapters.web.dto.ApiDtos;
import com.amrsamy.caseflow.adapters.web.security.CaseFlowUserPrincipal;
import com.amrsamy.caseflow.application.service.CaseApplicationService;
import com.amrsamy.caseflow.application.service.CasePipelineService;
import com.amrsamy.caseflow.domain.model.CaseStatus;
import com.amrsamy.caseflow.domain.model.Priority;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CaseController {

    private final CaseApplicationService caseApplicationService;
    private final CasePipelineService casePipelineService;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;

    public CaseController(
            CaseApplicationService caseApplicationService,
            CasePipelineService casePipelineService,
            UserRepository userRepository,
            CaseRepository caseRepository) {
        this.caseApplicationService = caseApplicationService;
        this.casePipelineService = casePipelineService;
        this.userRepository = userRepository;
        this.caseRepository = caseRepository;
    }

    @PostMapping("/events")
    public ResponseEntity<ApiDtos.CaseResponse> ingest(
            @AuthenticationPrincipal CaseFlowUserPrincipal principal,
            @Valid @RequestBody ApiDtos.IngestEventRequest request) {

        CaseEntity created = caseApplicationService.ingestEvent(
                principal.getTenantId(),
                request.externalRef(),
                request.title(),
                request.description(),
                request.priority() != null ? request.priority() : Priority.MEDIUM,
                request.skills(),
                principal.getId());
        caseApplicationService.enqueuePipeline(created.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toCaseResponse(created, Map.of()));
    }

    @GetMapping("/cases")
    public List<ApiDtos.CaseResponse> listCases(
            @AuthenticationPrincipal CaseFlowUserPrincipal principal,
            @RequestParam(required = false) String status) {

        CaseStatus filter = status != null ? CaseStatus.valueOf(status.toUpperCase()) : null;
        List<CaseEntity> cases = caseApplicationService.listCases(principal.getTenantId(), filter);
        Map<Long, String> names = agentNames(principal.getTenantId());
        return cases.stream().map(c -> toCaseResponse(c, names)).toList();
    }

    @GetMapping("/cases/{id}")
    public ApiDtos.CaseDetailResponse getCase(
            @AuthenticationPrincipal CaseFlowUserPrincipal principal,
            @PathVariable Long id) {

        CaseEntity entity = caseApplicationService.getCase(principal.getTenantId(), id);
        List<AuditEventEntity> audit = caseApplicationService.getAuditTrail(id);
        Map<Long, String> names = agentNames(principal.getTenantId());

        List<ApiDtos.AuditResponse> trail = audit.stream()
                .map(a -> new ApiDtos.AuditResponse(
                        a.getId(),
                        a.getAction(),
                        a.getActorId(),
                        a.getPayloadJson(),
                        format(a.getCreatedAt())))
                .toList();

        return new ApiDtos.CaseDetailResponse(toCaseResponse(entity, names), trail);
    }

    @PostMapping("/cases/{id}/transition")
    public ApiDtos.CaseResponse transition(
            @AuthenticationPrincipal CaseFlowUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ApiDtos.TransitionRequest request) {

        CaseStatus target = CaseStatus.valueOf(request.status().trim().toUpperCase());
        CaseEntity updated = caseApplicationService.transition(
                principal.getTenantId(), id, target, principal.getId());
        return toCaseResponse(updated, agentNames(principal.getTenantId()));
    }

    @GetMapping("/agents")
    public List<ApiDtos.AgentResponse> agents(@AuthenticationPrincipal CaseFlowUserPrincipal principal) {
        List<UserEntity> agents = caseApplicationService.listAgents(principal.getTenantId());
        List<CaseStatus> open = List.of(
                CaseStatus.ASSIGNED, CaseStatus.IN_PROGRESS, CaseStatus.QUEUED);

        return agents.stream().map(a -> new ApiDtos.AgentResponse(
                a.getId(),
                a.getDisplayName(),
                a.getEmail(),
                a.getSkills(),
                a.isActive(),
                format(a.getLastAssignedAt()),
                caseRepository.countByTenantIdAndAssigneeIdAndStatusIn(
                        principal.getTenantId(), a.getId(), open)
        )).toList();
    }

    @GetMapping("/queues")
    public List<ApiDtos.QueueResponse> queues(@AuthenticationPrincipal CaseFlowUserPrincipal principal) {
        Map<String, Long> depths = casePipelineService.queueDepths(principal.getTenantId());
        return depths.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ApiDtos.QueueResponse(e.getKey(), e.getValue()))
                .toList();
    }

    private Map<Long, String> agentNames(Long tenantId) {
        return userRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getDisplayName, (a, b) -> a));
    }

    private ApiDtos.CaseResponse toCaseResponse(CaseEntity c, Map<Long, String> names) {
        return new ApiDtos.CaseResponse(
                c.getId(),
                c.getExternalRef(),
                c.getTitle(),
                c.getDescription(),
                c.getPriority() != null ? c.getPriority().name() : null,
                c.getStatus() != null ? c.getStatus().name() : null,
                c.getQueueName(),
                c.getAssigneeId(),
                c.getAssigneeId() != null ? names.get(c.getAssigneeId()) : null,
                c.getSkills(),
                format(c.getSlaDeadline()),
                format(c.getCreatedAt()),
                format(c.getUpdatedAt()));
    }

    private String format(Instant instant) {
        return instant == null ? null : instant.toString();
    }
}
