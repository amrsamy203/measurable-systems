package com.amrsamy.dispatchgrid.adapters.web;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.adapters.web.dto.ApiDtos;
import com.amrsamy.dispatchgrid.adapters.web.security.ApiKeyAuthenticationFilter;
import com.amrsamy.dispatchgrid.adapters.web.security.DispatchGridUserPrincipal;
import com.amrsamy.dispatchgrid.application.service.CampaignApplicationService;
import com.amrsamy.dispatchgrid.domain.model.MessageStatus;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/send")
public class SendApiController {

    private final CampaignApplicationService campaignService;

    public SendApiController(CampaignApplicationService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public ApiDtos.SendMessageResponse send(
            Authentication authentication,
            @Valid @RequestBody ApiDtos.SendMessageRequest request) {
        Long tenantId = resolveTenantId(authentication);
        String name = request.campaignName() == null || request.campaignName().isBlank()
                ? "API send " + System.currentTimeMillis()
                : request.campaignName();
        CampaignEntity campaign = campaignService.createCampaign(
                tenantId, name, request.channel(), request.body());
        campaignService.addRecipients(campaign.getId(), List.of(request.destination()));
        campaign = campaignService.startCampaign(campaign.getId());
        var messages = campaignService.listMessages(campaign.getId());
        Long messageId = messages.isEmpty() ? null : messages.getFirst().getId();
        String status = messages.isEmpty() ? MessageStatus.QUEUED.name() : messages.getFirst().getStatus().name();
        return new ApiDtos.SendMessageResponse(campaign.getId(), messageId, status);
    }

    private Long resolveTenantId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof ApiKeyAuthenticationFilter.ApiKeyPrincipal api) {
            return api.tenantId();
        }
        if (principal instanceof DispatchGridUserPrincipal user) {
            return user.getTenantId();
        }
        throw new IllegalStateException("Unsupported principal for send API");
    }
}
