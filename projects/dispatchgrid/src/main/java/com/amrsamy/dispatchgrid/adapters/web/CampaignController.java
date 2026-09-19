package com.amrsamy.dispatchgrid.adapters.web;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.DispatchMessageEntity;
import com.amrsamy.dispatchgrid.adapters.web.dto.ApiDtos;
import com.amrsamy.dispatchgrid.adapters.web.security.ApiKeyAuthenticationFilter;
import com.amrsamy.dispatchgrid.adapters.web.security.DispatchGridUserPrincipal;
import com.amrsamy.dispatchgrid.application.service.CampaignApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignApplicationService campaignService;

    public CampaignController(CampaignApplicationService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public List<ApiDtos.CampaignResponse> list(Authentication authentication) {
        Long tenantId = resolveTenantId(authentication);
        return campaignService.listCampaigns(tenantId).stream().map(this::toDto).toList();
    }

    @PostMapping
    public ApiDtos.CampaignResponse create(
            Authentication authentication,
            @Valid @RequestBody ApiDtos.CreateCampaignRequest request) {
        Long tenantId = resolveTenantId(authentication);
        return toDto(campaignService.createCampaign(
                tenantId, request.name(), request.channel(), request.bodyTemplate()));
    }

    @GetMapping("/{id}")
    public ApiDtos.CampaignResponse get(@PathVariable Long id) {
        return toDto(campaignService.getCampaign(id));
    }

    @PostMapping("/{id}/recipients")
    public ApiDtos.CampaignResponse addRecipients(
            @PathVariable Long id,
            @RequestBody ApiDtos.AddRecipientsRequest request) {
        return toDto(campaignService.addRecipients(id, request.destinations()));
    }

    @PostMapping("/{id}/recipients/generate")
    public ApiDtos.CampaignResponse generate(
            @PathVariable Long id,
            @Valid @RequestBody ApiDtos.GenerateRecipientsRequest request) {
        return toDto(campaignService.generateSyntheticRecipients(id, request.count()));
    }

    @PostMapping("/{id}/start")
    public ApiDtos.CampaignResponse start(@PathVariable Long id) {
        return toDto(campaignService.startCampaign(id));
    }

    @GetMapping("/{id}/messages")
    public List<ApiDtos.MessageResponse> messages(@PathVariable Long id) {
        return campaignService.listMessages(id).stream().map(this::toMessageDto).toList();
    }

    private Long resolveTenantId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof DispatchGridUserPrincipal user) {
            return user.getTenantId();
        }
        if (principal instanceof ApiKeyAuthenticationFilter.ApiKeyPrincipal api) {
            return api.tenantId();
        }
        throw new IllegalStateException("Unsupported principal");
    }

    private ApiDtos.CampaignResponse toDto(CampaignEntity c) {
        return new ApiDtos.CampaignResponse(
                c.getId(),
                c.getName(),
                c.getChannel(),
                c.getStatus(),
                c.getRecipientCount(),
                c.getSentCount(),
                c.getFailedCount(),
                c.getCreatedAt(),
                c.getStartedAt(),
                c.getCompletedAt());
    }

    private ApiDtos.MessageResponse toMessageDto(DispatchMessageEntity m) {
        return new ApiDtos.MessageResponse(
                m.getId(),
                m.getCampaignId(),
                m.getChannel(),
                m.getDestination(),
                m.getStatus(),
                m.getProviderKey(),
                m.getProviderMessageId(),
                m.getAttemptCount(),
                m.getLastError(),
                m.getSentAt(),
                m.getDeliveredAt());
    }
}
