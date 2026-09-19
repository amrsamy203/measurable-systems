package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.adapters.metrics.FailureInjectionState;
import com.amrsamy.dispatchgrid.domain.model.Channel;
import org.springframework.stereotype.Component;

@Component
public class FakeSmsProvider extends AbstractFakeProvider {

    public static final String KEY = "FAKE_SMS";

    public FakeSmsProvider(FailureInjectionState failureInjection) {
        super(KEY, Channel.SMS, failureInjection, 8);
    }
}
