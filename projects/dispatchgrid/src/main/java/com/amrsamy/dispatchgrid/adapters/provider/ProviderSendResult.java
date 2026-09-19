package com.amrsamy.dispatchgrid.adapters.provider;

public record ProviderSendResult(
        boolean success,
        String providerMessageId,
        String error
) {
    public static ProviderSendResult ok(String providerMessageId) {
        return new ProviderSendResult(true, providerMessageId, null);
    }

    public static ProviderSendResult fail(String error) {
        return new ProviderSendResult(false, null, error);
    }
}
