package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.adapters.metrics.FailureInjectionState;
import com.amrsamy.dispatchgrid.domain.model.Channel;
import org.springframework.stereotype.Component;

@Component
public class FakeWebhookProvider extends AbstractFakeProvider {

    public static final String KEY = "FAKE_WEBHOOK";

    public FakeWebhookProvider(FailureInjectionState failureInjection) {
        super(KEY, Channel.WEBHOOK, failureInjection, 6);
    }
}
