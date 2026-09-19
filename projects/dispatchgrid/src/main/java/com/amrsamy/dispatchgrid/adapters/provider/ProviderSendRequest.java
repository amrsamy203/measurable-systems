package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.domain.model.Channel;

public record ProviderSendRequest(
        Long messageId,
        Channel channel,
        String destination,
        String body
) {
}
