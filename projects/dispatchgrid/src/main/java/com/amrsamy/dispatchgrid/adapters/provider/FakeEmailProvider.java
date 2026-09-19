package com.amrsamy.dispatchgrid.adapters.provider;

import com.amrsamy.dispatchgrid.adapters.metrics.FailureInjectionState;
import com.amrsamy.dispatchgrid.domain.model.Channel;
import org.springframework.stereotype.Component;

@Component
public class FakeEmailProvider extends AbstractFakeProvider {

    public static final String KEY = "FAKE_EMAIL";

    public FakeEmailProvider(FailureInjectionState failureInjection) {
        super(KEY, Channel.EMAIL, failureInjection, 5);
    }
}
