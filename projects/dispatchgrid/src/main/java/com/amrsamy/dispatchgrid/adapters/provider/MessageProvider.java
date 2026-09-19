package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.domain.model.Channel;

public interface MessageProvider {
    String key();

    Channel channel();

    ProviderSendResult send(ProviderSendRequest request);
}
